package com.bkahlert.hello.api

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.bkahlert.hello.aws.lambda.EventHandler
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.p
import kotlinx.html.stream.createHTML
import kotlinx.html.title

class RootHandler : EventHandler() {

    override suspend fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse {
        val host = event.headers["host"]
        val fixedAddress = host?.removePrefix("api.") ?: "github.com/bkahlert/hello"
        val fixedUri = "https://$fixedAddress"

        val response = APIGatewayV2HTTPResponse()
        response.isBase64Encoded = false
        response.statusCode = 302
        response.headers = mapOf(
            "Location" to fixedUri,
            "Content-Type" to "text/html",
        )
        response.body = createHTML().html {
            head { title("Hello! API") }
            body {
                p {
                    +"If you're here by accident, "
                    a(fixedUri) { +fixedAddress }
                    +" might be what you're looking for."
                }
            }
        }
        return response
    }
}
