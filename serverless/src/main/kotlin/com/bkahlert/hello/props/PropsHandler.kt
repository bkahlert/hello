package com.bkahlert.hello.props

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.bkahlert.aws.lambda.EventHandler
import com.bkahlert.aws.lambda.MimeTypes
import com.bkahlert.aws.lambda.decodedBody
import com.bkahlert.aws.lambda.json
import com.bkahlert.aws.lambda.withMimeType
import com.bkahlert.kommons.logging.SLF4J

class PropsHandler : EventHandler() {

    private val logger by SLF4J

    override suspend fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse = when (event.routeKey) {
        "GET /props/{id}" -> {
            val propId = requireNotNull(event.pathParameters["id"])
            val (mime, value) = when (propId) {
                "" -> {
                    logger.info("Getting all props")
                    MimeTypes.APPLICATION_JSON to PropsTable.getProps()?.let { json(it) }
                }

                else -> {
                    logger.info("Getting $propId")
                    MimeTypes.TEXT_PLAIN to PropsTable.getProp(propId)
                }
            }
            when (value) {
                null -> APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(404)
                    .build()

                else -> APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withMimeType { mime }
                    .withBody(value)
                    .build()
            }
        }

        "POST /props/{id}" -> {
            val propId = requireNotNull(event.pathParameters["id"])
            val value = event.decodedBody
            val oldValue = when (value) {
                null -> {
                    logger.info("Deleting $propId")
                    PropsTable.deleteProp(propId)
                }

                else -> {
                    logger.info("Updating $propId")
                    PropsTable.updateProp(propId, value)
                }
            }
            when (oldValue) {
                value -> APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .build()

                null -> APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(201)
                    .build()

                else -> APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(204)
                    .build()
            }
        }

        else -> throw IllegalStateException("route ${event.routeKey} unspecified")
    }
}
