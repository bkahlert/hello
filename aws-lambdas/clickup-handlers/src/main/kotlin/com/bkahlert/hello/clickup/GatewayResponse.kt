package com.bkahlert.hello.clickup

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import kotlinx.coroutines.runBlocking

class GatewayResponse(
    val body: String,
    val statusCode: Int = 200,
    vararg headers: Pair<String, String> = arrayOf("Content-Type" to "application/json"),
) {
    val headers: Map<String, String> = headers.toMap()
}


abstract class EventHandler : RequestHandler<APIGatewayProxyRequestEvent, GatewayResponse> {

    override fun handleRequest(
        input: APIGatewayProxyRequestEvent?,
        context: Context?,
    ): GatewayResponse = runBlocking {
        handleEvent(
            event = checkNotNull(input) { "input must not be null" },
            context = checkNotNull(context) { "context must not be null" },
        )
    }

    abstract suspend fun handleEvent(
        event: APIGatewayProxyRequestEvent,
        context: Context,
    ): GatewayResponse
}
