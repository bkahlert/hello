package app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.hello.client.UserInfoApiClient
import com.bkahlert.kommons.json.LenientAndPretty
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

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
