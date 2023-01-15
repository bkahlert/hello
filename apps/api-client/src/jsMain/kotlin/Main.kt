import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.hello.client.Environment
import com.bkahlert.hello.client.HelloClient
import com.bkahlert.hello.client.HelloClient.Anonymous
import com.bkahlert.hello.client.HelloClient.LoggedIn
import com.bkahlert.hello.client.HelloClient.LoggedOut
import com.bkahlert.hello.client.HelloClientConfig
import com.bkahlert.hello.client.UserInfoApiClient
import com.bkahlert.kommons.json.LenientAndPretty
import com.bkahlert.kommons.randomString
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
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

@Suppress("UNREACHABLE_CODE")
suspend fun main() {

    val environment = Environment.load()
    val helloClientConfig = HelloClientConfig.fromEnvironment(environment)
    var helloClient by mutableStateOf(HelloClient.load(helloClientConfig))

    var acc: String by mutableStateOf("𓐋👋xx")
    var next: String by mutableStateOf(randomString(1))
    var status: String by mutableStateOf("—")

    renderComposable(rootElementId = "root") {

        Div({ style { padding(25.px) } }) {

            val helloClientScope = rememberCoroutineScope()

            when (val client = helloClient.also {
                Text("Your are ${helloClient::class.simpleName}")
            }) {
                is Anonymous -> {
                    UserInfo(client.userInfo) { status = it }
                }

                is LoggedOut -> {
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
                                            status = Json.LenientAndPretty.encodeToString(response)
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


        Span({ style { padding(15.px) } }) {
            Text(acc)
        }

        Button(attrs = {
            onClick {
                acc += next
                next += randomString(1)
            }
        }) {
            Text("+ $next")
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
    client: UserInfoApiClient?,
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
                    onResponse(Json.LenientAndPretty.encodeToString(response))
                }
            }
        }) {
            Text("GET userInfo")
        }
    }
}
