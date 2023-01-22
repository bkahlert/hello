package app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.hello.client.HelloClient
import com.bkahlert.hello.client.HelloClient.Failed
import com.bkahlert.hello.client.HelloClient.LoggedIn
import com.bkahlert.hello.client.HelloClient.LoggedOut
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
import org.jetbrains.compose.web.dom.Text

@Composable
fun LoadedApp(
    client: HelloClient,
    onLoggedOut: (HelloClient) -> Unit,
) {
    val clientScope = rememberCoroutineScope()
    var status: String by mutableStateOf("—")

    Text("Your are ${client::class.simpleName}")
    when (client) {
        is Failed -> {
            UserInfo(client.userInfo) { status = it }
        }

        is LoggedOut -> {
            UserInfo(client.userInfo) { status = it }

            Button(attrs = {
                onClick {
                    clientScope.launch {
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
                    clientScope.launch {
                        onLoggedOut(client.logOut())
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
                                clientScope.launch {
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
                                clientScope.launch {
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
                                clientScope.launch {
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
                                clientScope.launch {
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

    Pre {
        Code {
            Text(status)
        }
    }
}
