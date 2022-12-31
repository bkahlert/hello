package com.bkahlert.hello.deployment

import software.amazon.awscdk.Duration
import software.amazon.awscdk.services.apigateway.AuthorizationType
import software.amazon.awscdk.services.apigateway.CognitoUserPoolsAuthorizer
import software.amazon.awscdk.services.apigateway.CorsOptions
import software.amazon.awscdk.services.apigateway.LambdaIntegration
import software.amazon.awscdk.services.apigateway.MethodOptions
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

class ClickUp(
    /** The scope in which to define this construct. */
    scope: Construct,
    /** The scoped construct ID. */
    id: String,
    /** Used for authorization. */
    userPool: UserPool,
) : Construct(scope, id) {

    val proxyFunction = Function.Builder.create(this, "ProxyFunction")
        .code(Code.fromAsset("../../aws-lambdas/clickup-handlers/build/libs/clickup-handlers-all.jar"))
        .handler("com.bkahlert.hello.clickup.ProxyHandler")
        .architecture(Architecture.ARM_64)
        .runtime(Runtime.JAVA_11)
        .timeout(Duration.seconds(30))
        .memorySize(1024)
        .logRetention(RetentionDays.FIVE_DAYS)
        .tracing(Tracing.ACTIVE)
        .build()

    val authorizer = CognitoUserPoolsAuthorizer.Builder.create(this, "Authorizer")
        .cognitoUserPools(listOf(userPool))
        .build()

    private val methodOptions = MethodOptions.builder()
        .authorizer(authorizer)
        .authorizationType(AuthorizationType.COGNITO)
        .authorizationScopes(listOf("openid"))
        .build()

    val api = RestApi.Builder.create(this, "RestApi")
        .restApiName("ClickUp API")
        .deployOptions(StageOptions.builder().tracingEnabled(true).build())
        .defaultCorsPreflightOptions(
            CorsOptions.builder()
                .allowCredentials(false)
                .allowHeaders(listOf("Content-Type", "X-Amz-Date", "Authorization", "X-Api-Key", "X-Amz-Security-Token", "X-Amz-User-Agent"))
                .allowMethods(listOf("OPTIONS", "GET", "PUT", "POST", "PATCH", "DELETE"))
                .allowOrigins(listOf("*"))
                .build()
        )
        .build()

    init {
        api.root.apply {
            addProxy().apply {
                addMethod("GET", LambdaIntegration(proxyFunction), methodOptions)
                addMethod("PUT", LambdaIntegration(proxyFunction), methodOptions)
                addMethod("POST", LambdaIntegration(proxyFunction), methodOptions)
                addMethod("PATCH", LambdaIntegration(proxyFunction), methodOptions)
                addMethod("DELETE", LambdaIntegration(proxyFunction), methodOptions)
            }
        }
    }
}
