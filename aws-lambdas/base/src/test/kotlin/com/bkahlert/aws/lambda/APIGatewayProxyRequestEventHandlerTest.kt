package com.bkahlert.aws.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.amazonaws.services.lambda.runtime.tests.annotations.Event
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.put
import org.junit.jupiter.params.ParameterizedTest

class EventHandlerTest {

    private val handler = TestHandler()

    @ParameterizedTest
    @Event(value = "events/string-body.json", type = APIGatewayProxyRequestEvent::class)
    fun `should handle string body`(event: APIGatewayProxyRequestEvent, context: TestContext) {
        val response = handler.handleRequest(event, context)
        response.statusCode shouldBe 200
        response.headers shouldContainExactly mapOf("Content-Type" to "application/json")
        response.body shouldBe """
            {"received":"Lorem ipsum"}
        """.trimIndent()
    }

    @ParameterizedTest
    @Event(value = "events/json-body.json", type = APIGatewayProxyRequestEvent::class)
    fun `should handle JSON body`(event: APIGatewayProxyRequestEvent, context: TestContext) {
        val response = handler.handleRequest(event, context)
        response.statusCode shouldBe 200
        response.headers shouldContainExactly mapOf("Content-Type" to "application/json")
        response.body shouldBe """
            {"received":{"foo":"bar","baz":null}}
        """.trimIndent()
    }

    @ParameterizedTest
    @Event(value = "events/string-body.json", type = APIGatewayProxyRequestEvent::class)
    fun `should log input`(event: APIGatewayProxyRequestEvent, context: TestContext) {
        handler.handleRequest(event, context)
        context.log.shouldContainExactly(
            """
            Lambda invoked: {body: Lorem ipsum,}
        """.trimIndent()
        )
    }
}


class TestHandler : APIGatewayProxyRequestEventHandler {
    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val logger = context.logger
        logger.log("Lambda invoked: $event")

        val body: String? = event.body?.toString()
        return when (val jsonElement = body?.runCatching { Json.decodeFromString<JsonElement>(this) }?.getOrNull()) {
            null -> jsonObjectResponse { put("received", body) }
            else -> jsonObjectResponse { put("received", jsonElement) }
        }
    }
}
