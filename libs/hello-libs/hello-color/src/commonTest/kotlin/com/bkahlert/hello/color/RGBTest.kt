package com.bkahlert.hello.color

import com.bkahlert.hello.color.Color.HSL
import com.bkahlert.hello.color.Color.RGB
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class RGBTest {
    val color = RGB(85, 255, 0, .5)
    val serialized = "rgba(85, 255, 0, 0.5)".quoted

    inner class Parsing {
        @Test fun shorthandHex() = testAll {
            RGB.parseOrNull("#5f0") shouldBe color.fade(1.0)
        }

        @Test fun shorthandHexWithAlpha() = testAll {
            RGB.parseOrNull("#5f03") shouldBe color.fade(.2)
        }

        @Test fun longHex() = testAll {
            RGB.parseOrNull("#55ff00") shouldBe color.fade(1.0)
        }

        @Test fun longHexWithAlpha() = testAll {
            RGB.parseOrNull("#55ff0033") shouldBe color.fade(.2)
        }

        @Test fun numericalHex() = testAll {
            RGB(0x55ff00) shouldBe color.fade(1.0)
        }

        @Test fun numericalHexWithAlpha() = testAll {
            RGB(0x55ff0033) shouldBe color.fade(.2)
        }

        @Test fun capitalLetterHex() = testAll {
            RGB.parseOrNull("#55FF0033") shouldBe color.fade(.2)
        }

        @Test fun zeroPrefixed() = testAll {
            RGB.parseOrNull("0x55ff00") shouldBe color.fade(1.0)
        }

        @Test fun withoutAlpha() = testAll {
            RGB.parseOrNull("rgb(85, 255, 0)") shouldBe color.fade(1.0)
        }

        @Test fun withAlpha() = testAll {
            RGB.parseOrNull("rgba(85, 255, 0, .5)") shouldBe color
        }

        @Test fun spaceSeparatedWithoutAlpha() = testAll {
            RGB.parseOrNull("rgb(85 255 0)") shouldBe color.fade(1.0)
        }

        @Test fun spaceSeparatedWithAlpha() = testAll {
            RGB.parseOrNull("rgb(85 255 0 / .5)") shouldBe color
        }

        @Test fun rgbaWithoutAlpha() = testAll {
            RGB.parseOrNull("rgba(85, 255, 0)") shouldBe color.fade(1.0)
        }

        @Test fun withoutSpaces() = testAll {
            RGB.parseOrNull("rgb(85,255,0)") shouldBe color.fade(1.0)
        }
    }

    @Test fun to_rgb() = testAll {
        RGB(0x000000).toRGB() shouldBe RGB(0x000000)
        RGB(0x0000ff).toRGB() shouldBe RGB(0x0000ff)
        RGB(0x00ff00).toRGB() shouldBe RGB(0x00ff00)
        RGB(0x00ffff).toRGB() shouldBe RGB(0x00ffff)
        RGB(0xff0000).toRGB() shouldBe RGB(0xff0000)
        RGB(0xff00ff).toRGB() shouldBe RGB(0xff00ff)
        RGB(0xffff00).toRGB() shouldBe RGB(0xffff00)
        RGB(0xffffff).toRGB() shouldBe RGB(0xffffff)
    }

    @Test fun to_hsl() = testAll {
        RGB(0x000000).toHSL() shouldBe HSL(0.deg, 0.0, 0.0)
        RGB(0x0000ff).toHSL() shouldBe HSL(240.deg, 100.0, 50.0)
        RGB(0x00ff00).toHSL() shouldBe HSL(120.deg, 100.0, 50.0)
        RGB(0x00ffff).toHSL() shouldBe HSL(180.deg, 100.0, 50.0)
        RGB(0xff0000).toHSL() shouldBe HSL(0.deg, 100.0, 50.0)
        RGB(0xff00ff).toHSL() shouldBe HSL(300.deg, 100.0, 50.0)
        RGB(0xffff00).toHSL() shouldBe HSL(60.deg, 100.0, 50.0)
        RGB(0xffffff).toHSL() shouldBe HSL(0.deg, 0.0, 100.0)
    }

    @Test fun transformations() = testAll {
        RGB(0.1, 0.2, 0.3, 0.5).adjust(red = 0.05, green = 0.1, blue = 0.2, alpha = 0.1) shouldBe RGB(0.15, 0.3, 0.5, 0.6)
        RGB(0.1, 0.2, 0.3, 0.5).adjust(red = -0.05, green = -0.1, blue = -0.2, alpha = -0.1) shouldBe RGB(0.05, 0.1, 0.1, 0.4)
        RGB(0.1, 0.2, 0.3, 0.5).fadeIn(.1) shouldBe RGB(0.1, 0.2, 0.3, 0.6)
        RGB(0.1, 0.2, 0.3, 0.5).opacify(.1) shouldBe RGB(0.1, 0.2, 0.3, 0.6)
        RGB(0.1, 0.2, 0.3, 0.5).fadeOut(.1) shouldBe RGB(0.1, 0.2, 0.3, 0.4)
        RGB(0.1, 0.2, 0.3, 0.5).transparentize(.1) shouldBe RGB(0.1, 0.2, 0.3, 0.4)
        RGB(0.1, 0.2, 0.3, 0.5).fade(.1) shouldBe RGB(0.1, 0.2, 0.3, 0.1)

        RGB(0.1, 0.2, 0.3, 0.5).scale(red = 0.2, green = 0.2, blue = 0.2, alpha = 0.2) shouldBe RGB(0.28, 0.36, 0.44, 0.6)
        RGB(0.1, 0.2, 0.3, 0.5).scale(red = -0.2, green = -0.2, blue = -0.2, alpha = -0.2) shouldBe RGB(0.08, 0.16, 0.24, 0.4)
        RGB(0.1, 0.2, 0.3, 0.5).scaleRed(0.2) shouldBe RGB(0.28, 0.2, 0.3, 0.5)
        RGB(0.1, 0.2, 0.3, 0.5).scaleRed(-0.2) shouldBe RGB(0.08, 0.2, 0.3, 0.5)
        RGB(0.1, 0.2, 0.3, 0.5).scaleGreen(0.2) shouldBe RGB(0.1, 0.36, 0.3, 0.5)
        RGB(0.1, 0.2, 0.3, 0.5).scaleGreen(-0.2) shouldBe RGB(0.1, 0.16, 0.3, 0.5)
        RGB(0.1, 0.2, 0.3, 0.5).scaleBlue(0.2) shouldBe RGB(0.1, 0.2, 0.44, 0.5)
        RGB(0.1, 0.2, 0.3, 0.5).scaleBlue(-0.2) shouldBe RGB(0.1, 0.2, 0.24, 0.5)
        RGB(0.1, 0.2, 0.3, 0.5).scaleAlpha(.2) shouldBe RGB(0.1, 0.2, 0.3, 0.6)
        RGB(0.1, 0.2, 0.3, 0.5).scaleAlpha(-.2) shouldBe RGB(0.1, 0.2, 0.3, 0.4)
    }


    @Test fun randomize() = testAll {
        repeat(10) {
            RGB(0.1, 0.2, 0.3, 0.5).randomize() should {
                it.red should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.0
                    value shouldBeLessThanOrEqualTo 0.25
                }
                it.green should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.05
                    value shouldBeLessThanOrEqualTo 0.35
                }
                it.blue should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.15
                    value shouldBeLessThanOrEqualTo 0.45
                }
                it.alpha shouldBe 0.5
            }
        }
        repeat(10) {
            RGB(0.1, 0.2, 0.3, 0.5).randomize(red = 0.1, green = 0.2, blue = 0.4, alpha = 0.4) should {
                it.red should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.05
                    value shouldBeLessThanOrEqualTo 0.15
                }
                it.green should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.1
                    value shouldBeLessThanOrEqualTo 0.3
                }
                it.blue should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.1
                    value shouldBeLessThanOrEqualTo 0.5
                }
                it.alpha should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.3
                    value shouldBeLessThanOrEqualTo 0.7
                }
            }
        }
    }

    @Test fun deserialize() = testAll {
        Json.Default.decodeFromString<RGB>(serialized) shouldBe color
    }

    @Test fun serialize() = testAll {
        Json.Default.encodeToString(color) shouldBe serialized
    }
}
