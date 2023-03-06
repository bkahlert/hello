package com.bkahlert.kommons.oauth

import com.bkahlert.kommons.auth.ErrorResponse
import com.bkahlert.kommons.auth.OpenIDConnectOperations
import com.bkahlert.kommons.auth.OpenIDProvider
import com.bkahlert.kommons.auth.RefreshToken
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.StorageOpenIDProviderMetadataCache
import com.bkahlert.kommons.auth.StorageTokenInfoStorage
import com.bkahlert.kommons.auth.TokenInfo
import com.bkahlert.kommons.auth.TokenInfoStorage
import com.bkahlert.kommons.auth.UserInfo
import com.bkahlert.kommons.auth.diagnostics
import com.bkahlert.kommons.encodeBase64Url
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.ktor.JsonHttpClient
import com.bkahlert.kommons.ktor.interceptOnce
import com.bkahlert.kommons.onRight
import com.bkahlert.kommons.sha256
import com.bkahlert.kommons.uri.toUri
import com.bkahlert.kommons.uri.toUrl
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.pluginOrNull
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.util.generateNonce
import io.ktor.utils.io.CancellationException
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job
import org.w3c.dom.url.URL
import kotlin.coroutines.coroutineContext
import kotlin.js.json

/**
 * State of the [Authorization Code Flow with Proof Key for Code Exchange (PKCE)](https://auth0.com/docs/get-started/authentication-and-authorization-flow/authorization-code-flow-with-proof-key-for-code-exchange-pkce)
 * using the specified [authorizationServer].
 */
@Suppress("LongLine")
public sealed class AuthorizationCodeFlowState(
    protected open val authorizationServer: OAuth2AuthorizationServer,
    protected open val clientId: String,
) {

    private data class Unauthorized(
        override val authorizationServer: OAuth2AuthorizationServer,
        override val clientId: String,
    ) : AuthorizationCodeFlowState(authorizationServer, clientId), Session.UnauthorizedSession {
        private val logger by ConsoleLogging

        public override suspend fun authorize(): Session {
            logger.info("Preparing authorization with ${authorizationServer.authorizationEndpoint}")
            val state = generateNonce() + generateNonce()
            val codeVerifier = generateNonce() + generateNonce()
            sessionStorage.setItem("codeVerifier-$state", codeVerifier)
            val codeChallenge = sha256(codeVerifier).encodeBase64Url()

            val authorizationUrl = URLBuilder(authorizationServer.authorizationEndpoint.toUrl()).apply {
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
            coroutineContext.job.cancel("Redirection to ${authorizationServer.authorizationEndpoint}")
            return resolve(authorizationServer, clientId)
        }
    }

    /**
     * Intermediate state in the authorization code flow
     * that is finalized by [getTokens].
     */
    private data class Authorizing(
        override val authorizationServer: OAuth2AuthorizationServer,
        override val clientId: String,
        val tokensStorage: TokenInfoStorage,
        val authorizationCode: String,
        val state: String?,
    ) : AuthorizationCodeFlowState(authorizationServer, clientId) {
        private val logger by ConsoleLogging

        suspend fun getTokens(): Authorized {
            logger.info("Authorization code received: $authorizationCode")
            window.history.replaceState(json(), document.title, "/")
            val state = checkNotNull(state) { "Failed to validate code due to missing state parameter" }
            logger.debug("Checking state $state")
            val codeVerifier = sessionStorage.getItem("codeVerifier-$state")
            sessionStorage.removeItem("codeVerifier-$state")
            checkNotNull(codeVerifier) { "Code is not valid" }

            logger.info("Getting tokens using code verifier: $codeVerifier")
            val tokenInfo: TokenInfo = JsonHttpClient().submitForm(
                url = authorizationServer.tokenEndpoint.toString(),
                formParameters = Parameters.build {
                    append("grant_type", "authorization_code")
                    append("client_id", clientId)
                    append("code", authorizationCode)
                    append("code_verifier", codeVerifier)
                    append("redirect_uri", window.location.origin)
                }
            ) { expectSuccess = true }.body()
            tokensStorage.set(authorizationServer.issuer, clientId, tokenInfo)
            logger.info("Authorization successful")

            return Authorized(authorizationServer, clientId, tokensStorage)
        }
    }

    private data class Authorized(
        override val authorizationServer: OAuth2AuthorizationServer,
        override val clientId: String,
        private val tokensStorage: TokenInfoStorage,
    ) : AuthorizationCodeFlowState(authorizationServer, clientId), Session.AuthorizedSession {
        private val logger by ConsoleLogging
        private val tokenInfo get() = tokensStorage.get(authorizationServer.issuer, clientId) ?: error("Failed to load tokens")

        /** User information contained in the cached [IdToken] */
        override val userInfo: UserInfo get() = tokenInfo.idToken?.payload ?: error("Failed to load ID token")
        override val diagnostics: Map<String, String?> get() = tokenInfo.diagnostics

        override fun installAuth(
            config: HttpClientConfig<HttpClientEngineConfig>,
            vararg resources: OAuth2ResourceServer,
        ): Unit = config.install(Auth) {
            bearer {
                loadTokens {
                    tokensStorage.get(
                        provider = authorizationServer.issuer,
                        clientId = clientId,
                    )?.bearerTokens
                }
                refreshTokens {
                    reauthorize(
                        refreshToken = oldTokens?.refreshToken?.let(::RefreshToken),
                        httpClient = client,
                    ) {
                        markAsRefreshTokenRequest()
                    }.bearerTokens
                }
                sendWithoutRequest { request ->
                    val uri = request.url.build().toUri()
                    resources.any { it.matches(uri) }
                }
            }
        }

        override suspend fun reauthorize(httpClient: HttpClient?): Session {
            reauthorize(
                refreshToken = tokensStorage.get(authorizationServer.issuer, clientId)?.refreshToken,
                httpClient = httpClient ?: JsonHttpClient(),
            )
            return this
        }

        override suspend fun unauthorize(httpClient: HttpClient?): Session.UnauthorizedSession {
            if (httpClient != null) {
                logger.info("Clearing tokens used by $httpClient")
                httpClient.pluginOrNull(Auth)?.providers?.removeAll { authProvider ->
                    if (authProvider is BearerAuthProvider) {
                        logger.debug("Clearing tokens used by $authProvider")
                        authProvider.clearToken()
                        true
                    } else {
                        false
                    }
                }
            }
            return revokeTokens()
        }

        /**
         * Refreshes the authorization using a cached or explicitly provided [refreshToken],
         * using a default or explicitly provided [httpClient],
         * and the optional [onSubmit] applied to the [HttpRequestBuilder].
         *
         * If no [refreshToken] is provided nor cached, or if
         * the refresh token has expired, the user is presented with
         * a primary authentication prompt.
         */
        public suspend fun reauthorize(
            refreshToken: RefreshToken?,
            httpClient: HttpClient,
            onSubmit: HttpRequestBuilder.() -> Unit = {},
        ): TokenInfo {
            if (refreshToken == null) {
                logger.info("No refresh token found. Re-authorizing ...")
                Unauthorized(authorizationServer, clientId).authorize()
                throw CancellationException("Re-authorization due to **missing** refresh token")
            }

            val response: HttpResponse = httpClient
                .interceptOnce { context ->
                    logger.info("Intercepting request to ${context.url.build()}")
                    if (context.headers.contains(HttpHeaders.Authorization)) {
                        context.headers.remove(HttpHeaders.Authorization)
                        logger.warn("Removed ${HttpHeaders.Authorization}")
                    }
                    execute(context)
                }.submitForm(
                    url = authorizationServer.tokenEndpoint.toString(),
                    formParameters = Parameters.build {
                        append("grant_type", "refresh_token")
                        append("client_id", clientId)
                        append("redirect_uri", window.location.origin)
                        append("refresh_token", refreshToken.token)
                    }
                ) {
                    onSubmit()
                    expectSuccess = false
                }

            return when (response.status) {
                HttpStatusCode.OK -> {
                    val refreshedTokenInfo: TokenInfo = response.body<TokenInfo>().let { tokenInfo ->
                        if (tokenInfo.refreshToken != null) tokenInfo // refresh token rotation activated
                        else tokenInfo.copy(refreshToken = RefreshToken(refreshToken.token)) // no token rotation, so keep current refresh token
                    }

                    tokensStorage.set(authorizationServer.issuer, clientId, refreshedTokenInfo)
                    refreshedTokenInfo
                }

                else -> {
                    val text = response.bodyAsText()
                    kotlin.runCatching {
                        val errorResponse = kotlin.runCatching {
                            LenientJson.decodeFromString(ErrorResponse.serializer(), text)
                        }.getOrElse {
                            error("Failed to decode response: $text")
                        }
                        check(errorResponse.error == "invalid_grant") { "Unexpected response: $errorResponse" }
                    }.getOrElse {
                        throw IllegalStateException("Failed to refresh token", it)
                    }
                    logger.info("Refresh token expired. Re-authorizing ...")
                    Unauthorized(authorizationServer, clientId).authorize()
                    throw CancellationException("Re-authorization due to **expired** refresh token")
                }
            }
        }

        suspend fun revokeTokens(): Session.UnauthorizedSession {
            logger.info("Revoking tokens")

            val tokenInfo = tokensStorage.get(authorizationServer.issuer, clientId)
            if (tokenInfo == null) {
                logger.info("Token storage already empty. No tokens to revoke.")
            } else {
                tokensStorage.remove(authorizationServer.issuer, clientId)
                logger.info("Token storage cleared")

                val revocationEndpoint = authorizationServer.revocationEndpoint
                if (revocationEndpoint == null) {
                    logger.warn("Tokens can't be revoked, because no revocation endpoint is provided.")
                } else {
                    when (val refreshToken = tokenInfo.refreshToken) {
                        null -> {
                            val accessToken = tokenInfo.accessToken
                            logger.info("No refresh token found. Revoking access token ${accessToken.truncated}")
                            OpenIDConnectOperations.revokeToken(accessToken, clientId, revocationEndpoint)
                        }

                        else -> {
                            logger.info("Revoking refresh token ${refreshToken.truncated}")
                            OpenIDConnectOperations.revokeToken(refreshToken, clientId, revocationEndpoint)
                        }
                    }.onRight {
                        logger.error("Token revocation failed", it)
                    }
                }
            }

            return when (val resolvedState = resolve(authorizationServer, clientId)) {
                is Session.UnauthorizedSession -> resolvedState
                else -> error("Unexpected resolved state: $resolvedState")
            }
        }
    }

    public companion object {

        private val metadataCache = StorageOpenIDProviderMetadataCache(localStorage)
        private val tokenInfoStorage = StorageTokenInfoStorage(localStorage)

        /**
         * Resolves the current [Session] using
         * the specified [authorizationServer]
         * and [clientId].
         */
        public suspend fun resolve(
            openIDProvider: OpenIDProvider,
            clientId: String,
        ): Session {
            val metadata = openIDProvider.loadOpenIDConfiguration(metadataCache)
            val authServer = OAuth2AuthorizationServer.from(openIDProvider, metadata)
            return resolve(authServer, clientId)
        }

        /**
         * Resolves the current [Session] using
         * the specified [authorizationServer]
         * and [clientId].
         */
        public suspend fun resolve(
            authorizationServer: OAuth2AuthorizationServer,
            clientId: String,
        ): Session {

            val searchParams = URL(window.location.href).searchParams
            val authorizationCode = searchParams.get("code")

            val resolved = if (authorizationCode != null) {
                // Finish authorization code flow
                Authorizing(
                    authorizationServer = authorizationServer,
                    clientId = clientId,
                    tokensStorage = tokenInfoStorage,
                    authorizationCode = authorizationCode,
                    state = searchParams.get("state"),
                ).getTokens()
            } else {
                when (tokenInfoStorage.get(authorizationServer.issuer, clientId)?.bearerTokens) {
                    null -> Unauthorized(authorizationServer, clientId)
                    else -> Authorized(authorizationServer, clientId, tokenInfoStorage)
                }
            }

            return resolved
        }
    }
}

public val TokenInfo.bearerTokens: BearerTokens?
    get() {
        if (!tokenType.equals("bearer", ignoreCase = true)) return null
        return refreshToken?.let { BearerTokens(accessToken.token, it.token) }
    }
