package com.bkahlert.kommons.hello

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
//    fun compose() = runTest {
//        composition {
//            Toolbar()
//        }
//        waitForRecompositionComplete()
//
//        root.querySelectorAll(".toolbar").asClue { it.length shouldBe 1 }
//        root.querySelectorAll(".toolbar > .links").asClue { it.length shouldBe 1 }
//        root.querySelectorAll(".toolbar > .search").asClue { it.length shouldBe 1 }
//        root.querySelectorAll(".toolbar > .tasks").asClue { it.length shouldBe 1 }
//        root.querySelectorAll(".bookmarks").asClue { it.length shouldBe 1 }
//    }
}

val jsonObject: JsonObject = buildJsonObject {
    put("foo", "bar")
    put("baz", null)
}
