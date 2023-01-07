package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.requiredEnv
import software.amazon.awscdk.App
import software.amazon.awscdk.Aspects
import software.amazon.awscdk.IAspect
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.lambda.CfnFunction
import software.constructs.IConstruct
import java.nio.file.Paths
import kotlin.io.path.div
import kotlin.io.path.exists

// TODO add testing: https://docs.aws.amazon.com/cdk/v2/guide/testing.html
// TODO check RemovalPolicy for prod
class HelloApp : App() {
    init {

        // Directory where app was started
        val appDir = Paths.get(System.getProperty("user.dir"))
        val rootDir = appDir.parent.parent.also { check(it.resolve("aws-cdk").exists()) }
        val appsDir = rootDir / "apps"
        val apiClientDistributions = appsDir / "api-client" / "build" / "distributions"

        val helloStack = HelloStack(
            scope = this,
            id = "HelloStack",
            siteBucketDeploymentSource = apiClientDistributions,
            props = StackProps.builder()
                .requiredEnv(
                    account = "382728805609",
                    region = "us-east-1",
                )
                .build()
        )

        Aspects.of(helloStack).add(LambdaLogGroupConfig())
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
