import HelloClient.Anonymous
import HelloClient.Config
import HelloClient.LoggedIn
import HelloClient.LoggedOut
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
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.serialization.JsonSerializer
import com.bkahlert.kommons.serialization.serialize
import com.bkahlert.kommons.text.simpleKebabCasedName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.browser.window
import kotlinx.coroutines.launch
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

open class Environment(
    private val url: String,
    private val httpClient: HttpClient = HttpClient(Js) {
        install(ContentNegotiation) { json(JsonSerializer) }
    },
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

sealed class HelloClient(
    open val apiEndpoint: String,
    val httpClient: HttpClient = HttpClient(Js) {
        install(ContentNegotiation) { json(JsonSerializer) }
    },
) {

    suspend fun getUserInfo(): String {
        val response = httpClient.get("$apiEndpoint/info")
        return response.bodyAsText()
    }

    data class Anonymous(
        override val apiEndpoint: String,
    ) : HelloClient(apiEndpoint)

    data class LoggedOut(
        override val apiEndpoint: String,
        private val authorizationState: Unauthorized,
    ) : HelloClient(apiEndpoint) {
        suspend fun logIn(): OAuth2AuthorizationState = authorizationState.authorize()
    }

    data class LoggedIn(
        override val apiEndpoint: String,
        private val authorizationState: Authorized,
    ) : HelloClient(apiEndpoint, authorizationState.buildClient(object : OAuth2Resource {
        override val name: String get() = "api"
        override fun matches(url: Url): Boolean = url.toString().startsWith(apiEndpoint)
    })) {

        suspend fun getProps(): String {
            val response = httpClient.get("$apiEndpoint/props")
            return response.bodyAsText()
        }

        suspend fun getProp(id: String): String {
            val response = httpClient.get("$apiEndpoint/props/$id")
            return response.bodyAsText()
        }

        suspend fun setProp(id: String, value: JsonObject): String {
            val response = httpClient.patch("$apiEndpoint/props/$id") {
                setBody(value.serialize())
            }
            return response.bodyAsText()
        }

        suspend fun clickUp(): String {
            val response = httpClient.get("$apiEndpoint/clickup")
            return response.bodyAsText()
        }

        suspend fun logOut(): HelloClient =
            resolve(authorizationState.revokeTokens(httpClient), apiEndpoint)
    }

    companion object {

        private val logger = HelloClient.simpleLogger()

        suspend fun resolve(
            config: Config,
        ): HelloClient {
            logger.info("Config: $config")
            if (config.openIDProvider == null || config.clientId == null) return Anonymous(config.apiEndpoint)

            val metadata = OpenIDProvider(config.openIDProvider).loadOpenIDConfiguration()
            val authServer = OAuth2AuthorizationServer.from(metadata)
            return resolve(authServer, config.clientId, config.apiUrl)
        }

        suspend fun resolve(
            authServer: OAuth2AuthorizationServer,
            clientId: String,
            apiEndpoint: String,
        ): HelloClient {
            val authorizationState = OAuth2AuthorizationState.compute(authServer, clientId)
            return resolve(authorizationState, apiEndpoint)
        }

        private suspend fun resolve(
            authorizationState: OAuth2AuthorizationState,
            apiEndpoint: String,
        ): HelloClient {
            val helloClient = when (authorizationState) {
                is Unauthorized -> LoggedOut(apiEndpoint, authorizationState)
                is Authorizing -> LoggedIn(apiEndpoint, authorizationState.getTokens())
                is Authorized -> LoggedIn(apiEndpoint, authorizationState)
            }
            logger.debug("$helloClient")
            return helloClient
        }
    }

    data class Config(
        val openIDProvider: String?,
        val clientId: String?,
        val apiEndpoint: String,
    ) {
        val apiUrl = apiEndpoint.takeUnless { it.startsWith("/") } ?: "https://${window.location.hostname}$apiEndpoint"
    }
}


suspend fun main() {

    val logger = SimpleLogger("main")

    val helloClientConfig = Config(
        openIDProvider = Environment.get("USER_POOL_PROVIDER_URL"),
        clientId = Environment.get("USER_POOL_CLIENT_ID"),
        apiEndpoint = Environment.get("API_ENDPOINT") ?: "/api",
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
                    Button(attrs = {
                        onClick {
                            helloClientScope.launch {
                                val response = client.getUserInfo()
                                console.info("Response: $response")
                                status = response
                            }
                        }
                    }) {
                        Text("UserInfo")
                    }
                }

                is LoggedOut -> {
                    Text("Your are ${helloClient::class.simpleKebabCasedName}")

                    Button(attrs = {
                        onClick {
                            helloClientScope.launch {
                                val response = client.getUserInfo()
                                console.info("Response: $response")
                                status = response
                            }
                        }
                    }) {
                        Text("UserInfo")
                    }

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

                    Button(attrs = {
                        onClick {
                            helloClientScope.launch {
                                val response = client.getUserInfo()
                                console.info("Response: $response")
                                status = response
                            }
                        }
                    }) {
                        Text("UserInfo")
                    }

                    Button(attrs = {
                        onClick {
                            helloClientScope.launch {
                                helloClient = client.logOut()
                            }
                        }
                    }) {
                        Text("Log out")
                    }

                    Div({ style { padding(25.px) } }) {

                        Button(attrs = {
                            onClick {
                                helloClientScope.launch {
                                    val response = client.getProps()
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
                                    val response = client.getProp("foo")
                                    console.info("Response: $response")
                                    status = response
                                }
                            }
                        }) {
                            Text("GET props/foo")
                        }

                        Button(attrs = {
                            onClick {
                                helloClientScope.launch {
                                    val response = client.setProp(
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

                        Button(attrs = {
                            onClick {
                                helloClientScope.launch {
                                    val response = client.clickUp()
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
