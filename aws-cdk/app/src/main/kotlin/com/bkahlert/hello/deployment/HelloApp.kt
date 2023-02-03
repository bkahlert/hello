package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.requiredEnv
import software.amazon.awscdk.App
import software.amazon.awscdk.Aspects
import software.amazon.awscdk.Environment
import software.amazon.awscdk.IAspect
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.lambda.CfnFunction
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.s3.deployment.Source
import software.constructs.IConstruct
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.pathString

sealed interface Stage {
    val zoneName: String // TODO make nullable (-> no custom name with Route 53)
    val subDomainName: String

    val cognitoDomainPrefix: String
    val cognitoSignInWithAppleSecretArn: String?
    val cognitoCallbackUrls: List<String>

    val webClientDistribution: Path

    val clickUpCode: Code
    val clickUpSecretArn: String?

    val userInfoCode: Code
    val userPropsCode: Code

    val siteDomain get() = "$subDomainName.$zoneName"
    val siteUrl get() = "https://$siteDomain"

    object DEV : Stage {
        override val zoneName: String = "aws-dev.choam.de"
        override val subDomainName: String = "hello"

        override val cognitoDomainPrefix: String = "hello-dev-bkahlert-com"
        override val cognitoSignInWithAppleSecretArn: String = "arn:aws:secretsmanager:us-east-1:382728805609:secret:dev/hello/SignInWithApple-ZHPZwg"
        override val cognitoCallbackUrls: List<String> = listOf(
            siteUrl,
            "http://localhost:8080",
            "http://localhost:8081",
            "http://localhost:3000",
            "https://example.com",
        )

        private val appDir = Paths.get(System.getProperty("user.dir"))
        private val rootDir = appDir.parent.parent.also { check(it.resolve("aws-cdk").exists()) }

        override val webClientDistribution = rootDir / "apps" / "web-app" / "build" / "distributions"

        override val clickUpCode: Code =
            Code.fromAsset((rootDir / "aws-lambdas" / "clickup-api-handlers" / "build" / "libs" / "clickup-api-handlers-all.jar").pathString)
        override val clickUpSecretArn: String =
            "arn:aws:secretsmanager:us-east-1:382728805609:secret:dev/hello/ClickUp-L2Pnag"

        override val userInfoCode: Code =
            Code.fromAsset((rootDir / "aws-lambdas" / "userinfo-api-handlers" / "build" / "libs" / "userinfo-api-handlers-all.jar").pathString)
        override val userPropsCode: Code =
            Code.fromAsset((rootDir / "aws-lambdas" / "userprops-api-handlers" / "build" / "libs" / "userprops-api-handlers-all.jar").pathString)
    }

    companion object {
        fun from(props: StackProps): Stage? = from(props.env)

        fun from(environment: Environment?): Stage? =
            if (environment?.account == "382728805609" && environment.region == "us-east-1") DEV
            else null
    }
}

// TODO add testing: https://docs.aws.amazon.com/cdk/v2/guide/testing.html
// TODO check RemovalPolicy for prod
class HelloApp : App() {
    init {
        // TODO use stages / multiple HelloApp instances
        val props = StackProps.builder()
            .requiredEnv(
                account = "382728805609",
                region = "us-east-1",
            )
            .build()

        val stage = requireNotNull(Stage.from(props))

        /* Cognito */
        val userPoolProviderStack = UserPoolProviderStack(
            parent = this,
            id = "UserPoolProvider",
            props = props,
            name = stage.siteDomain,
            domainPrefix = stage.cognitoDomainPrefix,
            callbackUrls = stage.cognitoCallbackUrls,
            signInWithAppleSecretArn = stage.cognitoSignInWithAppleSecretArn,
        )

        // CloudWatch role must be specified only once, see https://docs.aws.amazon.com/cdk/api/v2/docs/aws-cdk-lib.aws_apigateway-readme.html#deployments
        // TODO separate cloud watch role; https://docs.aws.amazon.com/cdk/api/v1/docs/aws-logs-readme.html
//    val cloudWatchRole = Cloudwatch

        /* ClickUp API */
        val clickUpStack = stage.clickUpSecretArn?.let {
            ClickUpStack(
                parent = this,
                id = "ClickUp",
                props = props,
                code = stage.clickUpCode,
                secretArn = it,
            )
        }

        /* UserInfo API */
        val userInfoStack = UserInfoStack(
            parent = this,
            id = "UserInfo",
            props = props,
            code = stage.userInfoCode,
            userPool = userPoolProviderStack.userPool,
            userPoolClientId = userPoolProviderStack.userPoolClient.userPoolClientId,
        )

        /* UserProps API */
        val userPropsStack = UserPropsStack(
            parent = this,
            id = "UserProps",
            props = props,
            code = stage.userPropsCode,
            userPool = userPoolProviderStack.userPool
        )

        val siteEnvironmentStack = SiteEnvironmentStack(
            parent = this,
            id = "SiteEnvironment",
            props = props,
            environment = buildMap {
                put("USER_POOL_PROVIDER_URL", userPoolProviderStack.userPool.userPoolProviderUrl) // intrinsic dependency not working here
                put("USER_POOL_CLIENT_ID", userPoolProviderStack.userPoolClient.userPoolClientId) // intrinsic dependency not working here
                clickUpStack?.also { put("CLICK_UP_API_ENDPOINT", "/api/clickup") }
                put("USER_INFO_API_ENDPOINT", "/api/user-info")
                put("USER_PROPS_API_ENDPOINT", "/api/user-props")
            }
        )

        // Directory where app was started
        val distributionStack = DistributionStack(
            parent = this,
            id = "Distribution",
            props = props,
            zoneName = stage.zoneName,
            siteDomain = stage.siteDomain,
            siteUrl = stage.siteUrl,
            apis = buildMap {
                put(siteEnvironmentStack.api, "/environment.json")
                clickUpStack?.also { put(it.api, "/api/clickup*") }
                put(userInfoStack.api, "/api/user-info*")
                put(userPropsStack.api, "/api/user-props*")
            },
            sources = listOf(
                Source.asset(stage.webClientDistribution.pathString),
            ),
            distributionsPaths = stage.webClientDistribution.listDirectoryEntries().map { "/${it.fileName}" }
        )

        Aspects.of(distributionStack).add(LambdaLogGroupConfig())
    }
}

class LambdaLogGroupConfig() : IAspect {

    override fun visit(node: IConstruct) {
        if (node is CfnFunction) {
            createLambdaLogGroup(node);
        }
    }

    private fun createLambdaLogGroup(lambda: CfnFunction) {
        // TODO check retention
    }
}
