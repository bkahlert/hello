package com.bkahlert.hello.clickup

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.bkahlert.hello.aws.lambda.MimeTypes
import com.bkahlert.kommons.debug.trace
import com.bkahlert.kommons.logging.SLF4J
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

class ProxyHandler : EventHandler() {

    private val logger by SLF4J
    private val clickupUrl by lazy { System.getenv("CLICKUP_URL") }
    private val clickupApiToken by lazy { System.getenv("CLICKUP_API_TOKEN") }
    private val clickupClientId by lazy { System.getenv("CLICKUP_CLIENT_ID") }
    private val clickupClientSecret by lazy { System.getenv("CLICKUP_CLIENT_SECRET") }

    override suspend fun handleEvent(
        event: APIGatewayProxyRequestEvent,
        context: Context,
    ): GatewayResponse {
        if (true) {
            logger.debug("EVENT:\n$event")
            logger.debug("CONTEXT:\n$context")
            return GatewayResponse("EVENT:\n$event", 200, "Content-Type" to MimeTypes.TEXT_PLAIN)
        }

        val userId = "todo" // event.requiredUserId
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

        return GatewayResponse(response.body().trace(out = { logger.info(it) }))
    }
}
