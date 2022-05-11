package com.bkahlert.kommons.color

import com.bkahlert.Brand.colors
import com.bkahlert.hello.ui.fmod
import com.bkahlert.kommons.color.Color.HSL
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.math.toHexadecimalString
import com.bkahlert.kommons.serialization.ColorSerializer
import com.bkahlert.kommons.serialization.HslSerializer
import com.bkahlert.kommons.serialization.RgbSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.compose.web.css.CSSAngleValue
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.hsl
import org.jetbrains.compose.web.css.hsla
import org.jetbrains.compose.web.css.rgba
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

interface Normalizable {
    val normalized: Double
}

/** Values with allowed values 0..255 */
value class Primary(val value: Double) : Normalizable {
    init {
        require(value in Range) { "$value must be in range [$Range]" }
    }

    override val normalized: Double get() = value / Range.endInclusive
    override fun toString(): String = "$value"

    companion object {
        fun of(value: Number) = Primary(value.toDouble())
        val Min: Double = 0.0
        val Max: Double = 255.0
        val Range: ClosedRange<Double> = Min..Max
    }
}

/** Values with allowed values 0°..360° */
value class Degree(val value: Double) : Normalizable {
    init {
        require(value in Range) { "$value must be in range [$Range]" }
    }

    override val normalized: Double get() = value / Range.endInclusive
    override fun toString(): String = "$value°"

    companion object {
        fun of(value: Number) = Primary(value.toDouble())
        val Min: Double = 0.0
        val Max: Double = 360.0
        val Range: ClosedRange<Double> = Min..Max
    }
}

/** Values with allowed values 0%..100% */
value class Percent(val value: Double) : Normalizable {
    init {
        require(value in Range) { "$value must be in range [$Range]" }
    }

    override val normalized: Double get() = value / Range.endInclusive
    override fun toString(): String = "$value%"

    companion object {
        fun of(value: Number) = Primary(value.toDouble())
        val Min: Double = 0.0
        val Max: Double = 100.0
        val Range: ClosedRange<Double> = Min..Max
    }
}

/** Values with allowed values 0..1 */
value class Normalized(val value: Double) : Normalizable {
    init {
        require(value in Range) { "$value must be in range [$Range]" }
    }

    override val normalized: Double get() = value
    override fun toString(): String = "$value%"

    companion object {
        val Min: Double = 0.0
        val Max: Double = 1.0
        val Range: ClosedRange<Double> = Min..Max
    }
}

@Serializable(with = ColorSerializer::class)
abstract class Color : CSSColorValue {

    /** Returns a copy of this color with the specified [a]. */
    abstract fun withAlpha(a: Double): Color

    /** Returns a copy of this color with the specified [a]. */
    fun withAlpha(a: Number): Color = withAlpha(a.toDouble())
    abstract fun toRGB(): RGB
    abstract fun toHSL(): HSL

    val perceivedBrightness: Double
        get() = toRGB().let { (r, g, b, _) ->
            sqrt(
                (r / 255.0).pow(2) * PERCEIVED_RED_RATIO +
                    (g / 255.0).pow(2) * PERCEIVED_GREEN_RATIO +
                    (b / 255.0).pow(2) * PERCEIVED_BLUE_RATIO
            )
        }

    val textColor: Color
        get() = if (perceivedBrightness > 0.5) colors.black else colors.white

    @Serializable(with = RgbSerializer::class)
    data class RGB(
        val red: Double,
        val green: Double,
        val blue: Double,
        val alpha: Double = Normalized.Max,
    ) : Color() {
        constructor(r: Number, g: Number, b: Number, a: Number = 1.0) : this(r.toDouble(), g.toDouble(), b.toDouble(), a.toDouble())

        init {
            require(red in 0.0..255.0) { "red must be in range [0..255] but was $red" }
            require(green in 0.0..255.0) { "green must be in range [0..255] but was $green" }
            require(blue in 0.0..255.0) { "blue must be in range [0..255] but was $blue" }
            require(alpha in 0.0..1.0) { "alpha must be in range [0..1] but was $alpha" }
        }

        override fun withAlpha(a: Double): Color = RGB(red, green, blue, a)

        override fun toRGB(): RGB = this

        override fun toHSL(): HSL {
            val scaledR = red / 255.0
            val scaledG = green / 255.0
            val scaledB = blue / 255.0
            val min: Double = minOf(scaledR, scaledG, scaledB)
            val max: Double = maxOf(scaledR, scaledG, scaledB)
            val delta: Double = max - min
            return HSL(
                h = when {
                    delta == 0.0 -> 0.0
                    max == scaledR -> 60.0 * (((scaledG - scaledB) / delta) fmod 6.0)
                    max == scaledG -> 60.0 * (((scaledB - scaledR) / delta) + 2.0)
                    max == scaledB -> 60.0 * (((scaledR - scaledG) / delta) + 4.0)
                    else -> 0.0
                },
                s = when (delta) {
                    0.0 -> 0.0
                    else -> ((delta / (1 - abs(max + min - 1))) * 100.0)
                },
                l = (((max + min) / 2.0) * 100.0),
                a = alpha,
            )
        }

        override fun toString(): String =
            if (alpha >= 1.0) buildString {
                append("#")
                append(red.toInt().toHexadecimalString())
                append(green.toInt().toHexadecimalString())
                append(blue.toInt().toHexadecimalString())
            }
            else rgba(
                red.roundToInt(),
                green.roundToInt(),
                blue.roundToInt(),
                alpha,
            ).toString()

        companion object {
            private const val HEX_PATTERN = "[a-fA-F0-9]"
            private val REGEX = Regex("(?:#|0x)($HEX_PATTERN{3,4}|$HEX_PATTERN{6}|$HEX_PATTERN{8})\\b|rgba?\\(([^)]*)\\)")
            private val SPLIT_REGEX = Regex("\\s*[ ,/]\\s*")
            operator fun invoke(rgb: Int): RGB = if (rgb > 16777215) {
                RGB(r = (rgb shr 24) and 0xFF, g = (rgb shr 16) and 0xFF, b = (rgb shr 8) and 0xFF, a = (rgb and 0xFF) / 255.0)
            } else {
                RGB(r = (rgb shr 16) and 0xFF, g = (rgb shr 8) and 0xFF, b = rgb and 0xFF)
            }

            operator fun invoke(rgb: String): RGB = parseOrNull(rgb) ?: throw IllegalArgumentException("$rgb is no color")
            fun parseOrNull(rgb: String): RGB? = REGEX.find(rgb)?.groupValues?.run {
                when {
                    getOrNull(1)?.isNotEmpty() == true -> get(1).run {
                        when (length) {
                            3 -> RGB(
                                red = substring(0, 1).repeat(2).toInt(16).toDouble(),
                                green = substring(1, 2).repeat(2).toInt(16).toDouble(),
                                blue = substring(2, 3).repeat(2).toInt(16).toDouble(),
                            )
                            4 -> RGB(
                                red = substring(0, 1).repeat(2).toInt(16).toDouble(),
                                green = substring(1, 2).repeat(2).toInt(16).toDouble(),
                                blue = substring(2, 3).repeat(2).toInt(16).toDouble(),
                                alpha = substring(3, 4).repeat(2).toInt(16).toDouble() / 255.0,
                            )
                            6 -> RGB(
                                red = substring(0, 2).toInt(16).toDouble(),
                                green = substring(2, 4).toInt(16).toDouble(),
                                blue = substring(4, 6).toInt(16).toDouble(),
                            )
                            8 -> RGB(
                                red = substring(0, 2).toInt(16).toDouble(),
                                green = substring(2, 4).toInt(16).toDouble(),
                                blue = substring(4, 6).toInt(16).toDouble(),
                                alpha = substring(6, 8).toInt(16).toDouble() / 255.0,
                            )
                            else -> null
                        }
                    }

                    getOrNull(2)?.isNotEmpty() == true -> get(2).split(SPLIT_REGEX).map { it.trim() }.run {
                        when (size) {
                            3 -> RGB(
                                red = get(0).toDouble(),
                                green = get(1).toDouble(),
                                blue = get(2).toDouble(),
                            )
                            4 -> RGB(
                                red = get(0).toDouble(),
                                green = get(1).toDouble(),
                                blue = get(2).toDouble(),
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

    @Serializable(with = HslSerializer::class)
    data class HSL(
        val h: Double,
        val s: Double,
        val l: Double,
        val a: Double = 1.0,
    ) : Color() {
        constructor(h: CSSAngleValue, s: Number, l: Number, a: Number = 1.0) : this(h.value.toDouble(), s.toDouble(), l.toDouble(), a.toDouble())

        init {
            require(h in 0.0..360.0) { "hue must be in range [0..360] but was $h" }
            require(s in 0.0..100.0) { "saturation must be in range [0..100] but was $s" }
            require(l in 0.0..100.0) { "lightness must be in range [0..100] but was $l" }
            require(a in 0.0..1.0) { "alpha must be in range [0..1] but was $a" }
        }

        override fun withAlpha(a: Double): Color = HSL(h, s, l, a)

        override fun toRGB(): RGB {
            val h: Double = h / 360.0
            val s: Double = s / 100.0
            val l: Double = l / 100.0
            val q: Double = if (l < 0.5) l * (1 + s) else l + s - s * l
            val p = 2 * l - q
            val r: Double = maxOf(0.0, hueToRgb(p, q, h + 1.0 / 3.0) * 255.0)
            val g: Double = maxOf(0.0, hueToRgb(p, q, h) * 255.0)
            val b: Double = maxOf(0.0, hueToRgb(p, q, h - 1.0 / 3.0) * 255.0)
            return RGB(r, g, b, a)
        }

        private fun hueToRgb(p: Double, q: Double, h: Double): Double {
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
            }
        }

        override fun toHSL(): HSL = this

        override fun toString(): String =
            if (a >= 1.0) hsl(h, s, l).toString()
            else hsla(h, s, l, a).toString()

        companion object {
            private val REGEX = Regex("hsla?\\(([^)]*)\\)")
            private val SPLIT_REGEX = Regex("\\s*[ ,/]\\s*")
            operator fun invoke(hsl: String): HSL = parseOrNull(hsl) ?: throw IllegalArgumentException("$hsl is no color")
            fun parseOrNull(hsl: String): HSL? {
                val components = REGEX.find(hsl)?.groupValues?.get(1)?.split(SPLIT_REGEX)?.takeIf { it.size in 3..4 }?.map { it.trim() } ?: return null
                return HSL(
                    h = components[0].removeSuffix("deg").toDouble(),
                    s = components[1].removeSuffix("%").toDouble(),
                    l = components[2].removeSuffix("%").toDouble(),
                    a = components.getOrNull(3)?.toDouble() ?: 1.0,
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
    }
}

fun Color.RGB.coerceAtMost(
    red: Double? = null,
    green: Double? = null,
    blue: Double? = null,
    alpha: Double? = null,
) = copy(
    red = red?.let { this.red.coerceAtMost(it) } ?: this.red,
    green = green?.let { this.green.coerceAtMost(it) } ?: this.green,
    blue = blue?.let { this.blue.coerceAtMost(it) } ?: this.blue,
    alpha = alpha?.let { this.alpha.coerceAtMost(it) } ?: this.alpha,
)

fun HSL.coerceAtMost(
    hue: Double? = null,
    saturation: Double? = null,
    lightness: Double? = null,
    alpha: Double? = null,
) = copy(
    h = hue?.let { h.coerceAtMost(it) } ?: h,
    s = saturation?.let { s.coerceAtMost(it) } ?: s,
    l = lightness?.let { l.coerceAtMost(it) } ?: l,
    a = alpha?.let { a.coerceAtMost(it) } ?: a,
)

fun StyleScope.backgroundColor(rgb: Int) =
    backgroundColor(RGB(rgb))
