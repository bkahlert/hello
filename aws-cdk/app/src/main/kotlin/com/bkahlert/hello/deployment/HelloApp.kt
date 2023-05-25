package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.build
import com.bkahlert.aws.cdk.requiredEnv
import software.amazon.awscdk.App
import software.amazon.awscdk.Environment
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.s3.deployment.Source
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.pathString

sealed interface Stage {
    val zoneName: String // TODO make nullable (-> no custom name with Route 53)
    val siteDomain: String
    val siteUrl: String get() = "https://$siteDomain"

    val cognitoDomainPrefix: String
    val cognitoSignInWithAppleSecretArn: String?
    val cognitoCallbackUrls: List<String>

    val webClientDistribution: Path

    val userInfoCode: Code
    val userPropsCode: Code

    object DEV : Stage {
        override val zoneName: String = "aws-dev.choam.de"
        override val siteDomain: String = "hello.$zoneName"

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

        override val userInfoCode: Code =
            Code.fromAsset((rootDir / "aws-lambdas" / "userinfo-api-handlers" / "build" / "libs" / "userinfo-api-handlers-all.jar").pathString)
        override val userPropsCode: Code =
            Code.fromAsset((rootDir / "aws-lambdas" / "userprops-api-handlers" / "build" / "libs" / "userprops-api-handlers-all.jar").pathString)
    }

    object PROD : Stage {
        override val zoneName: String = "hello.bkahlert.com"
        override val siteDomain: String = zoneName

        override val cognitoDomainPrefix: String = "hello-bkahlert-com"
        override val cognitoSignInWithAppleSecretArn: String = "arn:aws:secretsmanager:eu-central-1:709387325224:secret:prod/hello/SignInWithApple-yAznOO"
        override val cognitoCallbackUrls: List<String> = listOf(siteUrl)

        private val appDir = Paths.get(System.getProperty("user.dir"))
        private val rootDir = appDir.parent.parent.also { check(it.resolve("aws-cdk").exists()) }

        override val webClientDistribution = rootDir / "apps" / "web-app" / "build" / "distributions"

        override val userInfoCode: Code =
            Code.fromAsset((rootDir / "aws-lambdas" / "userinfo-api-handlers" / "build" / "libs" / "userinfo-api-handlers-all.jar").pathString)
        override val userPropsCode: Code =
            Code.fromAsset((rootDir / "aws-lambdas" / "userprops-api-handlers" / "build" / "libs" / "userprops-api-handlers-all.jar").pathString)
    }

    companion object {
        fun from(props: StackProps): Stage? = from(props.env)

        fun from(environment: Environment?): Stage? =
            if (environment?.account == "382728805609" && environment.region == "us-east-1") DEV
            else if (environment?.account == "709387325224" && environment.region == "eu-central-1") PROD
            else null
    }
}

// TODO add testing: https://docs.aws.amazon.com/cdk/v2/guide/testing.html
// TODO check RemovalPolicy for prod
// TODO for prod: CDK_DEPLOY_ACCOUNT=709387325224;CDK_DEPLOY_REGION=eu-central-1
class HelloApp : App() {
    init {
        val props = StackProps.builder()
            .requiredEnv()
            .crossRegionReferences(true)
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
                put("USER_INFO_API_ENDPOINT", "/api/user-info")
                put("USER_PROPS_API_ENDPOINT", "/api/user-props")
            }
        )

        val certificateStack = CertificateStack(
            parent = this,
            id = "Certificate",
            props = StackProps.builder().env(Environment.builder().build {
                props.env?.account?.also(::account)
                region("us-east-1")
            }).build(),
            siteDomain = stage.siteDomain,
        )

        // Directory where the app was started
        @Suppress("UNUSED_VARIABLE")
        val distributionStack = DistributionStack(
            parent = this,
            id = "Distribution",
            props = props,
            zoneName = stage.zoneName,
            siteDomain = stage.siteDomain,
            siteUrl = stage.siteUrl,
            siteCertificate = certificateStack.siteCertificate,
            apis = buildMap {
                put(siteEnvironmentStack.api, "/environment.json")
                put(userInfoStack.api, "/api/user-info*")
                put(userPropsStack.api, "/api/user-props*")
            },
            accessControlAllowOrigins = stage.cognitoCallbackUrls,
            sources = listOf(
                Source.asset(stage.webClientDistribution.pathString),
            ),
            distributionsPaths = stage.webClientDistribution.listDirectoryEntries().map { "/${it.fileName}" }
        )
    }
}
