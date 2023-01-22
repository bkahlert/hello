package playground.app.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.client.Environment
import com.bkahlert.hello.client.HelloClient
import com.bkahlert.hello.client.HelloClientConfig
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import playground.app.DispatchingLoader
import playground.app.LoadedApp

@Composable
fun AppDemos() {
    Demos("App") {
        Demo("App") {
            DispatchingLoader("environment", { Environment.load() }) { environment ->
                var client: HelloClient? by remember { mutableStateOf(null) }
                when (val currentClient = client) {
                    null -> DispatchingLoader("application", { HelloClient.load(HelloClientConfig.fromEnvironment(environment)) }) { client = it }
                    else -> LoadedApp(currentClient) { client = it }
                }
            }
        }
    }
}
