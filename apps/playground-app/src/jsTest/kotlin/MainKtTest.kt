import androidx.compose.runtime.Composable
import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.semanticui.test.JQueryLibrary
import com.bkahlert.semanticui.test.SemanticUiLibrary
import com.bkahlert.semanticui.test.compositionWith
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.string.shouldStartWith
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class MainKtTest {

    @Test
    fun json_serialization() {
        LenientJson.encodeToString(JsonObject.serializer(), jsonObject) shouldEqualJson "{\"foo\":\"bar\",\"baz\":null}"
        LenientJson.encodeToString(jsonObject) shouldEqualJson "{\"foo\":\"bar\",\"baz\":null}"
    }

    @Test
    fun compose() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            Foo()
        }
        root.innerHTML shouldStartWith "<p>Bar</p>"
    }
}

val jsonObject: JsonObject = buildJsonObject {
    put("foo", "bar")
    put("baz", null)
}

@Composable
fun Foo() {
    P { Text("Bar") }
}
