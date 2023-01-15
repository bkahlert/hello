package com.bkahlert.hello.dom

import com.bkahlert.hello.ui.compose.data
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class AttributesKtTest {

    @Test
    fun data() = runTest {
        composition {
            Div({ data("foo", "bar") })
        }
        root.innerHTML shouldBe "<div data-foo=\"bar\"></div>"
    }
}
