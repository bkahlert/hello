package com.bkahlert.semanticui.collection

import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class MenuKtTest {

    @Test
    fun menu() = runTest {
        composition {
            Menu({ style { color(Color("yellow")) } }) {
                Text("foo")
            }
        }
        root.innerHTML shouldBe "<div class=\"ui menu\" style=\"color: yellow;\">foo</div>"
    }
}
