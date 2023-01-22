package playground.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.randomString
import com.bkahlert.semanticui.demo.DEMO_BASE_DELAY
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.Text
import kotlin.time.times

@Composable
fun DispatchingLoaderDemos() {
    Demos("Dispatching Loader") {

        Demo("Successful loader") {
            DispatchingLoader("random string", { delay(DEMO_BASE_DELAY); randomString() }) {
                Text("Loaded: $it")
            }
        }

        Demo("Slow loader") {
            val slowDown = 5.0
            DispatchingLoader("random string", { delay(slowDown * DEMO_BASE_DELAY); randomString() }) {
                Text("Loaded: $it")
            }
        }

        Demo("Initially failing loader") {
            var failCount by remember { mutableStateOf(3) }
            DispatchingLoader("random string", {
                delay(DEMO_BASE_DELAY);
                require(failCount == 0) { "Failing for another ${--failCount} times" }
                randomString()
            }) {
                Text("Loaded: $it")
            }
        }

        Demo("Always failing loader") {
            DispatchingLoader("random string", { delay(DEMO_BASE_DELAY); error("Will always fail") }) { }
        }
    }
}
