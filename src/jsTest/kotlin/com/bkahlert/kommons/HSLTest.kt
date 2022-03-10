package com.bkahlert.kommons

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.Color.HSL
import com.bkahlert.kommons.text.quoted
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.deg
import kotlin.test.Test

class HSLTest {
    val color = HSL(100.deg, 100, 50, .5)
    val serialized = "hsla(100deg, 100%, 50%, 0.5)".quoted

    inner class Parsing {
        @Test fun withoutAlpha() {
            HSL.parseOrNull("hsl(100, 100%, 50%)") shouldBe color.transparentize(1)
        }

        @Test fun withAlpha() {
            HSL.parseOrNull("hsl(100, 100%, 50%, .5)") shouldBe color
        }

        @Test fun spaceSeparatedWithoutAlpha() {
            HSL.parseOrNull("hsl(100 100% 50%)") shouldBe color.transparentize(1)
        }

        @Test fun spaceSeparatedWithAlpha() {
            HSL.parseOrNull("hsl(100 100% 50% / .5)") shouldBe color
        }

        @Test fun hslaWithoutAlpha() {
            HSL.parseOrNull("hsla(100, 100%, 50%)") shouldBe color.transparentize(1)
        }

        @Test fun withoutSpaces() {
            HSL.parseOrNull("hsla(100,100%,50%)") shouldBe color.transparentize(1)
        }
    }

    @Test fun deserialize() {
        serialized.deserialize<HSL>() shouldBe color
    }

    @Test fun serialize() {
        color.serialize() shouldBe serialized
    }
}
