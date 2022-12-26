import HelloClient.Anonymous
import HelloClient.LoggedIn
import HelloClient.LoggedOut
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.hello.client.Config
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
import com.bkahlert.kommons.serialization.JsonSerializer
import com.bkahlert.kommons.serialization.serialize
import com.bkahlert.kommons.text.simpleKebabCasedName
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

val manualConfig = Config(
    openIdProvider = "https://hello-dev-bkahlert-com.auth.eu-central-1.amazoncognito.com",
    clientId = "7lhdbv12q1ud9rgg7g779u8va7",
    apiDomain = "api.hello-dev.aws.choam.de",
)
val manualConfig2 = Config(
    openIdProvider = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_o3rAtwpX5",
    clientId = "6874dpnigtpb9uht7osf0ijn6b",
    apiDomain = null,
    apiPath = "/api",
)

sealed class HelloClient {

    data class Anonymous(
        private val apiEndpoint: String,
    ) : HelloClient() {
        private val httpClient = HttpClient(Js) {
            install(ContentNegotiation) { json(JsonSerializer) }
        }

        suspend fun echo(message: String): String {
            val response = httpClient.post("$apiEndpoint/echo") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("message" to message))
            }
            return response.bodyAsText()
        }
    }

    data class LoggedOut(
        private val authorizationState: Unauthorized,
    ) : HelloClient() {
        suspend fun logIn() {
            authorizationState.authorize()
        }
    }

    data class LoggedIn(
        private val authorizationState: Authorized,
        private val apiEndpoint: String,
    ) : HelloClient() {
        private val propsEndpoint = SubResource(apiEndpoint, "prop")
        private val httpClient = authorizationState.buildClient(propsEndpoint)

        suspend fun getProps(): String {
            val response = httpClient.get("$apiEndpoint/props")
            return response.bodyAsText()
        }

        suspend fun getProp(id: String): String {
            val response = httpClient.get("$apiEndpoint/props/$id")
            return response.bodyAsText()
        }

        suspend fun setProp(id: String, value: JsonObject): String {
            val response = httpClient.post("$apiEndpoint/props") {
                setBody(buildMap {
                    value.keys.forEach { put(it, value[it]) }
                    put("propId", JsonPrimitive(id))
                }.serialize())
            }
            return response.bodyAsText()
        }

        suspend fun logOut(): HelloClient =
            resolve(authorizationState.revokeTokens(httpClient), apiEndpoint)

        private class SubResource(baseUrl: String, path: String) : OAuth2Resource {
            val url: String = "$baseUrl/$path"
            override val name: String = path
            override fun matches(url: Url): Boolean = url.toString().startsWith(this.url)
            override fun toString(): String = url
        }
    }

    companion object {

        private val logger = HelloClient.simpleLogger()

        suspend fun resolve(
            config: Config?,
        ): HelloClient {
            if (config == null) return Anonymous("/api")

            val metadata = OpenIDProvider(config.openIdProvider).loadOpenIDConfiguration()
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
                is Unauthorized -> LoggedOut(authorizationState)
                is Authorizing -> LoggedIn(authorizationState.getTokens(), apiEndpoint)
                is Authorized -> LoggedIn(authorizationState, apiEndpoint)
            }
            logger.debug("$helloClient")
            return helloClient
        }
    }
}


suspend fun main() {

    val logger = SimpleLogger("main")

    var helloClient by mutableStateOf(HelloClient.resolve(manualConfig2))

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
                    Div({ style { padding(25.px) } }) {

                        Button(attrs = {
                            onClick {
                                helloClientScope.launch {
                                    val response = client.echo("foo bar")
                                    console.info("Response: $response")
                                    status = response
                                }
                            }
                        }) {
                            Text("echo")
                        }
                    }
                }

                is LoggedOut -> {
                    Text("Your are ${helloClient::class.simpleKebabCasedName}")
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
