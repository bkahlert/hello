package com.bkahlert.hello.api.props

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.tests.annotations.Event
import com.bkahlert.hello.aws.TestContext
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest

class PropsHandlerTest {

    private val handler = PropsHandler()

    @Disabled("needs local DB")
    @ParameterizedTest
    @Event(value = "events/getProp/foo.json", type = APIGatewayV2HTTPEvent::class)
    fun `should get prop`(event: APIGatewayV2HTTPEvent) {
        val response = handler.handleRequest(event, TestContext)
        response.statusCode shouldBe 200
    }
}
