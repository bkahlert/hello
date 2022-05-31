package com.bkahlert.kommons.color

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.color.Color.HSL
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.tests
import com.bkahlert.kommons.text.quoted
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.deg
import kotlin.test.Test

class RGBTest {
    val color = RGB(85, 255, 0, .5)
    val serialized = "rgba(85, 255, 0, 0.5)".quoted

    inner class Parsing {
        @Test fun shorthandHex() = tests {
            RGB.parseOrNull("#5f0") shouldBe color.fade(1.0)
        }

        @Test fun shorthandHexWithAlpha() = tests {
            RGB.parseOrNull("#5f03") shouldBe color.fade(.2)
        }

        @Test fun longHex() = tests {
            RGB.parseOrNull("#55ff00") shouldBe color.fade(1.0)
        }

        @Test fun longHexWithAlpha() = tests {
            RGB.parseOrNull("#55ff0033") shouldBe color.fade(.2)
        }

        @Test fun numericalHex() = tests {
            RGB(0x55ff00) shouldBe color.fade(1.0)
        }

        @Test fun numericalHexWithAlpha() = tests {
            RGB(0x55ff0033) shouldBe color.fade(.2)
        }

        @Test fun capitalLetterHex() = tests {
            RGB.parseOrNull("#55FF0033") shouldBe color.fade(.2)
        }

        @Test fun zeroPrefixed() = tests {
            RGB.parseOrNull("0x55ff00") shouldBe color.fade(1.0)
        }

        @Test fun withoutAlpha() = tests {
            RGB.parseOrNull("rgb(85, 255, 0)") shouldBe color.fade(1.0)
        }

        @Test fun withAlpha() = tests {
            RGB.parseOrNull("rgba(85, 255, 0, .5)") shouldBe color
        }

        @Test fun spaceSeparatedWithoutAlpha() = tests {
            RGB.parseOrNull("rgb(85 255 0)") shouldBe color.fade(1.0)
        }

        @Test fun spaceSeparatedWithAlpha() = tests {
            RGB.parseOrNull("rgb(85 255 0 / .5)") shouldBe color
        }

        @Test fun rgbaWithoutAlpha() = tests {
            RGB.parseOrNull("rgba(85, 255, 0)") shouldBe color.fade(1.0)
        }

        @Test fun withoutSpaces() = tests {
            RGB.parseOrNull("rgb(85,255,0)") shouldBe color.fade(1.0)
        }
    }

    @Test fun to_rgb() = tests {
        RGB(0x000000).toRGB() shouldBe RGB(0x000000)
        RGB(0x0000ff).toRGB() shouldBe RGB(0x0000ff)
        RGB(0x00ff00).toRGB() shouldBe RGB(0x00ff00)
        RGB(0x00ffff).toRGB() shouldBe RGB(0x00ffff)
        RGB(0xff0000).toRGB() shouldBe RGB(0xff0000)
        RGB(0xff00ff).toRGB() shouldBe RGB(0xff00ff)
        RGB(0xffff00).toRGB() shouldBe RGB(0xffff00)
        RGB(0xffffff).toRGB() shouldBe RGB(0xffffff)
    }

    @Test fun to_hsl() = tests {
        RGB(0x000000).toHSL() shouldBe HSL(0.deg, 0.0, 0.0)
        RGB(0x0000ff).toHSL() shouldBe HSL(240.deg, 100.0, 50.0)
        RGB(0x00ff00).toHSL() shouldBe HSL(120.deg, 100.0, 50.0)
        RGB(0x00ffff).toHSL() shouldBe HSL(180.deg, 100.0, 50.0)
        RGB(0xff0000).toHSL() shouldBe HSL(0.deg, 100.0, 50.0)
        RGB(0xff00ff).toHSL() shouldBe HSL(300.deg, 100.0, 50.0)
        RGB(0xffff00).toHSL() shouldBe HSL(60.deg, 100.0, 50.0)
        RGB(0xffffff).toHSL() shouldBe HSL(0.deg, 0.0, 100.0)
    }

    @Test fun transformations() = tests {
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


    @Test fun randomize() = tests {
        repeat(100) {
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
        repeat(100) {
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

    @Test fun deserialize() = tests {
        serialized.deserialize<RGB>() shouldBe color
    }

    @Test fun serialize() = tests {
        color.serialize() shouldBe serialized
    }
}
