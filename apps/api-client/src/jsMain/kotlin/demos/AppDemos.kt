package demos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.LoadedApp
import app.Loader
import com.bkahlert.hello.client.Environment
import com.bkahlert.hello.client.HelloClient
import com.bkahlert.hello.client.HelloClientConfig
import com.bkahlert.semanticui.custom.Demo
import com.bkahlert.semanticui.custom.Demos
import org.jetbrains.compose.web.dom.Div

@Composable
fun AppDemos() {
    Demos("App") {
        Demo("App") {
            Loader("environment", { Environment.load() }) { environment ->
                var client: HelloClient? by mutableStateOf(null)
                Div {
                    when (val c = client) {
                        null -> Loader("app", { HelloClient.load(HelloClientConfig.fromEnvironment(environment)) }) { client = it }
                        else -> {
                            LoadedApp(c) { client = it }
                            console.log("Loaded $environment")
                            console.log("Loaded $client")
                        }
                    }
                }
            }
        }
    }
}
