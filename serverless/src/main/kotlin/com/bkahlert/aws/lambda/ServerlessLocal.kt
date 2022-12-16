package com.bkahlert.aws.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.bkahlert.kommons.debug.render

object ServerlessLocal {

    private val IS_LOCAL by lazy { System.getenv("IS_LOCAL").toBoolean() }

    /**
     * Invoking functions with Serverless locally requires
     * the returned object to have a proper `toString`.
     *
     * This post-processor returns a delegate of the original
     * return value with `toString` overridden.
     */
    private val postProcessor: (APIGatewayV2HTTPResponse) -> APIGatewayV2HTTPResponse by lazy {
        var fn: (APIGatewayV2HTTPResponse) -> APIGatewayV2HTTPResponse = { it }
        if (IS_LOCAL) {
            fn = { response ->
                object : APIGatewayV2HTTPResponse(
                    response.statusCode,
                    response.headers,
                    response.multiValueHeaders,
                    response.cookies,
                    response.body,
                    response.isBase64Encoded,
                ) {
                    override fun toString(): String = response.render()
                }
            }
        }
        fn
    }

    /**
     * Adapts the response if necessary for local debugging.
     */
    fun APIGatewayV2HTTPResponse.postProcess() =
        postProcessor.invoke(this)
}
