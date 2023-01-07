package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.toEnvironment
import software.amazon.awscdk.Duration
import software.amazon.awscdk.services.apigateway.Cors
import software.amazon.awscdk.services.apigateway.CorsOptions
import software.amazon.awscdk.services.apigateway.LambdaIntegration
import software.amazon.awscdk.services.apigateway.RestApi
import software.amazon.awscdk.services.apigateway.StageOptions
import software.amazon.awscdk.services.cognito.UserPool
import software.amazon.awscdk.services.lambda.Architecture
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.Runtime
import software.amazon.awscdk.services.lambda.Tracing
import software.amazon.awscdk.services.logs.RetentionDays
import software.constructs.Construct

class UserInfo(
    /** The scope in which to define this construct. */
    scope: Construct,
    /** The scoped construct ID. */
    id: String,
    /** Used for authorization. */
    userPool: UserPool,
    userPoolClientId: String,
) : Construct(scope, id) {

    val packageName = "com.bkahlert.hello.user.info"

    val corsOptions = CorsOptions.builder()
        .allowCredentials(true)
        .allowHeaders(Cors.DEFAULT_HEADERS)
        .allowMethods(Cors.ALL_METHODS)
        .allowOrigins(Cors.ALL_ORIGINS)
        .build()

    val getFunction = Function.Builder.create(this, "GetFunction")
        .code(Code.fromAsset("../../aws-lambdas/userinfo-api-handlers/build/libs/userinfo-api-handlers-all.jar"))
        .handler("$packageName.GetHandler")
        .architecture(Architecture.ARM_64)
        .runtime(Runtime.JAVA_11)
        .environment(
            mapOf(
                "USER_POOL_PROVIDER_URL" to userPool.userPoolProviderUrl,
                "USER_POOL_CLIENT_ID" to userPoolClientId,
            ) + corsOptions.toEnvironment()
        )
        .timeout(Duration.seconds(30))
        .memorySize(1024)
        .logRetention(RetentionDays.FIVE_DAYS)
        .tracing(Tracing.ACTIVE)
        .build()

    val api = RestApi.Builder.create(this, "RestApi")
        .restApiName("UserInfo API")
        .deployOptions(StageOptions.builder().tracingEnabled(true).build())
        .build()

    init {
        api.root.apply {
            addCorsPreflight(corsOptions)
            addMethod("GET", LambdaIntegration(getFunction))
        }
    }
}
