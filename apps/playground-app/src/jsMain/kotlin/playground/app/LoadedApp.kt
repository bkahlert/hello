package playground.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClient
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.client.http.PersonalAccessToken
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.client.HelloClient
import com.bkahlert.hello.client.HelloClient.Failed
import com.bkahlert.hello.client.HelloClient.LoggedIn
import com.bkahlert.hello.client.HelloClient.LoggedOut
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.json.LenientAndPretty
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.text.simpleKebabCasedName
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Segment
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text

@Composable
fun LoadedApp(
    client: HelloClient,
    onLoggedOut: (HelloClient) -> Unit,
) {
    val clientScope = rememberCoroutineScope()
    var status: String by remember { mutableStateOf("—") }

    S("ui three mini statistics") {
        S("statistic") {
            S("value") { Text("${client::class.simpleKebabCasedName}") }
            S("label") { Text("Status") }
        }
        S("statistic") {
            S("value") { Text(localStorage.length.toString()) }
            S("label") { Text("LocalStorage Items") }
        }
        S("statistic") {
            S("value") { Text(sessionStorage.length.toString()) }
            S("label") { Text("SessionStorage Items") }
        }
    }

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

            DispatchingLoader(
                name = "ClickUp credentials",
                load = {
                    console.info("Getting ClickUp API token")
                    val clickUpApiToken = client.userProps?.getProp<String?>("clickup.api-token")
                    console.info("ClickUp API token: $clickUpApiToken")
                    clickUpApiToken
                }) { clickUpApiToken ->

                Segment {
                    ClickUpMenu(
                        rememberClickUpMenuViewModel(
                            ClickUpHttpClientConfigurer(),
                            ClickUpTestClientConfigurer(),
                        ).apply {
                            if (clickUpApiToken != null) {
                                val clickUpClient = ClickUpHttpClient(PersonalAccessToken(clickUpApiToken), InMemoryStorage())
                                connect(clickUpClient)
                            }
                        }
                    )
                }
            }

            when (val userPropsClient = client.userProps) {
                null -> {}
                else -> {
                    Segment {
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
