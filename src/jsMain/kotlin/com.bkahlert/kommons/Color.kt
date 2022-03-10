package com.bkahlert.kommons

import com.bkahlert.Brand.colors
import com.bkahlert.hello.fmod
import com.bkahlert.kommons.math.toHexadecimalString
import com.bkahlert.kommons.serialization.ColorSerializer
import com.bkahlert.kommons.serialization.HslSerializer
import com.bkahlert.kommons.serialization.RgbSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.compose.web.css.CSSAngleValue
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.hsl
import org.jetbrains.compose.web.css.hsla
import org.jetbrains.compose.web.css.rgba
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable(with = ColorSerializer::class)
abstract class Color : CSSColorValue {
    abstract fun transparentize(a: Double): Color
    fun transparentize(a: Number): Color = transparentize(a.toDouble())
    abstract fun toRGB(): RGB
    abstract fun toHSL(): HSL

    val perceivedBrightness: Double
        get() = toRGB().let { (r, g, b, _) ->
            sqrt(
                r.pow(2) * PERCEIVED_RED_RATIO +
                    g.pow(2) * PERCEIVED_GREEN_RATIO +
                    b.pow(2) * PERCEIVED_BLUE_RATIO
            )
        }

    val textColor: Color
        get() = if (perceivedBrightness > 0.5) colors.black else colors.white

    @Serializable(with = RgbSerializer::class)
    data class RGB(
        val r: Double,
        val g: Double,
        val b: Double,
        val a: Double = 1.0,
    ) : Color() {
        constructor(r: Number, g: Number, b: Number, a: Number = 1.0) : this(r.toDouble(), g.toDouble(), b.toDouble(), a.toDouble())

        init {
            require(r in 0.0..255.0) { "red must be in range [0..255] but was $r" }
            require(g in 0.0..255.0) { "green must be in range [0..255] but was $g" }
            require(b in 0.0..255.0) { "blue must be in range [0..255] but was $b" }
            require(a in 0.0..1.0) { "alpha must be in range [0..1] but was $a" }
        }

        override fun transparentize(a: Double): Color = RGB(r, g, b, a)

        override fun toRGB(): RGB = this

        override fun toHSL(): HSL {
            val scaledR = r / 255.0
            val scaledG = g / 255.0
            val scaledB = b / 255.0
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
                a = a,
            )
        }

        override fun toString(): String =
            if (a >= 1.0) buildString {
                append("#")
                append(r.toInt().toHexadecimalString())
                append(g.toInt().toHexadecimalString())
                append(b.toInt().toHexadecimalString())
            }
            else rgba(r, g, b, a).toString()

        companion object {
            private const val HEX_PATTERN = "[a-fA-F0-9]"
            private val REGEX = Regex("(?:#|0x)($HEX_PATTERN{3,4}|$HEX_PATTERN{6}|$HEX_PATTERN{8})\\b|rgba?\\(([^)]*)\\)")
            private val SPLIT_REGEX = Regex("\\s*[ ,/]\\s*")
            operator fun invoke(rgb: Int): RGB = invoke("#${rgb.toHexadecimalString()}")
            operator fun invoke(rgb: String): RGB = parseOrNull(rgb) ?: throw IllegalArgumentException("$rgb is no color")
            fun parseOrNull(rgb: String): RGB? = REGEX.find(rgb)?.groupValues?.run {
                when {
                    getOrNull(1)?.isNotEmpty() == true -> get(1).run {
                        when (length) {
                            3 -> RGB(
                                r = substring(0, 1).repeat(2).toInt(16).toDouble(),
                                g = substring(1, 2).repeat(2).toInt(16).toDouble(),
                                b = substring(2, 3).repeat(2).toInt(16).toDouble(),
                            )
                            4 -> RGB(
                                r = substring(0, 1).repeat(2).toInt(16).toDouble(),
                                g = substring(1, 2).repeat(2).toInt(16).toDouble(),
                                b = substring(2, 3).repeat(2).toInt(16).toDouble(),
                                a = substring(3, 4).repeat(2).toInt(16).toDouble() / 255.0,
                            )
                            6 -> RGB(
                                r = substring(0, 2).toInt(16).toDouble(),
                                g = substring(2, 4).toInt(16).toDouble(),
                                b = substring(4, 6).toInt(16).toDouble(),
                            )
                            8 -> RGB(
                                r = substring(0, 2).toInt(16).toDouble(),
                                g = substring(2, 4).toInt(16).toDouble(),
                                b = substring(4, 6).toInt(16).toDouble(),
                                a = substring(6, 8).toInt(16).toDouble() / 255.0,
                            )
                            else -> null
                        }
                    }

                    getOrNull(2)?.isNotEmpty() == true -> get(2).split(SPLIT_REGEX).map { it.trim() }.run {
                        when (size) {
                            3 -> RGB(
                                r = get(0).toDouble(),
                                g = get(1).toDouble(),
                                b = get(2).toDouble(),
                            )
                            4 -> RGB(
                                r = get(0).toDouble(),
                                g = get(1).toDouble(),
                                b = get(2).toDouble(),
                                a = get(3).toDouble(),
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

        override fun transparentize(a: Double): Color = HSL(h, s, l, a)

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
        val PERCEIVED_RED_RATIO: Double = 0.299
        val PERCEIVED_GREEN_RATIO: Double = 0.587
        val PERCEIVED_BLUE_RATIO: Double = 0.114

        operator fun invoke(color: String): Color = parseOrNull(color) ?: throw IllegalArgumentException("$color is no color")
        fun parseOrNull(color: String): Color? =
            if (color.startsWith("hsl")) HSL.parseOrNull(color) else RGB.parseOrNull(color)
    }
}
