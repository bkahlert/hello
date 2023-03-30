package com.bkahlert.semanticui.custom

import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class StylesKtTest {

    @Test
    fun data() = runTest {
        composition {
            Div({ data("foo", "bar") })
        }
        root.innerHTML shouldBe "<div data-foo=\"bar\"></div>"
    }
}
