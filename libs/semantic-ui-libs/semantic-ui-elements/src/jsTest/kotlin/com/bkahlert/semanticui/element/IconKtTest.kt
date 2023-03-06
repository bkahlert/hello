package com.bkahlert.semanticui.element

import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class IconKtTest {

    @Test
    fun icon() = runTest {
        composition {
            Icon("help") { style { color(Color("yellow")) } }
        }
        root.innerHTML shouldBe "<i class=\"help icon\" style=\"color: yellow;\"></i>"
    }
}
