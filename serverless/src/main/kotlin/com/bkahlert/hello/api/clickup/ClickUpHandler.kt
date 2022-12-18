package com.bkahlert.hello.api.clickup

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.bkahlert.hello.api.requiredUserId
import com.bkahlert.hello.aws.lambda.EventHandler
import com.bkahlert.hello.aws.lambda.withMimeType
import com.bkahlert.kommons.debug.trace
import com.bkahlert.kommons.logging.SLF4J
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

class ClickUpHandler : EventHandler() {

    private val logger by SLF4J
    private val clickupUrl by lazy { System.getenv("CLICKUP_URL") }
    private val clickupApiToken by lazy { System.getenv("CLICKUP_API_TOKEN") }
    private val clickupClientId by lazy { System.getenv("CLICKUP_CLIENT_ID") }
    private val clickupClientSecret by lazy { System.getenv("CLICKUP_CLIENT_SECRET") }

    override suspend fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse {
        val userId = event.requiredUserId
        logger.info("Successfully authenticated with as $userId")

        var httpClient = HttpClient.newBuilder().build();

        var host = clickupUrl;
        var pathname = "/user";
        var request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(host + pathname))
            .header("Authorization", clickupApiToken)
            .build();

        var response = withContext(Dispatchers.IO) {
            httpClient.send(request, BodyHandlers.ofString())
        };

        return APIGatewayV2HTTPResponse.builder()
            .withStatusCode(200)
            .withMimeType { APPLICATION_JSON }
            .withBody(response.body().trace(out = { logger.info(it) }))
            .build()
    }
}
