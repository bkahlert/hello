import HelloClient.LoggedIn
import HelloClient.LoggedOut
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.SimpleLogger
import com.bkahlert.kommons.ktor.OAuthAuthorizationState
import com.bkahlert.kommons.ktor.OAuthAuthorizationState.Authorized
import com.bkahlert.kommons.ktor.OAuthAuthorizationState.Authorizing
import com.bkahlert.kommons.ktor.OAuthAuthorizationState.Unauthorized
import com.bkahlert.kommons.ktor.OAuthIdentityProvider
import com.bkahlert.kommons.ktor.OAuthResource
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

val config = mapOf(
    "sls-hello-dev-DomainNameHttp" to "api.hello-dev.aws.choam.de",
    "sls-hello-dev-HostedUiUrl" to "https://hello-dev-bkahlert-com.auth.eu-central-1.amazoncognito.com",
    "sls-hello-dev-WebAppClientID" to "7lhdbv12q1ud9rgg7g779u8va7",
)
private val apiHost: String = "${config["sls-hello-dev-DomainNameHttp"]}"
private val apiUrl: String = "https://$apiHost"
private val hostedUiUrl: String = config["sls-hello-dev-HostedUiUrl"]!!
private val userinfoEndpoint = "$hostedUiUrl/oauth2/userInfo"

val cognitoIdentityProvider = OAuthIdentityProvider(
    identifier = "https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_2kcGMqneE",
    clientId = config["sls-hello-dev-WebAppClientID"]!!,
    authorizationEndpoint = "$hostedUiUrl/oauth2/authorize",
    tokenEndpoint = "$hostedUiUrl/oauth2/token",
    revokeEndpoint = "$hostedUiUrl/oauth2/revoke",
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

        private class SubResource(baseUrl: String, path: String) : OAuthResource {
            val url: String = "$baseUrl/$path"
            override val name: String = path
            override fun matches(url: Url): Boolean = url.toString().startsWith(this.url)
            override fun toString(): String = url
        }
    }

    companion object {
        suspend fun resolve(
            identityProvider: OAuthIdentityProvider = cognitoIdentityProvider,
            apiEndpoint: String = apiUrl,
        ): HelloClient {
            return resolve(OAuthAuthorizationState.of(identityProvider), apiEndpoint)
        }

        private suspend fun resolve(
            authorizationState: OAuthAuthorizationState,
            apiEndpoint: String,
        ): HelloClient {
            return when (authorizationState) {
                is Unauthorized -> LoggedOut(authorizationState)
                is Authorizing -> LoggedIn(authorizationState.getTokens(), apiEndpoint)
                is Authorized -> LoggedIn(authorizationState, apiEndpoint)
            }
        }
    }
}


suspend fun main() {

    val logger = SimpleLogger("main")

    var helloClient by mutableStateOf(HelloClient.resolve())
    logger.debug("State of authorization $helloClient")

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
