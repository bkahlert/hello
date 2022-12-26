package com.bkahlert.kommons.ktor

import com.bkahlert.kommons.SimpleLogger.Companion.simpleLogger
import com.bkahlert.kommons.auth.OpenIDProvider
import com.bkahlert.kommons.auth.OpenIDProviderMetadata
import com.bkahlert.kommons.debug.asString
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.provideDelegate
import com.bkahlert.kommons.serialization.JsonSerializer
import com.bkahlert.kommons.text.Char.characters
import com.bkahlert.kommons.text.truncate
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.pluginOrNull
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint32Array
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.dom.url.URL
import kotlin.js.Promise
import kotlin.js.json

private fun minimalClient(config: HttpClientConfig<HttpClientEngineConfig>.() -> Unit = {}) = HttpClient(Js) {
    install(ContentNegotiation) { json(JsonSerializer) }
    config()
}

private val openIdClient by lazy { minimalClient() }

/**
 * Loads the [OpenID Provider Configuration Information](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig)
 */
public suspend fun OpenIDProvider.loadOpenIDConfiguration(
    tokenEndpointFallback: (OpenIDProviderMetadata) -> String? = { it.tokenEndpoint?.replace("/token", "/revoke") },
): OpenIDProviderMetadata {
    val metadata = openIdClient.get(openIDConfigurationUri).body<OpenIDProviderMetadata>()
    return metadata.takeUnless { it.revocationEndpoint == null } ?: metadata.copy(revocationEndpoint = tokenEndpointFallback(metadata))
}

/**
 * OAuth 2.0 authorization server
 */
public data class OAuth2AuthorizationServer(
    /** @see [OpenIDProviderMetadata.issuer] */
    val issuer: String,
    /** @see [OpenIDProviderMetadata.authorizationEndpoint] */
    val authorizationEndpoint: String,
    /** @see [OpenIDProviderMetadata.tokenEndpoint] */
    val tokenEndpoint: String,
    /**
     * Optional token revocation endpoint.
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7009">OAuth 2.0 Token Revocation</a>
     */
    val revocationEndpoint: String?,
) {
    public companion object {

        /**
         * Creates a [OAuth2AuthorizationServer] from the specified [metadata].
         */
        public fun from(metadata: OpenIDProviderMetadata): OAuth2AuthorizationServer = OAuth2AuthorizationServer(
            issuer = metadata.issuer,
            authorizationEndpoint = metadata.authorizationEndpoint,
            tokenEndpoint = requireNotNull(metadata.tokenEndpoint),
            revocationEndpoint = metadata.revocationEndpoint,
        )
    }
}

public interface OAuth2Resource {
    /** Name of the resource. */
    public val name: String

    /** Returns whether the given URI belongs to the resource. */
    public fun matches(url: Url): Boolean
}

/**
 * State of the [Authorization Code Flow with Proof Key for Code Exchange (PKCE)](https://auth0.com/docs/get-started/authentication-and-authorization-flow/authorization-code-flow-with-proof-key-for-code-exchange-pkce)
 * using the specified [authorizationServer].
 */
@Suppress("LongLine")
public sealed class OAuth2AuthorizationState(
    protected open val authorizationServer: OAuth2AuthorizationServer,
    protected open val clientId: String,
) {

    public data class Unauthorized(
        override val authorizationServer: OAuth2AuthorizationServer,
        override val clientId: String,
    ) : OAuth2AuthorizationState(authorizationServer, clientId) {
        private val logger = simpleLogger()

        public suspend fun authorize(): OAuth2AuthorizationState {
            logger.info("Preparing authorization with ${authorizationServer.authorizationEndpoint}")
            val state = generateNonce()
            val codeVerifier = generateNonce()
            sessionStorage.setItem("codeVerifier-$state", codeVerifier)
            val codeChallenge = base64URLEncode(sha256(codeVerifier))

            val authorizationUrl = URLBuilder(authorizationServer.authorizationEndpoint).apply {
                parameters.apply {
                    append("response_type", "code")
                    append("client_id", clientId)
                    append("state", state)
                    append("code_challenge_method", "S256")
                    append("code_challenge", codeChallenge)
                    append("redirect_uri", window.location.origin)
                }
            }.buildString()
            logger.info("Redirecting to $authorizationUrl")
            window.location.href = authorizationUrl
            coroutineScope { cancel("Redirection to ${authorizationServer.authorizationEndpoint}") }
            return compute(authorizationServer, clientId)
        }
    }

    public data class Authorizing(
        override val authorizationServer: OAuth2AuthorizationServer,
        override val clientId: String,
        val tokensStorage: BearerTokensStorage,
        val authorizationCode: String,
        val state: String?,
    ) : OAuth2AuthorizationState(authorizationServer, clientId) {
        private val logger = simpleLogger()

        public suspend fun getTokens(): Authorized {
            logger.info("Authorization code received: $authorizationCode")
            window.history.replaceState(json(), document.title, "/")
            val state = checkNotNull(state) { "Failed to validate code due to missing state parameter" }
            logger.debug("Checking state $state")
            val codeVerifier = sessionStorage.getItem("codeVerifier-$state")
            sessionStorage.removeItem("codeVerifier-$state")
            checkNotNull(codeVerifier) { "Code is not valid" }

            logger.info("Getting tokens using code verifier: $codeVerifier")
            val tokenInfo: TokenInfo = minimalClient().submitForm(
                url = authorizationServer.tokenEndpoint,
                formParameters = Parameters.build {
                    append("grant_type", "authorization_code")
                    append("client_id", clientId)
                    append("code", authorizationCode)
                    append("code_verifier", codeVerifier)
                    append("redirect_uri", window.location.origin)
                }
            ) { expectSuccess = true }.body()
            tokensStorage.accessToken = tokenInfo.accessToken
            tokensStorage.refreshToken = checkNotNull(tokenInfo.refreshToken) { "Refresh token missing" }
            logger.info("Authorization successful")

            return Authorized(authorizationServer, clientId, tokensStorage)
        }
    }

    public data class Authorized(
        override val authorizationServer: OAuth2AuthorizationServer,
        override val clientId: String,
        val tokensStorage: BearerTokensStorage,
    ) : OAuth2AuthorizationState(authorizationServer, clientId) {
        private val logger = simpleLogger()

        public fun buildClient(
            vararg resources: OAuth2Resource,
            config: HttpClientConfig<HttpClientEngineConfig>.() -> Unit = {},
        ): HttpClient = minimalClient {
            install(Auth) {
                bearer {
                    loadTokens {
                        tokensStorage.bearerTokens.also { logger.debug("Loaded tokens: $it") }
                    }
                    refreshTokens {
                        logger.info("Refreshing tokens")
                        val refreshTokenInfo: TokenInfo = this@refreshTokens.client.submitForm(
                            url = authorizationServer.tokenEndpoint,
                            formParameters = Parameters.build {
                                append("grant_type", "refresh_token")
                                append("client_id", clientId)
                                append("redirect_uri", window.location.origin)
                                append("refresh_token", oldTokens?.refreshToken ?: "")
                            }
                        ) { markAsRefreshTokenRequest() }.body()
                        tokensStorage.accessToken = refreshTokenInfo.accessToken
                        tokensStorage.bearerTokens!!
                    }
                    sendWithoutRequest { request ->
                        val url = request.url.build()
                        resources.any { it.matches(url) }
                    }
                }
            }
            config()
        }

        public suspend fun revokeTokens(client: HttpClient? = null): OAuth2AuthorizationState {
            val revocationEndpoint = authorizationServer.revocationEndpoint
            if (revocationEndpoint == null) {
                logger.warn("No token revocation endpoint configured. Doing nothing.")
                return this
            }

            logger.info("Revoking tokens")
            val auth = client?.pluginOrNull(Auth.Plugin)
            auth?.providers?.removeAll {
                if (it is BearerAuthProvider) {
                    it.clearToken()
                    true
                } else {
                    false
                }
            }

            when (val token = tokensStorage.refreshToken ?: tokensStorage.accessToken) {
                null -> logger.info("No tokens found")
                else -> {
                    val response = minimalClient().submitForm(
                        url = revocationEndpoint,
                        formParameters = Parameters.build {
                            append("token", token)
                            append("client_id", clientId)
                        }
                    ) {
                        expectSuccess = false
                    }
                    if (response.status.isSuccess()) {
                        logger.info("Successfully revoked tokens")
                    } else {
                        logger.error("Failed to revoke tokens: ${response.bodyAsText()}")
                    }
                    tokensStorage.bearerTokens = null
                }
            }
            return compute(authorizationServer, clientId)
        }
    }

    public companion object {

        /**
         * Computes the current [OAuth2AuthorizationState] based on the specified [authorizationServer]
         * and [clientId].
         */
        public fun compute(
            authorizationServer: OAuth2AuthorizationServer,
            clientId: String,
        ): OAuth2AuthorizationState {
            val bearerTokensStorage = BearerTokensStorage(localStorage.scoped(authorizationServer.issuer))

            val searchParams = URL(window.location.href).searchParams
            return when (val authorizationCode = searchParams.get("code")) {
                null -> when (bearerTokensStorage.bearerTokens) {
                    null -> Unauthorized(authorizationServer, clientId)
                    else -> Authorized(authorizationServer, clientId, bearerTokensStorage)
                }

                else -> Authorizing(
                    authorizationServer,
                    clientId,
                    bearerTokensStorage,
                    authorizationCode,
                    searchParams.get("state"),
                )
            }
        }
    }
}

public class BearerTokensStorage(
    storage: Storage,
) {

    private val logger = simpleLogger()

    public var accessToken: String? by storage
    public var refreshToken: String? by storage

    public var bearerTokens: BearerTokens?
        get() = (accessToken to refreshToken).let { (a, r) ->
            if (a != null && r != null) BearerTokens(a, r)
            else {
                if (a != null || r != null) logger.error("Incomplete tokens found")
                null
            }
        }
        set(value) {
            accessToken = value?.accessToken
            refreshToken = value?.refreshToken
        }

    override fun toString(): String {
        return asString {
            put("accessToken", accessToken?.truncate(20.characters))
            put("refreshToken", refreshToken?.truncate(20.characters))
        }
    }
}


@Serializable
private data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null, // only if grant_type was authorization_code
    @SerialName("id_token") val idToken: String? = null,
    @SerialName("token_type") val tokenType: String, // e.g. "Bearer"
    @SerialName("expires_in") val expiresIn: Int, // e.g. 3600
)

@Serializable
private data class ErrorInfo(
    @SerialName("error") val error: String,
    @SerialName("error_description") val errorDescription: String? = null,
)

private external object crypto {
    fun getRandomValues(array: Uint32Array): Uint32Array
    val subtle: SubtleCrypto
}

private external class SubtleCrypto {
    fun digest(algorithm: String, data: Uint8Array): Promise<ArrayBuffer>
}

private external class TextEncoder {
    fun encode(input: String): Uint8Array
}

private suspend fun sha256(str: String): ArrayBuffer {
    return crypto.subtle.digest("SHA-256", TextEncoder().encode(str)).await()
}

private suspend fun generateNonce(): String {
    val uint32Array = Uint32Array(4)
    console.log("uint32Array", uint32Array)
    val randomValues = crypto.getRandomValues(uint32Array)
    console.log("randomValues", randomValues)
    val randomValuesString = 0.until(randomValues.length).joinToString("") { randomValues[it].toString() }
    console.log("randomValuesString", randomValuesString)
    val hash = sha256(randomValuesString)
    console.log("hash", hash)
    // https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/digest
    val uint8Array = Uint8Array(hash)
    console.log("uint8Array", uint8Array)
    return 0.until(uint8Array.length).joinToString("") {
        uint8Array[it].toString(16).padStart(2, '0')
    }
}

private fun base64URLEncode(
    @Suppress("UNUSED_PARAMETER") string: ArrayBuffer,
): String = js("btoa(String.fromCharCode.apply(null, new Uint8Array(string))).replace(/\\+/g, '-').replace(/\\//g, '_').replace(/=+$/, '')") as String
