package com.bkahlert.kommons

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.Color.RGB
import com.bkahlert.kommons.text.quoted
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RGBTest {
    val color = RGB(85, 255, 0, .5)
    val serialized = "rgba(85, 255, 0, 0.5)".quoted

    inner class Parsing {
        @Test fun shorthandHex() {
            RGB.parseOrNull("#5f0") shouldBe color.transparentize(1)
        }

        @Test fun shorthandHexWithAlpha() {
            RGB.parseOrNull("#5f03") shouldBe color.transparentize(.2)
        }

        @Test fun longHex() {
            RGB.parseOrNull("#55ff00") shouldBe color.transparentize(1)
        }

        @Test fun longHexWithAlpha() {
            RGB.parseOrNull("#55ff0033") shouldBe color.transparentize(.2)
        }

        @Test fun numericalHex() {
            RGB(0x55ff00) shouldBe color.transparentize(1)
        }

        @Test fun numericalHexWithAlpha() {
            RGB(0x55ff0033) shouldBe color.transparentize(.2)
        }

        @Test fun capitalLetterHex() {
            RGB.parseOrNull("#55FF0033") shouldBe color.transparentize(.2)
        }

        @Test fun zeroPrefixed() {
            RGB.parseOrNull("0x55ff00") shouldBe color.transparentize(1)
        }

        @Test fun withoutAlpha() {
            RGB.parseOrNull("rgb(85, 255, 0)") shouldBe color.transparentize(1)
        }

        @Test fun withAlpha() {
            RGB.parseOrNull("rgba(85, 255, 0, .5)") shouldBe color
        }

        @Test fun spaceSeparatedWithoutAlpha() {
            RGB.parseOrNull("rgb(85 255 0)") shouldBe color.transparentize(1)
        }

        @Test fun spaceSeparatedWithAlpha() {
            RGB.parseOrNull("rgb(85 255 0 / .5)") shouldBe color
        }

        @Test fun rgbaWithoutAlpha() {
            RGB.parseOrNull("rgba(85, 255, 0)") shouldBe color.transparentize(1)
        }

        @Test fun withoutSpaces() {
            RGB.parseOrNull("rgb(85,255,0)") shouldBe color.transparentize(1)
        }
    }

    @Test fun deserialize() {
        serialized.deserialize<RGB>() shouldBe color
    }

    @Test fun serialize() {
        color.serialize() shouldBe serialized
    }
}
