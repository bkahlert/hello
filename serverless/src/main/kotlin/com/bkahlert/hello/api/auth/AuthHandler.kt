package com.bkahlert.hello.api.auth

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.bkahlert.hello.api.requiredUserId
import com.bkahlert.hello.aws.lambda.EventHandler
import com.bkahlert.kommons.logging.SLF4J

class AuthHandler : EventHandler() {

    private val logger by SLF4J

    override suspend fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse {
        // Provided by API Gateway
        // Otherwise would have to be checked
        val userId = event.requiredUserId
        logger.info("Successfully authenticated with as $userId")
        logger.dump(event, context)
        val response = APIGatewayV2HTTPResponse()
        response.isBase64Encoded = false
        response.statusCode = 200
        return response
    }
}
