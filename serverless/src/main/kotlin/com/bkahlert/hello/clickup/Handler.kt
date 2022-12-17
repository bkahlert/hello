package com.bkahlert.hello.clickup

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.bkahlert.aws.lambda.EventHandler
import com.bkahlert.aws.lambda.MimeTypes
import com.bkahlert.aws.lambda.decodedBody
import com.bkahlert.aws.lambda.json
import com.bkahlert.aws.lambda.withMimeType
import com.bkahlert.kommons.logging.SLF4J

class Handler : EventHandler() {

    private val logger by SLF4J
    private val clickupUrl by lazy { System.getenv("CLICKUP_URL") }
    private val clickupApiToken by lazy { System.getenv("CLICKUP_API_TOKEN") }
    private val clickupClientId by lazy { System.getenv("CLICKUP_CLIENT_ID") }
    private val clickupClientSecret by lazy { System.getenv("CLICKUP_CLIENT_SECRET") }

    override suspend fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse {
        logger.info("""
            $clickupUrl
            $clickupApiToken
            $clickupClientId
            $clickupClientSecret
        """.trimIndent())
        logger.info("route key: ${event.routeKey}")
        return APIGatewayV2HTTPResponse.builder()
            .withStatusCode(200)
            .withMimeType { APPLICATION_JSON }
            .withBody("value")
            .build()
        }
}
