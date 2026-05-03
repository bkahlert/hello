package com.bkahlert.semanticui.custom

import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CssColorValueKtTest {

    @Test
    fun css_color_value() = testAll {
        Color.RGB(10, 20, 30).cssColorValue.toString() shouldBe "#0a141e"
        Color.HSL(0.1, 0.2, 0.3).cssColorValue.toString() shouldBe "hsl(36deg, 20%, 30%)"
    }
}
