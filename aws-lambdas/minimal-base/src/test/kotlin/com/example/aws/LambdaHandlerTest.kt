package com.example.aws

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.tests.annotations.Event
import com.bkahlert.aws.lambda.TestContext
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest

class LambdaHandlerTest {

    private val handler = LambdaHandler()

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
