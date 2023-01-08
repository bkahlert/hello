import HelloClient.Anonymous
import HelloClient.Config
import HelloClient.LoggedIn
import HelloClient.LoggedOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.SimpleLogger
import com.bkahlert.kommons.SimpleLogger.Companion.simpleLogger
import com.bkahlert.kommons.auth.OAuth2AuthorizationServer
import com.bkahlert.kommons.auth.OAuth2AuthorizationState
import com.bkahlert.kommons.auth.OAuth2AuthorizationState.Authorized
import com.bkahlert.kommons.auth.OAuth2AuthorizationState.Authorizing
import com.bkahlert.kommons.auth.OAuth2AuthorizationState.Unauthorized
import com.bkahlert.kommons.auth.OAuth2Resource
import com.bkahlert.kommons.auth.OpenIDProvider
import com.bkahlert.kommons.auth.loadOpenIDConfiguration
import com.bkahlert.kommons.ktor.JsonHttpClient
import com.bkahlert.kommons.ktor.installTokenAuth
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.serialization.JsonSerializer
import com.bkahlert.kommons.serialization.serialize
import com.bkahlert.kommons.text.simpleKebabCasedName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import kotlin.properties.Delegates
import kotlin.reflect.KClass

open class Environment(
    private val url: String,
    private val httpClient: HttpClient = JsonHttpClient(),
) {
    private suspend fun load() = runCatching {
        httpClient.get(url) { expectSuccess = true }.body<JsonObject>()
    }.getOrElse {
        console.error("Error loading $url", it.message)
        JsonObject(emptyMap())
    }

    private var environment: JsonObject? = null
    suspend fun getJsonElement(key: String): JsonElement? {
        val env = environment ?: run { load().also { environment = it } }
        return env[key]
    }

    suspend inline fun <reified T> get(key: String): T? =
        getJsonElement(key)?.let { JsonSerializer.decodeFromJsonElement(it) }

    companion object : Environment("./environment.json")
}

abstract class ApiClient(
    val endpoint: String,
    val httpClient: HttpClient,
) {
    val url = endpointToUrl(endpoint)

    companion object {
        fun endpointToUrl(endpoint: String): String =
            endpoint.takeUnless { it.startsWith("/") } ?: "${window.location.protocol}//${window.location.hostname}$endpoint"
    }
}

class ClickUpClient(
    endpoint: String,
    httpClient: HttpClient,
) : ApiClient(endpoint, httpClient) {
    suspend fun clickUp(): String {
        val response = httpClient.get(url)
        return response.bodyAsText()
    }
}

class UserInfoClient(
    endpoint: String,
    httpClient: HttpClient,
) : ApiClient(endpoint, httpClient) {
    suspend fun info(): JsonObject = httpClient.get(url).body()
}

class UserPropsClient(
    endpoint: String,
    httpClient: HttpClient,
) : ApiClient(endpoint, httpClient) {
    suspend fun getProps(): String {
        val response = httpClient.get(url)
        return response.bodyAsText()
    }

    suspend inline fun <reified T> getProp(id: String): T =
        httpClient.get("$url/$id").body<JsonElement>().let { Json.decodeFromJsonElement(it) }

    suspend fun setProp(id: String, value: JsonObject): String {
        val response = httpClient.patch("$url/$id") {
            contentType(ContentType.Application.Json)
            setBody(value)
        }
        return response.bodyAsText()
    }
}

sealed class HelloClient(
    val apiClients: Map<KClass<out ApiClient>, String?>,
    val httpClient: HttpClient = JsonHttpClient(),
) {
    val userInfo: UserInfoClient? = apiClients[UserInfoClient::class]?.let { UserInfoClient(it, httpClient) }

    override fun toString(): String = this::class.simpleName.toString()

    class Anonymous(
        apiClients: Map<KClass<out ApiClient>, String?>,
    ) : HelloClient(apiClients)

    class LoggedOut(
        apiClients: Map<KClass<out ApiClient>, String?>,
        private val authorizationState: Unauthorized,
    ) : HelloClient(apiClients) {
        suspend fun logIn(): OAuth2AuthorizationState = authorizationState.authorize()
    }

    class LoggedIn(
        apiClients: Map<KClass<out ApiClient>, String?>,
        private val authorizationState: Authorized,
    ) : HelloClient(
        apiClients = apiClients,
        httpClient = JsonHttpClient {
            authorizationState.installAuth(this, object : OAuth2Resource {
                override val name: String get() = "api"
                private val endpointUrls = apiClients.values.filterNotNull().map { ApiClient.endpointToUrl(it) }
                override fun matches(url: Url): Boolean = url.toString().let { endpointUrls.any { endpointUrl -> it.startsWith(endpointUrl) } }
            })
        },
    ) {

        val userProps: UserPropsClient? = apiClients[UserPropsClient::class]?.let { UserPropsClient(it, httpClient) }

        var clickUpApiToken: String? by Delegates.observable(null) { _, _, token ->
            clickUp = when (token) {
                null -> null
                else -> apiClients[ClickUpClient::class]?.let {
                    logger.info("Initializing ClickUp client with $token")
                    ClickUpClient(it, JsonHttpClient { installTokenAuth(token) })
//                    null
                }
            }
        }

        var clickUp: ClickUpClient? = null
            private set

        suspend fun logOut(): HelloClient =
            resolve(authorizationState.revokeTokens(httpClient), apiClients)
    }

    companion object {

        private val logger = HelloClient.simpleLogger()

        suspend fun resolve(
            config: Config,
        ): HelloClient {
            logger.info("Config: $config")
            if (config.openIDProvider == null || config.clientId == null) return Anonymous(config.apiClients)

            val metadata = OpenIDProvider(config.openIDProvider).loadOpenIDConfiguration()
            val authServer = OAuth2AuthorizationServer.from(metadata)
            return resolve(authServer, config.clientId, config.apiClients)
        }

        suspend fun resolve(
            authServer: OAuth2AuthorizationServer,
            clientId: String,
            apiClients: Map<KClass<out ApiClient>, String?>,
        ): HelloClient {
            val authorizationState = OAuth2AuthorizationState.compute(authServer, clientId)
            return resolve(authorizationState, apiClients)
        }

        private suspend fun resolve(
            authorizationState: OAuth2AuthorizationState,
            apiClients: Map<KClass<out ApiClient>, String?>,
        ): HelloClient {
            val helloClient = when (authorizationState) {
                is Unauthorized -> LoggedOut(apiClients, authorizationState)
                is Authorizing -> LoggedIn(apiClients, authorizationState.getTokens())
                is Authorized -> LoggedIn(apiClients, authorizationState)
            }
            if (helloClient is LoggedIn) {
                logger.info("Getting ClickUp API token")
                val clickUpApiToken = helloClient.userProps?.getProp<String?>("clickup.api-token")
                logger.info("ClickUp API token: $clickUpApiToken")
                helloClient.clickUpApiToken = clickUpApiToken
                logger.info("Set: $clickUpApiToken")
            }
            logger.debug("$helloClient")
            return helloClient
        }
    }

    data class Config(
        val openIDProvider: String?,
        val clientId: String?,
        val apiClients: Map<KClass<out ApiClient>, String?>,
    ) {
        constructor(
            openIDProvider: String?,
            clientId: String?,
            vararg apiClients: Pair<KClass<out ApiClient>, String?>,
        ) : this(openIDProvider, clientId, mapOf(*apiClients))
    }
}


@Suppress("UNREACHABLE_CODE")
suspend fun main() {

    val logger = SimpleLogger("main")

    val helloClientConfig = Config(
        openIDProvider = Environment.get("USER_POOL_PROVIDER_URL"),
        clientId = Environment.get("USER_POOL_CLIENT_ID"),
        ClickUpClient::class to Environment.get("CLICK_UP_API_ENDPOINT"),
        UserInfoClient::class to Environment.get("USER_INFO_API_ENDPOINT"),
        UserPropsClient::class to Environment.get("USER_PROPS_API_ENDPOINT"),
    )
    var helloClient by mutableStateOf(HelloClient.resolve(helloClientConfig))

    var count: Int by mutableStateOf(0)
    var status: String by mutableStateOf("—")

    renderComposable(rootElementId = "root") {

        Div({ style { padding(25.px) } }) {

            val helloClientScope = rememberCoroutineScope()

            when (val client = helloClient.also {
                logger.info("${it::class.simpleKebabCasedName}")
            }) {
                is Anonymous -> {
                    Text("Your are ${helloClient::class.simpleKebabCasedName}")
                    UserInfo(client.userInfo) { status = it }
                }

                is LoggedOut -> {
                    Text("Your are ${helloClient::class.simpleKebabCasedName}")
                    UserInfo(client.userInfo) { status = it }

                    Button(attrs = {
                        onClick {
                            helloClientScope.launch {
                                client.logIn()
                            }
                        }
                    }) {
                        Text("Log in")
                    }
                }

                is LoggedIn -> {
                    Text("Your are ${helloClient::class.simpleKebabCasedName}")
                    UserInfo(client.userInfo) { status = it }

                    Button(attrs = {
                        onClick {
                            helloClientScope.launch {
                                helloClient = client.logOut()
                            }
                        }
                    }) {
                        Text("Log out")
                    }

                    when (val clickUpClient = client.clickUp) {
                        null -> {}
                        else -> {
                            Div({ style { padding(25.px) } }) {
                                Button(attrs = {
                                    onClick {
                                        helloClientScope.launch {
                                            val response = clickUpClient.clickUp()
                                            console.info("Response: $response")
                                            status = response
                                        }
                                    }
                                }) {
                                    Text("GET clickup")
                                }
                            }
                        }
                    }

                    when (val userPropsClient = client.userProps) {
                        null -> {}
                        else -> {
                            Div({ style { padding(25.px) } }) {

                                Button(attrs = {
                                    onClick {
                                        helloClientScope.launch {
                                            val response = userPropsClient.getProps()
                                            console.info("Response: $response")
                                            status = response
                                        }
                                    }
                                }) {
                                    Text("GET props")
                                }

                                Button(attrs = {
                                    onClick {
                                        helloClientScope.launch {
                                            val response = userPropsClient.getProp<JsonElement>("foo")
                                            console.info("Response: $response")
                                            status = response.serialize(true)
                                        }
                                    }
                                }) {
                                    Text("GET props/foo")
                                }

                                Button(attrs = {
                                    onClick {
                                        helloClientScope.launch {
                                            val response = userPropsClient.setProp(
                                                "foo",
                                                JsonObject(
                                                    mapOf(
                                                        "foo" to JsonPrimitive("bar"),
                                                        "baz" to JsonPrimitive(null),
                                                        "random" to JsonPrimitive(randomString()),
                                                    )
                                                )
                                            )
                                            console.info("Response: $response")
                                            status = response
                                        }
                                    }
                                }) {
                                    Text("POST props/foo")
                                }
                            }
                        }
                    }
                }
            }
        }




        Button(attrs = {
            onClick { count -= 1 }
        }) {
            Text("-")
        }

        Span({ style { padding(15.px) } }) {
            Text("$count")
        }

        Button(attrs = {
            onClick {
                count += 1
                status += "-yyyyyyyyyyyyyxxxxxxxxxxxx"
            }
        }) {
            Text("+")
        }

        Pre {
            Code {
                Text(status)
            }
        }
    }
}


@Composable
fun UserInfo(
    client: UserInfoClient?,
    onResponse: (String) -> Unit,
) {
    if (client == null) {
        Div { Text("userInfo unavailable") }
        return
    }

    val clientScope = rememberCoroutineScope()
    Div({ style { padding(25.px) } }) {
        Button(attrs = {
            onClick {
                clientScope.launch {
                    val response = client.info()
                    console.info("Response: $response")
                    onResponse(response.serialize(true))
                }
            }
        }) {
            Text("GET userInfo")
        }
    }
}
