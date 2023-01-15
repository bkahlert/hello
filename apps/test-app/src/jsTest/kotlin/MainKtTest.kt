import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.compose.web.testutils.runTest
import org.w3c.dom.asList
import kotlin.test.Test

class MainKtTest {

    @Test
    fun json_serialization() {
        Json.encodeToString(JsonObject.serializer(), jsonObject) shouldEqualJson "{\"foo\":\"bar\",\"baz\":null}"
        Json.encodeToString(jsonObject) shouldEqualJson "{\"foo\":\"bar\",\"baz\":null}"
    }

    @Test
    fun compose() = runTest {
        composition {
            Counter()
        }
        root.querySelectorAll("button").asList() should { buttons ->
            buttons.shouldHaveSize(2)
            buttons.map { it.textContent }.shouldContainExactly("-", "+")
        }
    }
}

val jsonObject: JsonObject = buildJsonObject {
    put("foo", "bar")
    put("baz", null)
}
