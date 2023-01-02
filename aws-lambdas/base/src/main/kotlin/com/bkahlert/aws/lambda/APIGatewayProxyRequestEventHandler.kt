package com.bkahlert.aws.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import kotlinx.coroutines.runBlocking

/**
 * Base implementation of a [proxy integrated](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-output-format)
 * AWS Lambda function with support for coroutines and null-safety.
 */
public fun interface APIGatewayProxyRequestEventHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(
        input: APIGatewayProxyRequestEvent?,
        context: Context?,
    ): APIGatewayProxyResponseEvent = runBlocking {
        handleEvent(
            event = checkNotNull(input) { "input must not be null" },
            context = checkNotNull(context) { "context must not be null" },
        )
    }

    public suspend fun handleEvent(
        event: APIGatewayProxyRequestEvent,
        context: Context,
    ): APIGatewayProxyResponseEvent
}
