package com.bkahlert.kommons.color

import com.bkahlert.kommons.color.Color.HSL
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.json.deserialize
import com.bkahlert.kommons.json.serialize
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.tests
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.css.deg
import kotlin.test.Test

class ColorTest {

    inner class Parsing {
        @Test fun hexRgb() = tests {
            Color.parseOrNull("#55ff00") shouldBe RGB(85, 255, 0)
        }

        @Test fun decimalRgb() = tests {
            Color.parseOrNull("rgb(85, 255, 0)") shouldBe RGB(85, 255, 0)
        }

        @Test fun hsl() = tests {
            Color.parseOrNull("hsla(100deg, 100%, 50%, .5)") shouldBe HSL(100.deg, 100.0, 50.0, .5)
        }

        @Test fun invalid() = tests {
            Color.parseOrNull("invalid") shouldBe null
        }
    }

    inner class Deserialization {
        @Test fun hexRgb() = tests {
            "#55ff00".quoted.deserialize<Color>() shouldBe RGB(85, 255, 0)
        }

        @Test fun decimalRgb() = tests {
            "rgb(85, 255, 0)".quoted.deserialize<Color>() shouldBe RGB(85, 255, 0)
        }

        @Test fun hsl() = tests {
            "hsl(100, 100%, 50%, .5)".quoted.deserialize<Color>() shouldBe HSL(100.deg, 100.0, 50.0, .5)
        }

        @Test fun invalid() = tests {
            shouldThrowAny { "invalid".quoted.deserialize<Color>() }
        }
    }

    inner class Serialization {
        @Test fun rgb() = tests {
            RGB(85, 255, 0).serialize<Color>() shouldBe "#55ff00".quoted
        }

        @Test fun hsl() = tests {
            HSL(100.deg, 100.0, 50.0, .5).serialize<Color>() shouldBe "rgba(85, 255, 0, 0.5)".quoted
        }
    }

    @Test fun transformations() = tests {
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).fadeIn(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.6)
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).opacify(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.6)
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).fadeOut(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.4)
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).transparentize(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.4)
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).fade(.1) shouldBe HSL(90.deg, 80.0, 50.0, 0.1)

        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).scaleAlpha(.2) shouldBe HSL(90.deg, 80.0, 50.0, 0.6)
        (HSL(90.deg, 80.0, 50.0, 0.5) as Color).scaleAlpha(-.2) shouldBe HSL(90.deg, 80.0, 50.0, 0.4)
    }

    @Test fun random_color() = tests {
        repeat(100) {
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

    @Test fun random_color_with_base_hue() = tests {
        repeat(100) {
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

    @Test fun random_color_with_base_hue_and_variance() = tests {
        repeat(100) {
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

    @Test fun random_color_within_range() = tests {
        repeat(100) {
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

    @Test fun random_color_with_base_hue_degree() = tests {
        repeat(100) {
            Color.random(180.deg) should {
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

    @Test fun random_color_with_base_hue_degree_and_variance() = tests {
        repeat(100) {
            Color.random(180.deg, 10.deg) should {
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
