package com.bkahlert.hello.props

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent


private val APIGatewayProxyRequestEvent.claims
    get() = requestContext?.authorizer?.get("claims")
        ?.let { it as? Map<*, *> }
        ?.mapKeys { it.key.toString() }
        ?.mapValues { it.value.toString() }
        ?: emptyMap()

val APIGatewayProxyRequestEvent.userId
    get() = claims["sub"]

val APIGatewayProxyRequestEvent.requiredUserId
    get() = userId ?: throw IllegalStateException("Unauthorized")

val APIGatewayProxyRequestEvent.username
    get() = claims["username"]

val APIGatewayProxyRequestEvent.requiredUsername
    get() = username ?: throw IllegalStateException("Unauthorized")
