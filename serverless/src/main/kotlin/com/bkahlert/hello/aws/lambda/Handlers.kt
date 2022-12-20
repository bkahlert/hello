package com.bkahlert.hello.aws.lambda

import aws.smithy.kotlin.runtime.util.decodeBase64
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.auth0.jwt.exceptions.JWTVerificationException
import com.bkahlert.hello.aws.lambda.ServerlessLocal.postProcess
import com.bkahlert.kommons.quoted
import kotlinx.coroutines.runBlocking

abstract class EventHandler : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    override fun handleRequest(
        input: APIGatewayV2HTTPEvent?,
        context: Context?,
    ): APIGatewayV2HTTPResponse = runBlocking {
        handleEvent(
            event = checkNotNull(input) { "input must not be null" },
            context = checkNotNull(context) { "context must not be null" },
        ).postProcess()
    }

    abstract suspend fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse
}

object MimeTypes {
    const val APPLICATION_JSON: String = "application/json"
    const val TEXT_PLAIN: String = "text/plain"
}

fun APIGatewayV2HTTPResponse.APIGatewayV2HTTPResponseBuilder.withMimeType(provide: MimeTypes.() -> String) =
    withHeaders(mapOf("Content-Type" to MimeTypes.provide()))


fun APIGatewayV2HTTPResponse.APIGatewayV2HTTPResponseBuilder.withException(exception: Throwable) =
    withStatusCode(
        when (exception) {
            is JWTVerificationException -> 401
            is IllegalArgumentException -> 400
            else -> 500
        }
    )
        .withMimeType { APPLICATION_JSON }
        .withBody(json("errorType" to exception::class.simpleName, "errorMessage" to exception.message))

fun json(map: Map<String, String?>) = map.entries
    .filter { (_, value) -> value != null }
    .joinToString(", ", "{ ", " }") { (key, value) -> "${key.quoted}: ${value.quoted}" }

fun json(vararg pairs: Pair<String, String?>) = json(pairs.toMap())

val APIGatewayV2HTTPEvent.decodedBody: String?
    get() = if (isBase64Encoded) body?.decodeBase64() else body

val APIGatewayV2HTTPEvent.httpMethod: String
    get() = requestContext.http.method
