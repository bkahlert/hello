package com.example.aws

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

/**
 * A Lambda that can be invoked manually
 * or by an API Gateway using [proxy integrations](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-output-format).
 */
class LambdaHandler : RequestHandler<Map<String, Any?>, ProxyResponse> {
    override fun handleRequest(
        input: Map<String, Any?>,
        context: Context,
    ): ProxyResponse {
        val logger = context.logger
        logger.log("Lambda invoked: $input")

        val body: String? = input["body"]?.toString()
        val escaped = body?.replace("\"", "\\\"")
        return ProxyResponse(
            """
            { "received": ${escaped?.let { "\"$it\"" } ?: "null"} }
        """.trimIndent()
        )
    }
}

class ProxyResponse(
    val body: String?,
    val statusCode: Int = 200,
    vararg headers: Pair<String, String> = arrayOf("Content-Type" to "application/json"),
) {
    val headers: Map<String, String> = headers.toMap()
}
