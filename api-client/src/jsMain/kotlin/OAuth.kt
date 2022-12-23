import SimpleLogger.Companion.simpleLogger
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.provideDelegate
import com.bkahlert.kommons.serialization.JsonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.pluginOrNull
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
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

data class OAuthIdentityProvider(
    val identifier: String,
    val clientId: String,
    val authorizationEndpoint: String,
    val tokenEndpoint: String,
    val revokeEndpoint: String,
    val resources: List<OAuthResource>,
) {
    val endpoints by lazy { arrayOf(authorizationEndpoint, tokenEndpoint, revokeEndpoint) }
}

data class OAuthResource(
    /** Name of the resource. */
    val name: String,
    /** Predicate that returns whether the given URI belongs to the resource. */
    val predicate: (Url) -> Boolean,
)

/**
 * State of the [Authorization Code Flow with Proof Key for Code Exchange (PKCE)](https://auth0.com/docs/get-started/authentication-and-authorization-flow/authorization-code-flow-with-proof-key-for-code-exchange-pkce)
 * using the specified [identityProvider].
 */
@Suppress("LongLine")
sealed class OAuthAuthorizationState(
    open val identityProvider: OAuthIdentityProvider,
) {

    data class Unauthorized(
        override val identityProvider: OAuthIdentityProvider,
    ) : OAuthAuthorizationState(identityProvider) {
        private val logger = simpleLogger()
        suspend fun authorize(): OAuthAuthorizationState {
            logger.info("Preparing authorization with ${identityProvider.authorizationEndpoint}")
            val state = generateNonce()
            val codeVerifier = generateNonce()
            sessionStorage.setItem("codeVerifier-$state", codeVerifier)
            val codeChallenge = base64URLEncode(sha256(codeVerifier))

            val authorizationUrl = URLBuilder(identityProvider.authorizationEndpoint).apply {
                parameters.apply {
                    append("response_type", "code")
                    append("client_id", identityProvider.clientId)
                    append("state", state)
                    append("code_challenge_method", "S256")
                    append("code_challenge", codeChallenge)
                    append("redirect_uri", window.location.origin)
                }
            }.buildString()
            logger.info("Redirecting to $authorizationUrl")
            window.location.href = authorizationUrl
            coroutineScope { cancel("Redirection to ${identityProvider.authorizationEndpoint}") }
            return of(identityProvider)
        }
    }

    data class Authorizing(
        override val identityProvider: OAuthIdentityProvider,
        val tokensStorage: BearerTokensStorage,
        val authorizationCode: String,
        val state: String?,
    ) : OAuthAuthorizationState(identityProvider) {
        private val logger = simpleLogger()
        suspend fun getTokens(): Authorized {
            logger.info("Authorization code received: $authorizationCode")
            window.history.replaceState(json(), document.title, "/")
            val state = checkNotNull(state) { "Failed to validate code due to missing state parameter" }
            logger.debug("Checking state $state")
            val codeVerifier = sessionStorage.getItem("codeVerifier-$state")
            sessionStorage.removeItem("codeVerifier-$state")
            checkNotNull(codeVerifier) { "Code is not valid" }

            logger.info("Getting tokens")
            val tokenInfo: TokenInfo = minimalClient().submitForm(
                url = identityProvider.tokenEndpoint,
                formParameters = Parameters.build {
                    append("grant_type", "authorization_code")
                    append("client_id", identityProvider.clientId)
                    append("code", authorizationCode)
                    append("code_verifier", codeVerifier)
                    append("redirect_uri", window.location.origin)
                }
            ) { expectSuccess = true }.body()
            tokensStorage.accessToken = tokenInfo.accessToken
            tokensStorage.refreshToken = checkNotNull(tokenInfo.refreshToken) { "Refresh token missing" }
            logger.info("Authorization code flow succeeded")

            return Authorized(identityProvider, tokensStorage)
        }
    }

    data class Authorized(
        override val identityProvider: OAuthIdentityProvider,
        val tokensStorage: BearerTokensStorage,
    ) : OAuthAuthorizationState(identityProvider) {
        private val logger = simpleLogger()

        fun buildClient(config: HttpClientConfig<HttpClientEngineConfig>.() -> Unit = {}): HttpClient = minimalClient {
            install(Auth) {
                bearer {
                    loadTokens {
                        tokensStorage.bearerTokens.also { console.log("Loaded tokens", it) }
                    }
                    refreshTokens {
                        logger.info("Refreshing tokens")
                        val refreshTokenInfo: TokenInfo = this@refreshTokens.client.submitForm(
                            url = identityProvider.tokenEndpoint,
                            formParameters = Parameters.build {
                                append("grant_type", "refresh_token")
                                append("client_id", identityProvider.clientId)
                                append("redirect_uri", window.location.origin)
                                append("refresh_token", oldTokens?.refreshToken ?: "")
                            }
                        ) { markAsRefreshTokenRequest() }.body()
                        tokensStorage.accessToken = refreshTokenInfo.accessToken
                        tokensStorage.bearerTokens!!
                    }
                    sendWithoutRequest { request ->
                        val url = request.url.build()
                        identityProvider.resources.any { it.predicate(url) }
                    }
                }
            }
            config()
        }

        suspend fun revokeTokens(client: HttpClient? = null): OAuthAuthorizationState {
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
                        url = identityProvider.revokeEndpoint,
                        formParameters = Parameters.build {
                            append("token", token)
                            append("client_id", identityProvider.clientId)
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
            return of(identityProvider)
        }
    }

    companion object {

        private val logger = simpleLogger()
        private fun minimalClient(config: HttpClientConfig<HttpClientEngineConfig>.() -> Unit = {}) = HttpClient(Js) {
            install(ContentNegotiation) { json(JsonSerializer) }
            config()
        }

        fun of(identityProvider: OAuthIdentityProvider): OAuthAuthorizationState {
            val bearerTokensStorage = BearerTokensStorage(localStorage.scoped(identityProvider.identifier))

            val searchParams = URL(window.location.href).searchParams
            logger.debug("searchParams $searchParams")
            return when (val authorizationCode = searchParams.get("code")) {
                null -> {
                    when (bearerTokensStorage.bearerTokens) {
                        null -> Unauthorized(identityProvider)
                        else -> Authorized(identityProvider, bearerTokensStorage)
                    }
                }

                else -> {
                    Authorizing(
                        identityProvider,
                        bearerTokensStorage,
                        authorizationCode,
                        searchParams.get("state"),
                    )
                }
            }
        }
    }
}

class BearerTokensStorage(
    storage: Storage = Storage.of(localStorage),
) {

    private val logger = simpleLogger()

    var accessToken: String? by storage
    var refreshToken: String? by storage

    var bearerTokens: BearerTokens?
        get() = accessToken.to(refreshToken).let { (a, r) ->
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
}


@Serializable
data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null, // only if grant_type was authorization_code
    @SerialName("id_token") val idToken: String? = null,
    @SerialName("token_type") val tokenType: String, // e.g. "Bearer"
    @SerialName("expires_in") val expiresIn: Int, // e.g. 3600
)

@Serializable
data class ErrorInfo(
    @SerialName("error") val error: String,
    @SerialName("error_description") val errorDescription: String? = null,
)

/**
 * Triggers the [Authorization Code Flow with Proof Key for Code Exchange (PKCE)](https://auth0.com/docs/get-started/authentication-and-authorization-flow/authorization-code-flow-with-proof-key-for-code-exchange-pkce)
 * using the specified [clientId], [authorizationEndpoint] and [tokenEndpoint].
 */
@Suppress("LongLine")
suspend fun authorizationCodeFlow(
    clientId: String,
    authorizationEndpoint: String,
    tokenEndpoint: String,
    tokensStorage: BearerTokensStorage = BearerTokensStorage(localStorage.scoped("hello-api")),
    logger: Logger = KtorSimpleLogger("AuthorizationCodeFlow"),
): HttpClient {
    val searchParams = URL(window.location.href).searchParams
    logger.debug("searchParams $searchParams")
    return when (val authorizationCode = searchParams.get("code")) {
        null -> {
            logger.info("Preparing authorization with $authorizationEndpoint")
            val state = generateNonce()
            val codeVerifier = generateNonce()
            sessionStorage.setItem("codeVerifier-$state", codeVerifier)
            val codeChallenge = base64URLEncode(sha256(codeVerifier))

            val authorizationUrl = URLBuilder(authorizationEndpoint).apply {
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
            coroutineScope { cancel("Redirection to $authorizationEndpoint") }.unsafeCast<HttpClient>()
        }

        else -> {
            logger.info("Authorization code received: $authorizationCode")
            window.history.replaceState(json(), document.title, "/")
            val state = checkNotNull(searchParams.get("state")) { "State missing in $searchParams" }
            logger.debug("Checking state $state")
            val codeVerifier = sessionStorage.getItem("codeVerifier-$state")
            sessionStorage.removeItem("codeVerifier-$state")
            checkNotNull(codeVerifier) { "Code is not valid" }

            val client = authenticatedClient(clientId, authorizationEndpoint, tokenEndpoint, tokensStorage)

            logger.info("Getting tokens")
            val tokenInfo: TokenInfo = client.submitForm(
                url = tokenEndpoint,
                formParameters = Parameters.build {
                    append("grant_type", "authorization_code")
                    append("client_id", clientId)
                    append("code", authorizationCode)
                    append("code_verifier", codeVerifier)
                    append("redirect_uri", window.location.origin)
                }
            ).body()
            tokensStorage.accessToken = tokenInfo.accessToken
            tokensStorage.refreshToken = checkNotNull(tokenInfo.refreshToken) { "Refresh token missing" }
            logger.info("Authorization code flow succeeded")

            return client
        }
    }
}

// TODO move parameter to TokenStorage
fun authenticatedClient(
    clientId: String,
    authorizationEndpoint: String,
    tokenEndpoint: String,
    tokensStorage: BearerTokensStorage = BearerTokensStorage(localStorage.scoped("hello-api")),
    logger: Logger = KtorSimpleLogger("AuthenticatedClient"),
) = HttpClient(Js) {
    install(ContentNegotiation) {
        json(JsonSerializer)
    }
    install(Auth) {
        bearer {
            loadTokens { tokensStorage.bearerTokens.also { console.log("Loaded tokens", it) } }
            refreshTokens {
//                logger.info("Refreshing tokens")
//                val refreshTokenInfo: TokenInfo = client.submitForm(
//                    url = tokenEndpoint,
//                    formParameters = Parameters.build {
//                        append("grant_type", "refresh_token")
//                        append("client_id", clientId)
//                        append("redirect_uri", window.location.origin)
//                        append("refresh_token", oldTokens?.refreshToken ?: "")
//                    }
//                ) { markAsRefreshTokenRequest() }.body()
//                tokensStorage.save(refreshTokenInfo.accessToken)
                tokensStorage.bearerTokens!!
            }
            sendWithoutRequest { request ->
                request.url.buildString().let {
                    it.contains("api.hello")
                }
            }
        }
    }

    HttpResponseValidator {
        handleResponseExceptionWithRequest { ex, _ ->
            console.error("response validation", ex)
            throw ex
        }
    }
}

external object crypto {
    fun getRandomValues(array: Uint32Array): Uint32Array
    val subtle: SubtleCrypto
}

external class SubtleCrypto {
    fun digest(algorithm: String, data: Uint8Array): Promise<ArrayBuffer>
}

external class TextEncoder {
    fun encode(input: String): Uint8Array
}

@JsName("Array")
external object ArrayX {
    fun from(arrayLike: Uint8Array): Array<Int>
}

suspend fun sha256(str: String): ArrayBuffer {
    return crypto.subtle.digest("SHA-256", TextEncoder().encode(str)).await()
}

suspend fun generateNonce(): String {
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

fun base64URLEncode(string: ArrayBuffer): String {
    return js("btoa(String.fromCharCode.apply(null, new Uint8Array(string))).replace(/\\+/g, '-').replace(/\\//g, '_').replace(/=+$/, '')")
}
