package com.bkahlert.kommons.color

import com.bkahlert.Brand
import com.bkahlert.hello.ui.fmod
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.math.ValueRange.Angle
import com.bkahlert.kommons.math.ValueRange.Bytes
import com.bkahlert.kommons.math.ValueRange.Normalized
import com.bkahlert.kommons.math.ValueRange.Percent
import com.bkahlert.kommons.math.ValueRange.Scaling
import com.bkahlert.kommons.math.map
import com.bkahlert.kommons.math.normalize
import com.bkahlert.kommons.math.round
import com.bkahlert.kommons.math.scale
import com.bkahlert.kommons.math.toHexadecimalString
import com.bkahlert.kommons.ranges.random
import com.bkahlert.kommons.serialization.ColorSerializer
import com.bkahlert.kommons.serialization.HslSerializer
import com.bkahlert.kommons.serialization.RgbSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.compose.web.css.CSSAngleValue
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.deg
import org.jetbrains.compose.web.css.hsl
import org.jetbrains.compose.web.css.hsla
import org.jetbrains.compose.web.css.rgba
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

@Serializable(with = ColorSerializer::class)
abstract class Color(
    /** The alpha of this color in the range `0.0..1.0`. */
    open val alpha: Double,
) : CSSColorValue {

    /** Increase the [alpha] of this color by the specified [amount]. */
    abstract fun fadeIn(amount: Double): Color

    /** Increase the [alpha] of this color by the specified [amount]. */
    abstract fun opacify(amount: Double): Color

    /** Decrease the [alpha] of this color by the specified [amount]. */
    abstract fun fadeOut(amount: Double): Color

    /** Decrease the [alpha] of this color by the specified [amount]. */
    abstract fun transparentize(amount: Double): Color

    /** Set the [alpha] of this color to the specified [value]. */
    abstract fun fade(value: Double): Color

    /**
     * Fluidly scales the [alpha] of this color, whereas
     * a positive [amount] less or equal to `+1.0` move the [alpha] closer to its maximum
     * and a negative [amount] greater or equal to `-1.0` move the [alpha] closer to its minimum.
     */
    abstract fun scaleAlpha(amount: Double): Color

    abstract fun toRGB(): RGB
    abstract fun toHSL(): HSL

    val perceivedBrightness: Double
        get() = toRGB().run { sqrt(red.pow(2) * PERCEIVED_RED_RATIO + green.pow(2) * PERCEIVED_GREEN_RATIO + blue.pow(2) * PERCEIVED_BLUE_RATIO) }

    val textColor: Color
        get() = if (perceivedBrightness > 0.5) Brand.colors.black else Brand.colors.white

    /**
     * A color in the RGB color space with
     * the [red] in the range `0.0..1.0`
     * the [green] in the range `0.0..1.0`,
     * the [blue] in the range `0.0..1.0`,
     * and the [alpha] in the range `0.0..1.0`.
     *
     * @see <a href="https://en.wikipedia.org/wiki/RGB_color_model">RGB color model</a>
     */
    @Serializable(with = RgbSerializer::class)
    data class RGB(
        /** The red primary of this color in the range `0.0..1.0`. */
        val red: Double,
        /** The green primary of this color in the range `0.0..1.0`. */
        val green: Double,
        /** The blue primary of this color in the range `0.0..1.0`. */
        val blue: Double,
        override val alpha: Double = Normalized.endInclusive
    ) : Color(alpha) {

        /**
         * Creates a new RGB color with
         * the specified [red] in the range `0..255`,
         * the [green] in the range `0..255`,
         * the [blue] in the range `0..255`,
         * and the [alpha] in the range `0.0.
         */
        constructor(red: Int, green: Int, blue: Int, alpha: Double = Normalized.endInclusive) : this(
            red.normalize(Bytes), green.normalize(Bytes), blue.normalize(Bytes), alpha,
        )

        /**
         * Increases or decreases one or more properties of this color by fixed amounts,
         * whereas all properties are coerced with their corresponding minimum and maximum value.
         */
        fun adjust(
            red: Double = Normalized.Minimum,
            green: Double = Normalized.Minimum,
            blue: Double = Normalized.Minimum,
            alpha: Double = Normalized.Minimum,
        ): RGB = copy(
            red = if (red == Normalized.Minimum) this.red else (this.red + red).round(0.001).coerceIn(Normalized),
            green = if (green == Normalized.Minimum) this.green else (this.green + green).round(0.001).coerceIn(Normalized),
            blue = if (blue == Normalized.Minimum) this.blue else (this.blue + blue).round(0.001).coerceIn(Normalized),
            alpha = if (alpha == Normalized.Minimum) this.alpha else (this.alpha + alpha).round(0.001).coerceIn(Normalized),
        )

        /** Increase the [alpha] of this color by the specified [amount]. */
        override fun fadeIn(amount: Double): RGB = adjust(alpha = amount)

        /** Increase the [alpha] of this color by the specified [amount]. */
        override fun opacify(amount: Double): RGB = fadeIn(amount)

        /** Decrease the [alpha] of this color by the specified [amount]. */
        override fun fadeOut(amount: Double): RGB = adjust(alpha = -amount)

        /** Decrease the [alpha] of this color by the specified [amount]. */
        override fun transparentize(amount: Double): RGB = fadeOut(amount)

        /** Set the [alpha] of this color to the specified [value]. */
        override fun fade(value: Double): RGB = copy(alpha = value)

        /**
         * Fluidly scales one or more properties of this color, whereas
         * positive amounts less or equal to `+1.0` move the corresponding property closer to its maximum
         * and a negative amounts greater or equal to `-1.0` move the corresponding property closer to its minimum.
         */
        fun scale(
            red: Double = Scaling.None,
            green: Double = Scaling.None,
            blue: Double = Scaling.None,
            alpha: Double = Scaling.None,
        ): RGB = copy(
            red = if (red == Scaling.None) this.red else this.red.scale(red).round(0.001),
            green = if (green == Scaling.None) this.green else this.green.scale(green).round(0.001),
            blue = if (blue == Scaling.None) this.blue else this.blue.scale(blue).round(0.001),
            alpha = if (alpha == Scaling.None) this.alpha else this.alpha.scale(alpha).round(0.001),
        )

        /**
         * Fluidly scales the [red] primary of this color, whereas
         * a positive [amount] less or equal to `+1.0` move the [red] closer to its maximum
         * and a negative [amount] greater or equal to `-1.0` move the [red] closer to its minimum.
         */
        fun scaleRed(amount: Double): RGB = scale(red = amount)

        /**
         * Fluidly scales the [green] primary of this color, whereas
         * a positive [amount] less or equal to `+1.0` move the [green] closer to its maximum
         * and a negative [amount] greater or equal to `-1.0` move the [green] closer to its minimum.
         */
        fun scaleGreen(amount: Double): RGB = scale(green = amount)

        /**
         * Fluidly scales the [blue] primary of this color, whereas
         * a positive [amount] less or equal to `+1.0` move the [HSL.lightness] closer to its maximum
         * and a negative [amount] greater or equal to `-1.0` move the [HSL.lightness] closer to its minimum.
         */
        fun scaleBlue(amount: Double): RGB = scale(blue = amount)

        /**
         * Fluidly scales the [alpha] of this color, whereas
         * a positive [amount] less or equal to `+1.0` move the [alpha] closer to its maximum
         * and a negative [amount] greater or equal to `-1.0` move the [alpha] closer to its minimum.
         */
        override fun scaleAlpha(amount: Double): RGB = scale(alpha = amount)

        /**
         * Randomly adjusts the properties of this color (default: [red], [green] and [blue] properties)
         * with the amounts ranging from `-(amount/2)` to `+(amount/2)`.
         */
        fun randomize(
            red: Double = 0.3,
            green: Double = 0.3,
            blue: Double = 0.3,
            alpha: Double = Normalized.Minimum,
        ): RGB = adjust(
            if (red == Normalized.Minimum) red else Random.nextDouble(red / -2.0, red / 2.0),
            if (green == Normalized.Minimum) green else Random.nextDouble(green / -2.0, green / 2.0),
            if (blue == Normalized.Minimum) blue else Random.nextDouble(blue / -2.0, blue / 2.0),
            if (alpha == Normalized.Minimum) alpha else Random.nextDouble(alpha / -2.0, alpha / 2.0),
        )

        override fun toRGB(): RGB = this

        override fun toHSL(): HSL {
            val min: Double = minOf(red, green, blue)
            val max: Double = maxOf(red, green, blue)
            val delta: Double = max - min
            return HSL(
                hue = when {
                    delta == 0.0 -> Angle.start
                    max == red -> 60.0 * (((green - blue) / delta) fmod 6.0)
                    max == green -> 60.0 * (((blue - red) / delta) + 2.0)
                    max == blue -> 60.0 * (((red - green) / delta) + 4.0)
                    else -> Angle.start
                }.normalize(Angle),
                saturation = when (delta) {
                    0.0 -> Normalized.start
                    else -> delta / (1.0 - abs(max + min - 1.0))
                },
                lightness = ((max + min) / 2.0),
                alpha = alpha,
            )
        }

        override fun toString(): String =
            if (alpha >= Normalized.endInclusive) buildString {
                append("#")
                listOf(red, green, blue).forEach { append(it.map(Bytes).toHexadecimalString()) }
            }
            else rgba(red.map(Bytes), green.map(Bytes), blue.map(Bytes), alpha).toString()

        companion object {
            private const val HEX_PATTERN = "[a-fA-F0-9]"
            private val REGEX = Regex("(?:#|0x)($HEX_PATTERN{3,4}|$HEX_PATTERN{6}|$HEX_PATTERN{8})\\b|rgba?\\(([^)]*)\\)")
            private val SPLIT_REGEX = Regex("\\s*[ ,/]\\s*")
            operator fun invoke(rgb: Int): RGB = if (rgb > 16777215) {
                RGB(red = (rgb shr 24) and 0xFF, green = (rgb shr 16) and 0xFF, blue = (rgb shr 8) and 0xFF, alpha = (rgb and 0xFF) / 255.0)
            } else {
                RGB(red = (rgb shr 16) and 0xFF, green = (rgb shr 8) and 0xFF, blue = rgb and 0xFF)
            }

            operator fun invoke(rgb: String): RGB = parseOrNull(rgb) ?: throw IllegalArgumentException("$rgb is no color")
            fun parseOrNull(rgb: String): RGB? = REGEX.find(rgb)?.groupValues?.run {
                when {
                    getOrNull(1)?.isNotEmpty() == true -> get(1).run {
                        when (length) {
                            3 -> RGB(
                                red = substring(0, 1).repeat(2).toInt(16),
                                green = substring(1, 2).repeat(2).toInt(16),
                                blue = substring(2, 3).repeat(2).toInt(16),
                            )
                            4 -> RGB(
                                red = substring(0, 1).repeat(2).toInt(16),
                                green = substring(1, 2).repeat(2).toInt(16),
                                blue = substring(2, 3).repeat(2).toInt(16),
                                alpha = substring(3, 4).repeat(2).toInt(16).normalize(Bytes),
                            )
                            6 -> RGB(
                                red = substring(0, 2).toInt(16),
                                green = substring(2, 4).toInt(16),
                                blue = substring(4, 6).toInt(16),
                            )
                            8 -> RGB(
                                red = substring(0, 2).toInt(16),
                                green = substring(2, 4).toInt(16),
                                blue = substring(4, 6).toInt(16),
                                alpha = substring(6, 8).toInt(16).normalize(Bytes),
                            )
                            else -> null
                        }
                    }

                    getOrNull(2)?.isNotEmpty() == true -> get(2).split(SPLIT_REGEX).map { it.trim() }.run {
                        when (size) {
                            3 -> RGB(
                                red = get(0).toInt(),
                                green = get(1).toInt(),
                                blue = get(2).toInt(),
                            )
                            4 -> RGB(
                                red = get(0).toInt(),
                                green = get(1).toInt(),
                                blue = get(2).toInt(),
                                alpha = get(3).toDouble(),
                            )
                            else -> null
                        }
                    }

                    else -> null
                }
            }
        }
    }

    /**
     * A color in the HSL color space with
     * the [hue] in the range `0.0..1.0`
     * the [saturation] in the range `0.0..1.0`,
     * the [lightness] in the range `0.0..1.0`,
     * and the [alpha] in the range `0.0..1.0`.
     *
     * @see <a href="https://en.wikipedia.org/wiki/HSL_and_HSV">HSL and HSV</a>
     */
    @Serializable(with = HslSerializer::class)
    data class HSL(
        /** The hue of this color in the range `0.0..1.0`. */
        val hue: Double,
        /** The saturation of this color in the range `0.0..1.0`. */
        val saturation: Double,
        /** The lightness of this color in the range `0.0..1.0`. */
        val lightness: Double,
        override val alpha: Double = Normalized.endInclusive
    ) : Color(alpha) {

        /**
         * Creates a new HSL color with
         * the specified [hue] in the range `0°..360°`,
         * the [saturation] in the range `0.0..100.0`,
         * the [lightness] in the range `0.0..100.0`,
         * and the [alpha] in the range `0.0.
         */
        constructor(
            hue: CSSAngleValue,
            saturation: Double,
            lightness: Double,
            alpha: Double = Normalized.endInclusive
        ) : this(
            hue.value.toDouble().normalize(Angle),
            saturation.normalize(Percent),
            lightness.normalize(Percent),
            alpha,
        )

        /** The hue angle of this color in the range `0°..360°`. */
        val hueAngle: Double get() = hue.map(Angle)

        /** The saturation of this color in the range `0%..100%`. */
        val saturationPercent: Double get() = saturation.map(Percent)

        /** The lightness of this color in the range `0%..100%`. */
        val lightnessPercent: Double get() = lightness.map(Percent)

        /**
         * Increases or decreases one or more properties of this color by fixed amounts,
         * whereas all but [hue] are coerced with their corresponding minimum and maximum value
         * and the resulting [hue] will wrap around its extrema.
         */
        fun adjust(
            hue: Double = Normalized.Minimum,
            saturation: Double = Normalized.Minimum,
            lightness: Double = Normalized.Minimum,
            alpha: Double = Normalized.Minimum,
        ): HSL = copy(
            hue = if (hue == Normalized.Minimum) this.hue else (this.hue + hue).round(0.001).mod(Normalized.Maximum),
            saturation = if (saturation == Normalized.Minimum) this.saturation else (this.saturation + saturation).round(0.001).coerceIn(Normalized),
            lightness = if (lightness == Normalized.Minimum) this.lightness else (this.lightness + lightness).round(0.001).coerceIn(Normalized),
            alpha = if (alpha == Normalized.Minimum) this.alpha else (this.alpha + alpha).round(0.001).coerceIn(Normalized),
        )

        /** Rotates the [hue] angle of this color by the specified [amount] in the range `-1.0..+1.0`. */
        fun spin(amount: Double): HSL = adjust(hue = amount)

        /** Rotates the [hue] angle of this color by the specified [amount] in the range `-360°..+360°`. */
        fun spin(amount: CSSAngleValue): HSL = copy(
            hue = (hueAngle + amount.value.toDouble()).mod(Angle.Maximum).normalize(Angle)
        )

        /** Increase the [saturation] of this color by the specified [amount]. */
        fun saturate(amount: Double): HSL = adjust(saturation = amount)

        /** Decrease the [saturation] of this color by the specified [amount]. */
        fun desaturate(amount: Double): HSL = adjust(saturation = -amount)

        /** Increase the [lightness] of this color by the specified [amount]. */
        fun lighten(amount: Double): HSL = adjust(lightness = amount)

        /** Decrease the [lightness] of this color by the specified [amount]. */
        fun darken(amount: Double): HSL = adjust(lightness = -amount)

        /** Increase the [alpha] of this color by the specified [amount]. */
        override fun fadeIn(amount: Double): HSL = adjust(alpha = amount)

        /** Increase the [alpha] of this color by the specified [amount]. */
        override fun opacify(amount: Double): HSL = fadeIn(amount)

        /** Decrease the [alpha] of this color by the specified [amount]. */
        override fun fadeOut(amount: Double): HSL = adjust(alpha = -amount)

        /** Decrease the [alpha] of this color by the specified [amount]. */
        override fun transparentize(amount: Double): HSL = fadeOut(amount)

        /** Set the [alpha] of this color to the specified [value]. */
        override fun fade(value: Double): HSL = copy(alpha = value)

        /**
         * Fluidly scales one or more properties of this color, whereas
         * positive amounts less or equal to `+1.0` move the corresponding property closer to its maximum
         * and a negative amounts greater or equal to `-1.0` move the corresponding property closer to its minimum.
         */
        fun scale(
            saturation: Double = Scaling.None,
            lightness: Double = Scaling.None,
            alpha: Double = Scaling.None,
        ): HSL = copy(
            saturation = if (saturation == Scaling.None) this.saturation else this.saturation.scale(saturation).round(0.001),
            lightness = if (lightness == Scaling.None) this.lightness else this.lightness.scale(lightness).round(0.001),
            alpha = if (alpha == Scaling.None) this.alpha else this.alpha.scale(alpha).round(0.001),
        )

        /**
         * Fluidly scales the [saturation] of this color, whereas
         * a positive [amount] less or equal to `+1.0` move the [saturation] closer to its maximum
         * and a negative [amount] greater or equal to `-1.0` move the [saturation] closer to its minimum.
         */
        fun scaleSaturation(amount: Double): HSL = scale(saturation = amount)

        /**
         * Fluidly scales the [saturation] of this color, whereas
         * a positive [amount] less or equal to `+1.0` move the [lightness] closer to its maximum
         * and a negative [amount] greater or equal to `-1.0` move the [lightness] closer to its minimum.
         */
        fun scaleLightness(amount: Double): HSL = scale(lightness = amount)

        /**
         * Fluidly scales the [alpha] of this color, whereas
         * a positive [amount] less or equal to `+1.0` move the [alpha] closer to its maximum
         * and a negative [amount] greater or equal to `-1.0` move the [alpha] closer to its minimum.
         */
        override fun scaleAlpha(amount: Double): HSL = scale(alpha = amount)

        /**
         * Randomly adjusts the properties of this color (default: [hue] property)
         * with the amounts ranging from `-(amount/2)` to `+(amount/2)`.
         */
        fun randomize(
            hue: Double = 0.3,
            saturation: Double = Normalized.Minimum,
            lightness: Double = Normalized.Minimum,
            alpha: Double = Normalized.Minimum,
        ): HSL = adjust(
            if (hue == Normalized.Minimum) hue else Random.nextDouble(hue / -2.0, hue / 2.0),
            if (saturation == Normalized.Minimum) saturation else Random.nextDouble(saturation / -2.0, saturation / 2.0),
            if (lightness == Normalized.Minimum) lightness else Random.nextDouble(lightness / -2.0, lightness / 2.0),
            if (alpha == Normalized.Minimum) alpha else Random.nextDouble(alpha / -2.0, alpha / 2.0),
        )

        override fun toRGB(): RGB {
            val q: Double = if (lightness < 0.5) (saturation + 1.0) * lightness else saturation + lightness - saturation * lightness
            val p: Double = lightness * 2.0 - q
            return RGB(
                hueToRgb(p, q, hue + 1.0 / 3.0),
                hueToRgb(p, q, hue),
                hueToRgb(p, q, hue - 1.0 / 3.0),
                alpha,
            )
        }

        private fun hueToRgb(p: Double, q: Double, h: Double): Int {
            @Suppress("NAME_SHADOWING")
            val h = when {
                h < 0 -> h + 1.0
                h > 1 -> h - 1.0
                else -> h
            }
            return when {
                6 * h < 1 -> p + (q - p) * 6 * h
                2 * h < 1 -> q
                3 * h < 2 -> p + (q - p) * 6 * (2.0f / 3.0f - h)
                else -> p
            }.coerceAtLeast(Bytes.start.toDouble()).map(Bytes)
        }

        override fun toHSL(): HSL = this

        override fun toString(): String =
            if (alpha >= Normalized.endInclusive) hsl(hueAngle.round(0.1), saturationPercent.round(0.1), lightnessPercent.round(0.1)).toString()
            else hsla(hueAngle.round(0.1), saturationPercent.round(0.1), lightnessPercent.round(0.1), alpha.round(0.1)).toString()

        companion object {
            private val REGEX = Regex("hsla?\\(([^)]*)\\)")
            private val SPLIT_REGEX = Regex("\\s*[ ,/]\\s*")
            operator fun invoke(hsl: String): HSL = parseOrNull(hsl) ?: throw IllegalArgumentException("$hsl is no color")
            fun parseOrNull(hsl: String): HSL? {
                val components = REGEX.find(hsl)?.groupValues?.get(1)?.split(SPLIT_REGEX)?.takeIf { it.size in 3..4 }?.map { it.trim() } ?: return null
                return HSL(
                    hue = components[0].removeSuffix("deg").toDouble().normalize(Angle),
                    saturation = components[1].removeSuffix("%").toDouble().normalize(Percent),
                    lightness = components[2].removeSuffix("%").toDouble().normalize(Percent),
                    alpha = components.getOrNull(3)?.toDouble() ?: Normalized.endInclusive,
                )
            }
        }
    }

    companion object {
        val Tomato = RGB(0xff6347)
        val TomatoSauce = RGB(0xb21807)

        val PERCEIVED_RED_RATIO: Double = 0.299
        val PERCEIVED_GREEN_RATIO: Double = 0.587
        val PERCEIVED_BLUE_RATIO: Double = 0.114

        operator fun invoke(color: Int): RGB = RGB(color)
        operator fun invoke(color: String): Color = parseOrNull(color) ?: throw IllegalArgumentException("$color is no color")
        fun parseOrNull(color: String): Color? =
            if (color.startsWith("hsl")) HSL.parseOrNull(color) else RGB.parseOrNull(color)

        val Default: HSL get() = Brand.colors.primary.toHSL()

        /**
         * Creates a random color in the specified [hueRange].
         */
        fun random(hueRange: ClosedRange<Double> = Normalized): HSL =
            Default.copy(hue = hueRange.random())

        /**
         * Creates a random [HSL] color with its [HSL.hue] being a random
         * value in the range `[hue]-[variance]/2..[hue]+[variance]/2`
         * and a fixed [HSL.saturation] and [HSL.lightness].
         */
        fun random(hue: Double, variance: Double = 1.0 / 3.0): HSL =
            random(hue - (variance / 2.0)..hue + (variance / 2.0))

        /**
         * Creates a random [HSL] color with its [HSL.hue] being a random
         * value in the range `[hueAngle]-[variance]/2..[hueAngle]+[variance]/2`
         * and a fixed [HSL.saturation] and [HSL.lightness].
         */
        fun random(hueAngle: CSSAngleValue, variance: CSSAngleValue = 60.deg): HSL {
            val hueAngleValue = hueAngle.value.toDouble()
            val varianceValue = variance.value.toDouble()
            return Default.copy(
                hue = ((hueAngleValue - varianceValue / 2.0)..(hueAngleValue + varianceValue / 2.0)).random().mod(Angle.Maximum).normalize(Angle)
            )
        }
    }
}


fun StyleScope.backgroundColor(rgb: Int) =
    backgroundColor(RGB(rgb))
