package com.bkahlert.kommons.hello

import com.bkahlert.hello.App
import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.semanticui.test.GenericToStringLibrary
import com.bkahlert.semanticui.test.JQueryLibrary
import com.bkahlert.semanticui.test.SemanticUiLibrary
import com.bkahlert.semanticui.test.compositionWith
import io.kotest.assertions.asClue
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
        compositionWith(JQueryLibrary, SemanticUiLibrary, GenericToStringLibrary) {
            App()
        }
        waitForRecompositionComplete()

        root.querySelectorAll(".toolbar").asClue { it.length shouldBe 1 }
        root.querySelectorAll(".toolbar > .links").asClue { it.length shouldBe 1 }
        root.querySelectorAll(".toolbar > .search").asClue { it.length shouldBe 1 }
        root.querySelectorAll(".toolbar > .tasks").asClue { it.length shouldBe 1 }
        root.querySelectorAll(".bookmarks").asClue { it.length shouldBe 1 }
    }
}

val jsonObject: JsonObject = buildJsonObject {
    put("foo", "bar")
    put("baz", null)
}
