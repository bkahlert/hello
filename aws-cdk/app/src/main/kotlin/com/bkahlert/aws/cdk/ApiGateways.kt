package com.bkahlert.aws.cdk

import com.bkahlert.kommons.text.toPascalCasedString
import software.amazon.awscdk.Stack
import software.amazon.awscdk.services.apigateway.CorsOptions
import software.amazon.awscdk.services.apigateway.GatewayResponse
import software.amazon.awscdk.services.apigateway.ResponseType
import software.amazon.awscdk.services.apigateway.RestApi

/**
 * Creates for each of the specified [responseTypes]
 * a [GatewayResponse] that applies the specified [CorsOptions].
 *
 * **or**
 *
 * Using the default settings,
 * adds [CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS) headers
 * so that clients with expired tokens can read the response
 * and trigger a token refresh appropriately.
 *
 * @see <a href="https://github.com/serverless/examples/blob/5e3e7030399cf99a98de424e92f90501996befb4/aws-node-auth0-custom-authorizers-api/serverless.yml#L37">AWS custom authorizer example</a>
 */
context(Stack)
@Suppress("LongLine")
fun RestApi.enableCorsOnGatewayResponses(
    corsOptions: CorsOptions,
    vararg responseTypes: ResponseType = arrayOf(
        ResponseType.EXPIRED_TOKEN,
        ResponseType.UNAUTHORIZED,
    ),
): List<GatewayResponse> {
    val stack: Stack = this@Stack
    val restApi: RestApi = this@enableCorsOnGatewayResponses
    return responseTypes.map { type ->
        GatewayResponse.Builder.create(stack, "${type.responseType.toPascalCasedString()}GatewayResponseWithCors")
            .restApi(restApi)
            .type(type)
            .responseHeaders(corsOptions.toResponseHeaders())
            .build()
    }
}

fun CorsOptions.toResponseHeaders() = buildMap {
    allowCredentials?.also {
        put("Access-Control-Allow-Credentials", "'$it'")
    }
    allowHeaders?.also {
        put("Access-Control-Allow-Headers", "'${it.joinToString(",")}'")
    }
    allowMethods?.also {
        put("Access-Control-Allow-Methods", "'${it.joinToString(",")}'")
    }
    allowOrigins?.also {
        put("Access-Control-Allow-Origin", if (it.contains("*")) "method.request.header.origin" else "'${it.joinToString(",")}'")
    }
}
