package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.toEnvironment
import software.amazon.awscdk.Duration
import software.amazon.awscdk.services.apigateway.AuthorizationType
import software.amazon.awscdk.services.apigateway.CognitoUserPoolsAuthorizer
import software.amazon.awscdk.services.apigateway.Cors
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
import software.amazon.awscdk.services.secretsmanager.ISecret
import software.constructs.Construct

class ClickUp(
    /** The scope in which to define this construct. */
    scope: Construct,
    /** The scoped construct ID. */
    id: String,
    /** Used for authorization. */
    userPool: UserPool,
    /** The credentials of the [ClickUp API](https://clickup.com/api/). */
    val secret: ISecret,
) : Construct(scope, id) {

    val corsOptions = CorsOptions.builder()
        .allowCredentials(false)
        .allowHeaders(Cors.DEFAULT_HEADERS)
        .allowMethods(Cors.ALL_METHODS)
        .allowOrigins(Cors.ALL_ORIGINS)
        .build()

    val proxyFunction = Function.Builder.create(this, "ProxyFunction")
        .code(Code.fromAsset("../../aws-lambdas/clickup-api-handlers/build/libs/clickup-api-handlers-all.jar"))
        .handler("com.bkahlert.hello.clickup.ProxyHandler")
        .environment(
            mapOf(
                "CLICKUP_URL" to "https://api.clickup.com/api/v2",
                "CLICKUP_API_TOKEN" to secret.secretValueFromJson("api_token").unsafeUnwrap(),
                "CLICKUP_CLIENT_ID" to secret.secretValueFromJson("client_id").unsafeUnwrap(),
                "CLICKUP_CLIENT_SECRET" to secret.secretValueFromJson("client_secret").unsafeUnwrap(),
            ) + corsOptions.toEnvironment()
        )
        .architecture(Architecture.ARM_64)
        .runtime(Runtime.JAVA_11)
        .timeout(Duration.seconds(30))
        .memorySize(1024)
        .logRetention(RetentionDays.FIVE_DAYS)
        .tracing(Tracing.ACTIVE)
        .build()
//        .also { secret.grantRead(it) }

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
        .build()

    init {
        api.root.apply {
            addCorsPreflight(corsOptions)
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
