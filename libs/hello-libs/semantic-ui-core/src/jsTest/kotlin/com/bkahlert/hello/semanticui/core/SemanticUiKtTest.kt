package com.bkahlert.hello.semanticui.core

import com.bkahlert.hello.semanticui.test.JQueryLibrary
import com.bkahlert.hello.semanticui.test.SemanticUiLibrary
import com.bkahlert.hello.semanticui.test.compositionWith
import com.bkahlert.hello.semanticui.test.root
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class SemanticUiKtTest {

    @Test
    fun semantic() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            Semantic("foo", "bar") {
                Text("baz")
            }
        }
        root { it.innerHTML shouldBe "<div class=\"foo bar\">baz</div>" }
    }

    @Test
    fun semantic_ui() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            SemanticUI("foo", "bar") {
                Text("baz")
            }
        }
        root { it.innerHTML shouldBe "<div class=\"ui foo bar\">baz</div>" }
    }
}
