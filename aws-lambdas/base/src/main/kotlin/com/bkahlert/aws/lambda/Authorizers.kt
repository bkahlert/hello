package com.bkahlert.aws.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent

private val APIGatewayProxyRequestEvent.claims
    get() = requestContext?.authorizer?.get("claims")
        ?.let { it as? Map<*, *> }
        ?.mapKeys { it.key.toString() }
        ?.mapValues { it.value.toString() }
        ?: emptyMap()

public val APIGatewayProxyRequestEvent.userId: String?
    get() = claims["sub"]

public val APIGatewayProxyRequestEvent.requiredUserId: String
    get() = userId ?: throw IllegalStateException("Unauthorized")

public val APIGatewayProxyRequestEvent.username: String?
    get() = claims["username"]

public val APIGatewayProxyRequestEvent.requiredUsername: String
    get() = username ?: throw IllegalStateException("Unauthorized")
