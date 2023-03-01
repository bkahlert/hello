package com.bkahlert.hello.clickup

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.caseInsensitiveHeaders
import com.bkahlert.aws.lambda.errorResponse
import com.bkahlert.aws.lambda.response
import com.bkahlert.kommons.auth.Token
import com.bkahlert.kommons.ktor.JsonHttpClient
import com.bkahlert.kommons.ktor.TokenAuthPlugin
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders

class ProxyHandler : APIGatewayProxyRequestEventHandler() {
    private val clickupUrl by lazy { System.getenv("CLICKUP_URL") }
    private val clickupClientId by lazy { System.getenv("CLICKUP_CLIENT_ID") }
    private val clickupClientSecret by lazy { System.getenv("CLICKUP_CLIENT_SECRET") }

    private val apiEndpoint = "https://hello.aws-dev.choam.de/api"
    private val clickupReturnUrlPath by lazy { "/oauth2/idpresponse" }
    private val clickupReturnUrl by lazy { "$apiEndpoint/clickup$clickupReturnUrlPath" }

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val clickupApiToken = event.caseInsensitiveHeaders[HttpHeaders.Authorization].firstOrNull()
        logger.info("ClickUp API token: $clickupApiToken")

        if (clickupApiToken == null) return errorResponse(401, "Authorization header missing", context)

        val httpClient = JsonHttpClient {
            install(TokenAuthPlugin) {
                token = object : Token {
                    override val token: String get() = clickupApiToken
                }
            }
            expectSuccess = false
        }
        val host = clickupUrl
        val pathname = "/user"

        val responseAsString = httpClient.get("$host$pathname").bodyAsText()

        logger.info("ClickUp response: $responseAsString")

        return response(responseAsString)
    }
}
