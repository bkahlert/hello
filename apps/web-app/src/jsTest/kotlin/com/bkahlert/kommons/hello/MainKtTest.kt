package com.bkahlert.kommons.hello

import com.bkahlert.hello.Grid
import com.bkahlert.kommons.json.LenientJson
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
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
        composition {
            Grid()
        }
        root.innerHTML shouldBe "<div class=\"AppStylesheet-helloGridContainer\"></div>"
    }
}

val jsonObject: JsonObject = buildJsonObject {
    put("foo", "bar")
    put("baz", null)
}
