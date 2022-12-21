import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
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
import org.w3c.dom.Storage
import org.w3c.dom.url.URL
import kotlin.js.Promise
import kotlin.js.json

/**
 * Storage for persisting bearer tokens.
 */
interface BearerTokensStorage {
    fun load(): BearerTokens?
    fun save(tokens: BearerTokens): BearerTokens
    fun save(accessToken: String, refreshToken: String): BearerTokens = save(BearerTokens(accessToken, refreshToken))
    fun save(accessToken: String): BearerTokens {
        val currentTokens = checkNotNull(load()) { "Failed to update access token because tokens are missing" }
        return save(accessToken, currentTokens.refreshToken)
    }

    companion object {
        fun from(storage: Storage, key: String = "tokens") = object : BearerTokensStorage {
            override fun load(): BearerTokens? = storage.getItem(key)
                ?.split("\n")
                ?.let { (accessToken, refreshToken) ->
                    BearerTokens(accessToken, refreshToken)
                }

            override fun save(tokens: BearerTokens): BearerTokens = tokens.apply {
                console.warn("Saving $accessToken\n$refreshToken")
                storage.setItem(key, "$accessToken\n$refreshToken")
                console.warn("Saved ${load()}")
            }
        }
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
    tokensStorage: BearerTokensStorage = BearerTokensStorage.from(localStorage),
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
            tokensStorage.save(tokenInfo.accessToken, checkNotNull(tokenInfo.refreshToken) { "Refresh token missing" })
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
    tokensStorage: BearerTokensStorage,
    logger: Logger = KtorSimpleLogger("AuthenticatedClient"),
) = HttpClient(Js) {
    install(ContentNegotiation) {
        json(JsonSerializer)
    }
    install(Auth) {
        bearer {
            loadTokens { tokensStorage.load().also { console.log("Loaded tokens", it) } }
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
                tokensStorage.load()!!
            }
            sendWithoutRequest { request ->
                request.url.buildString().let {
                    it.startsWith(authorizationEndpoint) || it.startsWith(tokenEndpoint) || it.contains("api.hello")
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

val config = mapOf(
    "sls-hello-dev-DomainNameHttp" to "api.hello-dev.aws.choam.de",
    "sls-hello-dev-HostedUiUrl" to "https://hello-dev-bkahlert-com.auth.eu-central-1.amazoncognito.com",
    "sls-hello-dev-WebAppClientID" to "7lhdbv12q1ud9rgg7g779u8va7",
)
val apiUrl: String = "https://${config["sls-hello-dev-DomainNameHttp"]}"
val hostedUiUrl: String = config["sls-hello-dev-HostedUiUrl"]!!
val clientId: String = config["sls-hello-dev-WebAppClientID"]!!

val authorizationEndpoint = "$hostedUiUrl/oauth2/authorize"
val tokenEndpoint = "$hostedUiUrl/oauth2/token"
val userinfoEndpoint = "$hostedUiUrl/oauth2/userInfo"

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
