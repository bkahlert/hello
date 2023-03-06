package com.bkahlert.semanticui.module

import com.bkahlert.semanticui.module.CheckboxElementType.Slider
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class CheckboxKtTest {

    @Test
    fun checkbox() = runTest {
        composition {
            Checkbox(Slider, { style { color(Color("yellow")) } }) {
                Text("foo")
            }
        }
        root.innerHTML shouldBe "<div class=\"ui slider checkbox\" style=\"color: yellow;\">foo</div>"
    }
}
