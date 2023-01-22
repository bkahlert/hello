package playground.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.hello.client.UserInfoApiClient
import com.bkahlert.kommons.json.LenientAndPretty
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Segment
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    Segment {
        Button({
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
