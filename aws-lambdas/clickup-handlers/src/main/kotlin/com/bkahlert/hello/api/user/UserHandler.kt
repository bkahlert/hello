package com.bkahlert.hello.api.user

import aws.smithy.kotlin.runtime.util.length
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.IncorrectClaimException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.bkahlert.hello.aws.lambda.EventHandler
import com.bkahlert.hello.aws.lambda.json
import com.bkahlert.hello.aws.lambda.withException
import com.bkahlert.hello.aws.lambda.withMimeType
import com.bkahlert.kommons.logging.SLF4J
import com.bkahlert.kommons.toMomentString
import kotlinx.datetime.Clock
import kotlinx.datetime.toKotlinInstant
import java.security.interfaces.RSAPublicKey


class UserHandler(
    tokenValidator: JsonWebTokenValidator? = null,
) : EventHandler() {

    private val logger by SLF4J
    private val tokenValidator by lazy {
        tokenValidator ?: run {
            val identityProviderUri: String = System.getenv("IDENTITY_PROVIDER_URI")
            // OpenID config: "$identityProviderUri/.well-known/openid-configuration"
            val jwkProvider = JwkProviderBuilder(identityProviderUri).cached(true).build()
            val webAppClientId: String = System.getenv("WEB_APP_CLIENT_ID")
            JsonWebTokenValidator(
                jwkProvider = jwkProvider,
                issuer = identityProviderUri,
                clientId = webAppClientId,
            )
        }
    }


    override suspend fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse = when (event.routeKey) {
        "GET /user" -> when (val authorization = event.headers["authorization"]) {
            null -> APIGatewayV2HTTPResponse.builder()
                .withStatusCode(401)
                .withMimeType { APPLICATION_JSON }
                .build()

            else -> kotlin.runCatching {
                val token = tokenValidator.validate(authorization)
                val userId = token.subject
                logger.info("userId {}", userId)
                APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withMimeType { APPLICATION_JSON }
                    .withBody(json("userId" to userId))
                    .build()
            }.getOrElse {
                APIGatewayV2HTTPResponse.builder().withException(it).build()
            }
        }

        else -> throw IllegalStateException("route ${event.routeKey} unspecified")
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
            require(it.length == 2) { "Invalid authorization header" }
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
