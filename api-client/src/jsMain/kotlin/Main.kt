import OAuthAuthorizationState.Authorized
import OAuthAuthorizationState.Authorizing
import OAuthAuthorizationState.Unauthorized
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.text.simpleKebabCasedName
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
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
    resources = listOf(
        OAuthResource("api") { it.host == apiHost }
    ),
)

open class HelloClient(
    private val identityProvider: OAuthIdentityProvider,
    private val apiEndpoint: String,
) {
    val state: OAuthAuthorizationState
        get() = OAuthAuthorizationState.of(identityProvider)

    private var _httpClient: HttpClient? = null
    private suspend fun httpClient() = _httpClient ?: run {
        when (val s = state) {
            is Unauthorized -> s.authorize().unsafeCast<HttpClient>()
            is Authorizing -> s.getTokens().buildClient()
            is Authorized -> s.buildClient()
        }.also { _httpClient = it }
    }

    suspend fun getProp(id: String): String {
        val response = httpClient().get("$apiEndpoint/props/$id")
        return response.bodyAsText()
    }

    companion object : HelloClient(cognitoIdentityProvider, apiUrl)
}

fun main() {

    val logger = SimpleLogger("main")

    var authorizationState by mutableStateOf(OAuthAuthorizationState.of(cognitoIdentityProvider))
    logger.debug("State of authorization $authorizationState")

    var count: Int by mutableStateOf(0)
    var status: String by mutableStateOf("—")

    renderComposable(rootElementId = "root") {

        Div({ style { padding(25.px) } }) {
            val authorizationScope = rememberCoroutineScope()

            when (val state = authorizationState.also {
                logger.info("${it::class.simpleKebabCasedName}")
            }) {
                is Unauthorized -> {
                    Text("Your are ${state::class.simpleKebabCasedName}")
                    Button(attrs = {
                        onClick {
                            authorizationScope.launch {
                                authorizationState = state.authorize()
                            }
                        }
                    }) {
                        Text("Log in")
                    }
                }

                is Authorizing -> {
                    logger.debug("Continuing ongoing authorization")
                    Text("Your are ${state::class.simpleKebabCasedName}")
                    authorizationScope.launch {
                        authorizationState = state.getTokens()
                    }
                }

                is Authorized -> {
                    Text("Your are ${state::class.simpleKebabCasedName}")
                    Button(attrs = {
                        onClick {
                            authorizationScope.launch {
                                authorizationState = state.revokeTokens()
                            }
                        }
                    }) {
                        Text("Log out")
                    }

                    Div({ style { padding(25.px) } }) {

                        val restClient: HttpClient by mutableStateOf(state.buildClient())
                        val restClientScope = rememberCoroutineScope()

                        Button(attrs = {
                            onClick {
                                restClientScope.launch {
                                    val response = restClient.get("https://hello-dev.aws.choam.de")
                                    val text = response.bodyAsText()
                                    console.info("Response: $text")
                                    status = text
                                }
                            }
                        }) {
                            Text("GET hello-dev")
                        }

                        Button(attrs = {
                            onClick {
                                restClientScope.launch {
                                    val text = runCatching {
                                        val response = restClient.get("https://api.hello-dev.aws.choam.de/props/foo")
                                        response.bodyAsText()
                                    }.getOrElse {
                                        it.message
                                    }

                                    console.info("Response: $text")
                                    status = text ?: ""
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
