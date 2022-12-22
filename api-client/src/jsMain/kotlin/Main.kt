import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.web.MyProductRelease
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.browser.localStorage
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

sealed class AuthenticationState {
    object Unauthenticated : AuthenticationState()
}

val x: MyProductRelease = MyProductRelease("version", "release")


fun main() {

    var count: Int by mutableStateOf(0)
    var status: String by mutableStateOf("—")

    console.log("YEAH", x)

    val tokensStorage = BearerTokensStorage.from(localStorage)

    var restClient by mutableStateOf(
        if (tokensStorage.load() == null) {
            HttpClient(Js) {
                install(ContentNegotiation) {
                    json(JsonSerializer)
                }
            }
        } else {
            authenticatedClient(
                clientId = clientId,
                authorizationEndpoint = authorizationEndpoint,
                tokenEndpoint = tokenEndpoint,
                tokensStorage = tokensStorage,
            )
        }
    )

    renderComposable(rootElementId = "root") {
        Div({ style { padding(25.px) } }) {
            val coroutineScope = rememberCoroutineScope()
            Button(attrs = {
                onClick {
                    coroutineScope.launch {
                        restClient = authorizationCodeFlow(
                            clientId = clientId,
                            authorizationEndpoint = authorizationEndpoint,
                            tokenEndpoint = tokenEndpoint,
                        )
                    }
                }
            }) {
                Text("GET hello-dev")
            }
        }

        Div({ style { padding(25.px) } }) {
            val coroutineScope = rememberCoroutineScope()

            Button(attrs = {
                onClick {
                    coroutineScope.launch {
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
                    coroutineScope.launch {
                        val response = restClient.get("https://api.hello-dev.aws.choam.de/props/foo")
                        val text = response.bodyAsText()
                        console.info("Response: $text")
                        status = text
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
