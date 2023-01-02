package com.bkahlert.hello.clickup

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.SLF4J
import com.bkahlert.aws.lambda.jsonObjectResponse
import com.bkahlert.aws.lambda.response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.put
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

class ProxyHandler : APIGatewayProxyRequestEventHandler {

    private val logger by SLF4J
    private val clickupUrl by lazy { System.getenv("CLICKUP_URL") }
    private val clickupApiToken by lazy { System.getenv("CLICKUP_API_TOKEN") }
    private val clickupClientId by lazy { System.getenv("CLICKUP_CLIENT_ID") }
    private val clickupClientSecret by lazy { System.getenv("CLICKUP_CLIENT_SECRET") }

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        if (true) {
            logger.debug("EVENT:\n$event")
            logger.debug("CONTEXT:\n$context")
            return jsonObjectResponse { put("body", event.body) }
        }

        val userId = "todo" // event.requiredUserId
        logger.info("Successfully authenticated with as $userId")

        val httpClient = HttpClient.newBuilder().build()

        val host = clickupUrl
        val pathname = "/user"
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(host + pathname))
            .header("Authorization", clickupApiToken)
            .build()

        val response = withContext(Dispatchers.IO) {
            httpClient.send(request, BodyHandlers.ofString())
        }

        val responseAsString = response.body()

        logger.info("ClickUp response: $responseAsString")

        return response(responseAsString)
    }
}
