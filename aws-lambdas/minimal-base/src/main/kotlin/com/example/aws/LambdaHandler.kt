package com.example.aws

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.SLF4J
import com.bkahlert.aws.lambda.jsonObjectResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.put
import org.slf4j.Logger

class LambdaHandler : APIGatewayProxyRequestEventHandler {

    private val logger: Logger by SLF4J

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.info("Lambda invoked: $event")

        val body: String? = event.body?.toString()
        return when (val jsonElement = body?.runCatching { Json.decodeFromString<JsonElement>(this) }?.getOrNull()) {
            null -> jsonObjectResponse { put("received", body) }
            else -> jsonObjectResponse { put("received", jsonElement) }
        }
    }
}
