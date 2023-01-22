package demos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.Loader
import com.bkahlert.kommons.randomString
import com.bkahlert.semanticui.custom.Demo
import com.bkahlert.semanticui.custom.Demos
import org.jetbrains.compose.web.dom.Text

@Composable
fun LoaderDemos() {
    Demos("Loader") {

        Demo("Successful loader") {
            Loader("random string", { randomString() }) {
                Text("Loaded: $it")
            }
        }

        Demo("Initially failing loader") {
            var failCount by mutableStateOf(3)
            Loader("random string", {
                require(failCount == 0) { "Failing for another ${--failCount} times" }
                randomString()
            }) {
                Text("Loaded: $it")
            }
        }

        Demo("Always failing loader") {
            Loader("random string", { error("Will always fail") }) { }
        }
    }
}
