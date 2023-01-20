package com.bkahlert.kommons.color

import com.bkahlert.kommons.color.Color.Companion
import com.bkahlert.kommons.color.Color.HSL
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.test.testAll
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ColorTest {

    inner class Parsing {
        @Test fun hexRgb() = testAll {
            Color.parseOrNull("#55ff00") shouldBe RGB(85, 255, 0)
        }

        @Test fun decimalRgb() = testAll {
            Color.parseOrNull("rgb(85, 255, 0)") shouldBe RGB(85, 255, 0)
        }

        @Test fun hsl() = testAll {
            Color.parseOrNull("hsla(100deg, 100%, 50%, .5)") shouldBe HSL(100.deg, 100.0, 50.0, .5)
        }

        @Test fun invalid() = testAll {
            Color.parseOrNull("invalid") shouldBe null
        }
    }

    inner class Deserialization {
        @Test fun hexRgb() = testAll {
            Json.Default.decodeFromString<Color>("#55ff00".quoted) shouldBe RGB(85, 255, 0)
        }

        @Test fun decimalRgb() = testAll {
            Json.Default.decodeFromString<Color>("rgb(85, 255, 0)".quoted) shouldBe RGB(85, 255, 0)
        }

        @Test fun hsl() = testAll {
            Json.Default.decodeFromString<Color>("hsl(100, 100%, 50%, .5)".quoted) shouldBe HSL(100.deg, 100.0, 50.0, .5)
        }

        @Test fun invalid() = testAll {
            shouldThrowAny { Json.Default.decodeFromString<Color>("invalid".quoted) }
        }
    }

    inner class Serialization {
        @Test fun rgb() = testAll {
            Json.Default.encodeToString(RGB(85, 255, 0)) shouldBe "#55ff00".quoted
        }

        @Test fun hsl() = testAll {
            Json.Default.encodeToString(HSL(100.deg, 100.0, 50.0, .5)) shouldBe "hsla(100deg, 100%, 50%, 0.5)".quoted
        }
    }

    @Test fun transformations() = testAll {
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).fadeIn(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.6)
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).opacify(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.6)
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).fadeOut(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.4)
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).transparentize(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.4)
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).fade(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.1)

        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).scaleAlpha(.2) shouldBe HSL(90.deg, 80.0, 50.0, 0.6)
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).scaleAlpha(-.2) shouldBe HSL(90.deg, 80.0, 50.0, 0.4)
    }

    @Test fun random_color() = testAll {
        repeat(10) {
            Color.random() should {
                it.hueAngle should { angle ->
                    angle shouldBeGreaterThanOrEqualTo 0.0
                    angle shouldBeLessThanOrEqualTo 360.0
                }
                it.saturation shouldBe Color.Default.saturation
                it.lightness shouldBe Color.Default.lightness
                it.alpha shouldBe Color.Default.alpha
            }
        }
    }

    @Test fun random_color_with_base_hue() = testAll {
        repeat(10) {
            Color.random(Color.Default.hue) should {
                it.hue should { value ->
                    value shouldBeGreaterThanOrEqualTo Color.Default.hue - 0.17
                    value shouldBeLessThanOrEqualTo Color.Default.hue + 0.17
                }
                it.saturation shouldBe Color.Default.saturation
                it.lightness shouldBe Color.Default.lightness
                it.alpha shouldBe Color.Default.alpha
            }
        }
    }

    @Test fun random_color_with_base_hue_and_variance() = testAll {
        repeat(10) {
            Color.random(0.5, 0.1) should {
                it.hue should { hue ->
                    hue shouldBeGreaterThanOrEqualTo 0.45
                    hue shouldBeLessThanOrEqualTo 0.55
                }
                it.saturation shouldBe Color.Default.saturation
                it.lightness shouldBe Color.Default.lightness
                it.alpha shouldBe Color.Default.alpha
            }
        }
    }

    @Test fun random_color_within_range() = testAll {
        repeat(10) {
            Color.random(0.1..0.2) should {
                it.hue should { value ->
                    value shouldBeGreaterThanOrEqualTo 0.1
                    value shouldBeLessThanOrEqualTo 0.2
                }
                it.saturation shouldBe Color.Default.saturation
                it.lightness shouldBe Color.Default.lightness
                it.alpha shouldBe Color.Default.alpha
            }
        }
    }

    @Test fun random_color_with_base_hue_degree() = testAll {
        repeat(10) {
            Companion.random(180.deg) should {
                it.hueAngle should { angle ->
                    angle shouldBeGreaterThanOrEqualTo 150.0
                    angle shouldBeLessThanOrEqualTo 210.0
                }
                it.saturation shouldBe Color.Default.saturation
                it.lightness shouldBe Color.Default.lightness
                it.alpha shouldBe Color.Default.alpha
            }
        }
    }

    @Test fun random_color_with_base_hue_degree_and_variance() = testAll {
        repeat(10) {
            Companion.random(180.deg, 10.deg) should {
                it.hueAngle should { angle ->
                    angle shouldBeGreaterThanOrEqualTo 170.0
                    angle shouldBeLessThanOrEqualTo 190.0
                }
                it.saturation shouldBe Color.Default.saturation
                it.lightness shouldBe Color.Default.lightness
                it.alpha shouldBe Color.Default.alpha
            }
        }
    }
}
