package com.bkahlert.kommons.color

import com.bkahlert.hello.serialize
import com.bkahlert.kommons.color.Color.HSL
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.tests
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.deg
import kotlin.test.Test

class HSLTest {
    val color = HSL(100.deg, 100.0, 50.0, .5)
    val serialized = "hsla(100deg, 100%, 50%, 0.5)".quoted

    inner class Parsing {
        @Test fun withoutAlpha() = tests {
            HSL.parseOrNull("hsl(100, 100%, 50%)") shouldBe color.fade(1.0)
        }

        @Test fun withAlpha() = tests {
            HSL.parseOrNull("hsl(100, 100%, 50%, .5)") shouldBe color
        }

        @Test fun spaceSeparatedWithoutAlpha() = tests {
            HSL.parseOrNull("hsl(100 100% 50%)") shouldBe color.fade(1.0)
        }

        @Test fun spaceSeparatedWithAlpha() = tests {
            HSL.parseOrNull("hsl(100 100% 50% / .5)") shouldBe color
        }

        @Test fun hslaWithoutAlpha() = tests {
            HSL.parseOrNull("hsla(100, 100%, 50%)") shouldBe color.fade(1.0)
        }

        @Test fun withoutSpaces() = tests {
            HSL.parseOrNull("hsla(100,100%,50%)") shouldBe color.fade(1.0)
        }
    }

    @Test fun to_rgb() = tests {
        HSL(0.deg, 0.0, 0.0).toRGB() shouldBe RGB(0x000000)
        HSL(240.deg, 100.0, 50.0).toRGB() shouldBe RGB(0x0000ff)
        HSL(120.deg, 100.0, 50.0).toRGB() shouldBe RGB(0x00ff00)
        HSL(180.deg, 100.0, 50.0).toRGB() shouldBe RGB(0x00ffff)
        HSL(0.deg, 100.0, 50.0).toRGB() shouldBe RGB(0xff0000)
        HSL(300.deg, 100.0, 50.0).toRGB() shouldBe RGB(0xff00ff)
        HSL(60.deg, 100.0, 50.0).toRGB() shouldBe RGB(0xffff00)
        HSL(0.deg, 0.0, 100.0).toRGB() shouldBe RGB(0xffffff)
    }

    @Test fun to_hsl() = tests {
        HSL(0.deg, 0.0, 0.0).toHSL() shouldBe HSL(0.deg, 0.0, 0.0)
        HSL(240.deg, 100.0, 50.0).toHSL() shouldBe HSL(240.deg, 100.0, 50.0)
        HSL(120.deg, 100.0, 50.0).toHSL() shouldBe HSL(120.deg, 100.0, 50.0)
        HSL(180.deg, 100.0, 50.0).toHSL() shouldBe HSL(180.deg, 100.0, 50.0)
        HSL(0.deg, 100.0, 50.0).toHSL() shouldBe HSL(0.deg, 100.0, 50.0)
        HSL(300.deg, 100.0, 50.0).toHSL() shouldBe HSL(300.deg, 100.0, 50.0)
        HSL(60.deg, 100.0, 50.0).toHSL() shouldBe HSL(60.deg, 100.0, 50.0)
        HSL(0.deg, 0.0, 100.0).toHSL() shouldBe HSL(0.deg, 0.0, 100.0)
    }

    @Test fun transformations() = tests {
        HSL(90.deg, 80.0, 50.0, 0.5).adjust(hue = 120 / 360.0, saturation = 0.2, lightness = 0.2, alpha = 0.1) shouldBe HSL(209.88.deg, 100.0, 70.0, 0.6)
        HSL(90.deg, 80.0, 50.0, 0.5).adjust(hue = -120 / 360.0, saturation = -0.2, lightness = -0.2, alpha = -0.1) shouldBe HSL(330.12.deg, 60.0, 30.0, 0.4)
        HSL(90.deg, 80.0, 50.0, 0.5).spin(120.deg) shouldBe HSL(210.deg, 80.0, 50.0, 0.5)
        HSL(90.deg, 80.0, 50.0, 0.5).spin((-120).deg) shouldBe HSL(330.deg, 80.0, 50.0, 0.5)
        HSL(90.deg, 80.0, 50.0, 0.5).saturate(.2) shouldBe HSL(90.deg, 100.0, 50.0, 0.5)
        HSL(90.deg, 80.0, 50.0, 0.5).desaturate(.2) shouldBe HSL(90.deg, 60.0, 50.0, 0.5)
        HSL(90.deg, 80.0, 50.0, 0.5).lighten(.2) shouldBe HSL(90.deg, 80.0, 70.0, 0.5)
        HSL(90.deg, 80.0, 50.0, 0.5).darken(.2) shouldBe HSL(90.deg, 80.0, 30.0, 0.5)
        HSL(90.deg, 80.0, 50.0, 0.5).fadeIn(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.6)
        HSL(90.deg, 80.0, 50.0, 0.5).opacify(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.6)
        HSL(90.deg, 80.0, 50.0, 0.5).fadeOut(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.4)
        HSL(90.deg, 80.0, 50.0, 0.5).transparentize(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.4)
        HSL(90.deg, 80.0, 50.0, 0.5).fade(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.1)

        HSL(90.deg, 80.0, 50.0, 0.5).scale(saturation = 0.2, lightness = 0.2, alpha = 0.2) shouldBe HSL(90.deg, 84.0, 60.0, 0.6)
        HSL(90.deg, 80.0, 50.0, 0.5).scale(saturation = -0.2, lightness = -0.2, alpha = -0.2) shouldBe HSL(90.deg, 64.0, 40.0, 0.4)
        HSL(90.deg, 80.0, 50.0, 0.5).scaleSaturation(.2) shouldBe HSL(90.deg, 84.0, 50.0, 0.5)
        HSL(90.deg, 80.0, 50.0, 0.5).scaleSaturation(-.2) shouldBe HSL(90.deg, 64.0, 50.0, 0.5)
        HSL(90.deg, 80.0, 50.0, 0.5).scaleLightness(.2) shouldBe HSL(90.deg, 80.0, 60.0, 0.5)
        HSL(90.deg, 80.0, 50.0, 0.5).scaleLightness(-.2) shouldBe HSL(90.deg, 80.0, 40.0, 0.5)
        HSL(90.deg, 80.0, 50.0, 0.5).scaleAlpha(.2) shouldBe HSL(90.deg, 80.0, 50.0, 0.6)
        HSL(90.deg, 80.0, 50.0, 0.5).scaleAlpha(-.2) shouldBe HSL(90.deg, 80.0, 50.0, 0.4)
    }

    @Test fun randomize() = tests {
        repeat(100) {
            HSL(90.deg, 80.0, 50.0, 0.5).randomize() should {
                it.hueAngle should { angle ->
                    angle shouldBeGreaterThanOrEqualTo 30.0
                    angle shouldBeLessThanOrEqualTo 150.0
                }
                it.saturation shouldBe 0.8
                it.lightness shouldBe 0.5
                it.alpha shouldBe 0.5
            }
        }
        repeat(100) {
            HSL(90.deg, 80.0, 50.0, 0.5).randomize(hue = 0.1, saturation = 0.4, lightness = 0.4, alpha = 0.4) should {
                it.hueAngle should { value ->
                    value shouldBeGreaterThanOrEqualTo 70.0
                    value shouldBeLessThanOrEqualTo 110.0
                }
                it.saturation should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.6
                    value shouldBeLessThanOrEqualTo 1.0
                }
                it.lightness should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.3
                    value shouldBeLessThanOrEqualTo 0.7
                }
                it.alpha should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.3
                    value shouldBeLessThanOrEqualTo 0.7
                }
            }
        }
    }

    @Test fun serialize() = tests {
        color.serialize() shouldBe serialized
    }
}
