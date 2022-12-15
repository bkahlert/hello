package com.bkahlert.aws.lambda

import aws.smithy.kotlin.runtime.util.decodeBase64
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.text.simpleTitleCasedName
import kotlinx.coroutines.runBlocking

abstract class EventHandler : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    override fun handleRequest(
        input: APIGatewayV2HTTPEvent?,
        context: Context?,
    ): APIGatewayV2HTTPResponse = kotlin.runCatching {
        checkNotNull(input) { "input must not be null" }
        checkNotNull(context) { "context must not be null" }
        runBlocking {
            handleEvent(input, context)
        }
    }.getOrElse { ex ->
        val response = APIGatewayV2HTTPResponse()
        response.statusCode = 500
        response.body = json(
            "error" to ex::class.simpleTitleCasedName,
            "message" to ex.message,
        )
        response
    }

    abstract suspend fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse
}

fun json(vararg pairs: Pair<String, String?>) = pairs
    .filter { (_, value) -> value != null }
    .joinToString(", ", "{ ", " }") { (key, value) -> "${key.quoted}: ${value.quoted}" }

val APIGatewayV2HTTPEvent.decodedBody: String?
    get() = if (isBase64Encoded) body?.decodeBase64() else body

val APIGatewayV2HTTPEvent.httpMethod: String
    get() = requestContext.http.method
