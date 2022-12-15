package com.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.kommons.logging.SLF4J
import kotlinx.html.body
import kotlinx.html.html
import kotlinx.html.stream.createHTML
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

class Handler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private val logger by SLF4J

    private val snippets = mutableMapOf<String, String>()

    init {
        logger.info("Just go instantiated")
    }

    override fun handleRequest(event: APIGatewayProxyRequestEvent?, context: Context): APIGatewayProxyResponseEvent {
        logger.info("Adding snippets {}", event?.queryStringParameters?.keys)
        event?.queryStringParameters?.also { snippets.putAll(it) }

        val response = APIGatewayProxyResponseEvent()
        response.isBase64Encoded = false
        response.statusCode = 200
        response.headers = mapOf(
            "Content-Type" to "text/html"
        )
        response.body = createHTML().html {
            body {
                table {
                    tr {
                        th { +"Key" }
                        th { +"Value" }
                    }
                    snippets.forEach { (key, value) ->
                        tr {
                            td { +key }
                            td { +value }
                        }
                    }
                }
            }
        }
        return response
    }
}
