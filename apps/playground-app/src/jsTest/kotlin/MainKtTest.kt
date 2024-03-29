import com.bkahlert.kommons.json.LenientJson
import io.kotest.assertions.json.shouldEqualJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test

class MainKtTest {

    @Test
    fun json_serialization() {
        LenientJson.encodeToString(JsonObject.serializer(), jsonObject) shouldEqualJson "{\"foo\":\"bar\",\"baz\":null}"
        LenientJson.encodeToString(jsonObject) shouldEqualJson "{\"foo\":\"bar\",\"baz\":null}"
    }

//    @Test
//    fun fritz2() = runTest {
//        composition {
//            Foo()
//        }
//        root.innerHTML shouldStartWith "<p>Bar</p>"
//    }
}

val jsonObject: JsonObject = buildJsonObject {
    put("foo", "bar")
    put("baz", null)
}
