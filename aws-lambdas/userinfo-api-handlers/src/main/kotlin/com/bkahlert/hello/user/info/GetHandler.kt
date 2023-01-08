package com.bkahlert.hello.user.info

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.IncorrectClaimException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.SLF4J
import com.bkahlert.aws.lambda.caseInsensitiveHeaders
import com.bkahlert.aws.lambda.jsonObjectResponse
import com.bkahlert.aws.lambda.jsonResponse
import com.bkahlert.kommons.toMomentString
import kotlinx.datetime.Clock
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.put
import java.security.interfaces.RSAPublicKey
import java.util.Base64

class GetHandler(
    tokenValidator: JsonWebTokenValidator? = null,
) : APIGatewayProxyRequestEventHandler() {

    private val tokenValidator by lazy {
        tokenValidator ?: run {
            val identityProviderUri: String = System.getenv("USER_POOL_PROVIDER_URL")
            // OpenID config: "$identityProviderUri/.well-known/openid-configuration"
            val jwkProvider = JwkProviderBuilder(identityProviderUri).cached(true).build()
            val webAppClientId: String = System.getenv("USER_POOL_CLIENT_ID")
            JsonWebTokenValidator(
                jwkProvider = jwkProvider,
                issuer = identityProviderUri,
                clientId = webAppClientId,
            )
        }
    }

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val authorization = event.caseInsensitiveHeaders["Authorization"].firstOrNull()
            ?: return jsonObjectResponse(401) { put("message", "Authorization header missing") }

        return kotlin.runCatching {
            val token = tokenValidator.validate(authorization)
            val userId = token.subject
            logger.info("userId {}", userId)

            val decodedPayload = Base64.getUrlDecoder().decode(JsonWebTokenValidator.extractToken(authorization).split('.')[1]).decodeToString()
            logger.debug("Decoded payload: $decodedPayload")

            jsonResponse(Json.parseToJsonElement(decodedPayload))
        }.getOrElse { exception ->
            jsonObjectResponse(
                when (exception) {
                    is JWTVerificationException -> 401
                    is IllegalArgumentException -> 400
                    else -> 500
                }
            ) { put("message", exception.message) }
        }
    }
}

class JsonWebTokenValidator(
    private val jwkProvider: JwkProvider,
    private val issuer: String,
    private val clientId: String,
    private val clock: Clock = Clock.System,
) {
    private val logger by SLF4J

    // https://aws.amazon.com/premiumsupport/knowledge-center/decode-verify-cognito-json-token/
    // https://docs.aws.amazon.com/cognito/latest/developerguide/amazon-cognito-user-pools-using-tokens-verifying-a-jwt.html
    // https://github.com/auth0/java-jwt
    fun validate(authorization: String): DecodedJWT {
        val token: String = extractToken(authorization)
        val jwt: DecodedJWT = JWT.decode(token)
        val jwk: Jwk = jwkProvider.get(jwt.keyId)
        val alg: Algorithm = jwk.alg
        alg.verify(jwt)

        // token is correctly signed

        if (jwt.issuer != issuer) throw IncorrectClaimException("Issuer does not match", "iss", jwt.claims["iss"])
        when (val tokenUse = jwt.claims["token_use"]?.asString()) {
            "id" -> {
                logger.debug("token_use {}", tokenUse)
                if (clientId !in jwt.audience) throw IncorrectClaimException("No audience matches", "aud", jwt.claims["aud"])
            }

            "access" -> {
                logger.debug("token_use {}", tokenUse)
                if (clientId != jwt.getClaim("client_id").asString()) throw IncorrectClaimException(
                    "Client ID does not match",
                    "client_id",
                    jwt.claims["client_id"]
                )
            }
        }
        val validFor = jwt.expiresAtAsInstant.toKotlinInstant() - clock.now()
        if (validFor.isNegative()) throw TokenExpiredException("Token expired ${validFor.toMomentString()}", jwt.expiresAtAsInstant)

        // token is valid

        return jwt
    }

    companion object {
        fun extractToken(authorization: String): String = authorization.split(' ', limit = 2).let {
            require(it.size == 2) { "Invalid authorization header" }
            when (val scheme = it.first()) {
                "Bearer" -> it.last()
                else -> throw IllegalArgumentException("Unsupported authorization scheme $scheme")
            }
        }

        val Jwk.alg: Algorithm
            get() = when (val algorithm = algorithm) {
                "RS256" -> Algorithm.RSA256(publicKey as RSAPublicKey, null)
                else -> throw IllegalStateException("Unsupported algorithm $algorithm")
            }
    }
}
