import HelloClient.LoggedIn
import HelloClient.LoggedOut
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.hello.client.Config
import com.bkahlert.kommons.SimpleLogger
import com.bkahlert.kommons.SimpleLogger.Companion.simpleLogger
import com.bkahlert.kommons.auth.OpenIDProvider
import com.bkahlert.kommons.ktor.OAuth2AuthorizationServer
import com.bkahlert.kommons.ktor.OAuth2AuthorizationState
import com.bkahlert.kommons.ktor.OAuth2AuthorizationState.Authorized
import com.bkahlert.kommons.ktor.OAuth2AuthorizationState.Authorizing
import com.bkahlert.kommons.ktor.OAuth2AuthorizationState.Unauthorized
import com.bkahlert.kommons.ktor.OAuth2Resource
import com.bkahlert.kommons.ktor.loadOpenIDConfiguration
import com.bkahlert.kommons.text.simpleKebabCasedName
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import kotlinx.coroutines.launch
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

sealed class HelloClient {

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

        suspend fun getProp(id: String): String {
            val response = httpClient.get("$apiEndpoint/props/$id")
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
            config: Config = Config.DEFAULT,
        ): HelloClient {
            val metadata = OpenIDProvider(config.openIdProvider).loadOpenIDConfiguration()
            val authServer = OAuth2AuthorizationServer.from(metadata)
            return resolve(authServer, config.clientId, "https://${config.apiDomain}")
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

    var helloClient by mutableStateOf(HelloClient.resolve())

    var count: Int by mutableStateOf(0)
    var status: String by mutableStateOf("—")

    renderComposable(rootElementId = "root") {

        Div({ style { padding(25.px) } }) {

            val helloClientScope = rememberCoroutineScope()

            when (val client = helloClient.also {
                logger.info("${it::class.simpleKebabCasedName}")
            }) {
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
                                    val response = client.getProp("foo")
                                    console.info("Response: $response")
                                    status = response
                                }
                            }
                        }) {
                            Text("GET api.hello-dev/props")
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
                    }
                    Pre {
                        Code {
                            Text(status)
                        }
                    }
                }
            }
        }
    }
}
