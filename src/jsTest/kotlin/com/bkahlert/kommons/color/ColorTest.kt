package com.bkahlert.kommons.color

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.color.Color.HSL
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.text.quoted
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.deg
import kotlin.test.Test

class ColorTest {

    inner class Parsing {
        @Test fun hexRgb() {
            Color.parseOrNull("#55ff00") shouldBe RGB(85, 255, 0)
        }

        @Test fun decimalRgb() {
            Color.parseOrNull("rgb(85, 255, 0)") shouldBe RGB(85, 255, 0)
        }

        @Test fun hsl() {
            Color.parseOrNull("hsla(100deg, 100%, 50%, .5)") shouldBe HSL(100.deg, 100, 50, .5)
        }

        @Test fun invalid() {
            Color.parseOrNull("invalid") shouldBe null
        }
    }

    inner class Deserialization {
        @Test fun hexRgb() {
            "#55ff00".quoted.deserialize<Color>() shouldBe RGB(85, 255, 0)
        }

        @Test fun decimalRgb() {
            "rgb(85, 255, 0)".quoted.deserialize<Color>() shouldBe RGB(85, 255, 0)
        }

        @Test fun hsl() {
            "hsl(100, 100%, 50%, .5)".quoted.deserialize<Color>() shouldBe HSL(100.deg, 100, 50, .5)
        }

        @Test fun invalid() {
            shouldThrowAny { "invalid".quoted.deserialize<Color>() }
        }
    }

    inner class Serialization {
        @Test fun rgb() {
            RGB(85, 255, 0).serialize<Color>() shouldBe "#55ff00".quoted
        }

        @Test fun hsl() {
            HSL(100.deg, 100, 50, .5).serialize<Color>() shouldBe "rgba(85, 255, 0, 0.5)".quoted
        }
    }
}
