package com.bkahlert.hello.api

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent

// Provided by API Gateway
// Otherwise would have to be checked
// TODO consider at least checking the "iss" claim to be the issuer https://cognito-idp.$AWS_REGION.amazonaws.com/$USER_POOL_ID/.well-known/openid-configuration
// see https://advancedweb.hu/how-to-add-cognito-login-to-a-website/#check-the-signature
private val APIGatewayV2HTTPEvent.claims
    get() = requestContext?.authorizer?.jwt?.claims ?: emptyMap()

val APIGatewayV2HTTPEvent.userId
    get() = claims["sub"]

val APIGatewayV2HTTPEvent.requiredUserId
    get() = userId ?: throw IllegalStateException("unauthorized")

val APIGatewayV2HTTPEvent.username
    get() = claims["username"]

val APIGatewayV2HTTPEvent.requiredUsername
    get() = username ?: throw IllegalStateException("unauthorized")
