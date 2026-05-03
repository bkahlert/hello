package com.bkahlert.semanticui.core

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class SemanticUiKtTest {

    @Test
    fun semantic() = runTest {
        composition {
            S("foo", "bar") {
                Text("baz")
            }
        }
        root should { it.innerHTML shouldBe "<div class=\"foo bar\">baz</div>" }
    }
}
