package com.bkahlert.kommons.auth

import com.bkahlert.kommons.Either
import com.bkahlert.kommons.either
import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.ktor.JsonHttpClient
import com.bkahlert.kommons.or
import com.bkahlert.kommons.uri.Uri
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

public object OpenIDConnectOperations {

    private val client by lazy {
        JsonHttpClient { expectSuccess = true }
    }

    private suspend fun revokeToken(
        token: String,
        clientId: String,
        revocationEndpoint: Uri,
    ): Either<HttpResponse, RevocationException> = either {
        client.submitForm(
            url = revocationEndpoint.toString(),
            formParameters = Parameters.build {
                append("token", token)
                append("client_id", clientId)
            }
        )
    } or {
        if (it is ResponseException) RevocationException(it)
        else throw it
    }

    /**
     * Revokes a user's access token that was issued with the specified [refreshToken]
     * and all later access tokens from the same refresh token.
     *
     * APIs authenticated by the revoked tokens can't be used any longer.
     */
    public suspend fun revokeToken(
        refreshToken: RefreshToken,
        clientId: String,
        revocationEndpoint: Uri,
    ): Either<HttpResponse, RevocationException> =
        revokeToken(refreshToken.token, clientId, revocationEndpoint)

    /**
     * Revokes a user's access token.
     *
     * APIs authenticated by the revoked token can't be used any longer.
     */
    public suspend fun revokeToken(
        accessToken: AccessToken,
        clientId: String,
        revocationEndpoint: Uri,
    ): Either<HttpResponse, RevocationException> =
        revokeToken(accessToken.token, clientId, revocationEndpoint)
}

@Serializable
public data class ErrorResponse(
    @SerialName("error") val error: String,
    @SerialName("error_description") val errorDescription: String? = null,
) {
    public companion object {
        public fun of(exception: ResponseException): ErrorResponse {
            val body = exception.message?.substringAfter(". Text: \"")?.substringBeforeLast("\"") ?: throw exception
            return LenientJson.decodeFromString(body)
        }
    }
}

public class RevocationException(
    cause: ResponseException,
    response: ErrorResponse = ErrorResponse.of(cause),
    fallbackDescription: String = when (response.error) {
        "invalid_request" -> "The revocation feature is disabled."
        "unsupported_token_type" -> "The type of the provided token is not supported."
        "invalid_client" -> "The provided client credentials are invalid."
        else -> "Unexpected response: $response"
    }
) : RuntimeException(
    message = response.errorDescription ?: fallbackDescription,
    cause = cause,
)
