package com.bkahlert

import com.bkahlert.hello.fmod
import org.jetbrains.compose.web.css.CSSAngleValue
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.deg
import org.jetbrains.compose.web.css.hsl
import org.jetbrains.compose.web.css.hsla
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.css.rgba
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object Brand {

    val fonts = listOf(
        "system-ui",
        "-apple-system",
        "Segoe UI",
        "Roboto",
        "Helvetica",
        "Arial",
        "sans-serif",
        "Apple Color Emoji",
        "Segoe UI Emoji",
    )

    val fontFamily = fonts.joinToString(",") { "'$it'" }

    object colors {
        val black: HSL = HSL(0.deg, 0, 5)
        val red: HSL = HSL(329.deg, 73.2, 43.9)
        val green: HSL = HSL(101.deg, 45.2, 49.4)
        val yellow: HSL = HSL(49.deg, 100.0, 57.8)
        val blue: HSL = HSL(198.deg, 76.7, 51.2)
        val magenta: HSL = HSL(294.deg, 73.2, 43.9)
        val cyan: HSL = HSL(186.deg, 98.6, 28.2)
        val white: HSL = HSL(0.deg, 0.0, 86.3)

        val primary: HSL = blue
        val secondary: HSL = magenta
    }

    val rainbow = listOf(
        colors.blue,
        RGB(100, 139, 224),
        RGB(163, 94, 187),
        colors.red,
        RGB(247, 79, 87),
        RGB(255, 146, 52),
        colors.yellow,
        RGB(203, 207, 40),
        RGB(153, 196, 53),
        colors.green,
        RGB(4, 169, 113),
        RGB(0, 151, 139),
        colors.cyan,
        RGB(0, 144, 170),
        RGB(0, 158, 198),
        colors.blue,
    )
}

interface Color : CSSColorValue {
    fun transparentize(a: Number): Color
    fun toRGB(): RGB
    fun toHSL(): HSL

    val perceivedBrightness: Double
        get() = toRGB().let { (r, g, b, _) ->
            sqrt(
                r.pow(2) * PERCEIVED_RED_RATIO +
                    g.pow(2) * PERCEIVED_GREEN_RATIO +
                    b.pow(2) * PERCEIVED_BLUE_RATIO
            )
        }

    val textColor: Color
        get() = if (perceivedBrightness > 0.5) Brand.colors.black else Brand.colors.white

    companion object {
        val PERCEIVED_RED_RATIO: Double = 0.299
        val PERCEIVED_GREEN_RATIO: Double = 0.587
        val PERCEIVED_BLUE_RATIO: Double = 0.114
    }
}

class RGB(val r: Number, val g: Number, val b: Number, val a: Number = 1.0) : Color {

    constructor(rgb: String) : this(
        r = rgb.substring(1, 3).toInt(16),
        g = rgb.substring(3, 5).toInt(16),
        b = rgb.substring(5, 7).toInt(16),
        a = if (rgb.length == 9) rgb.substring(7, 9).toInt(16) else 1.0,
    )

    init {
        require(r.toInt() in 0..255) { "red must be in range [0..255] but was $r" }
        require(g.toInt() in 0..255) { "green must be in range [0..255] but was $g" }
        require(b.toInt() in 0..255) { "blue must be in range [0..255] but was $b" }
        require(a.toInt() in 0..1) { "alpha must be in range [0..1] but was $a" }
    }

    operator fun component1(): Double = r.toDouble() / 255.0
    operator fun component2(): Double = g.toDouble() / 255.0
    operator fun component3(): Double = b.toDouble() / 255.0
    operator fun component4(): Double = a.toDouble()

    override fun transparentize(a: Number): Color = RGB(r, g, b, a)

    override fun toRGB(): RGB = this

    override fun toHSL(): HSL {
        val (r, g, b, a) = this
        val min: Double = minOf(r, g, b)
        val max: Double = maxOf(r, g, b)
        val delta: Double = max - min
        return HSL(
            h = when {
                delta == 0.0 -> 0.deg
                max == r -> (60.0 * (((g - b) / delta) fmod 6.0)).deg
                max == g -> (60.0 * (((b - r) / delta) + 2.0)).deg
                max == b -> (60.0 * (((r - g) / delta) + 4.0)).deg
                else -> 0.deg
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
        if (a.toDouble() >= 1.0) rgb(r, g, b).toString()
        else rgba(r, g, b, a).toString()
}

class HSL(val h: CSSAngleValue, val s: Number, val l: Number, val a: Number = 1.0) : Color {

    private val deg: Double = h.toString().removeSuffix("deg").toDouble()

    init {
        require(deg.toInt() in 0..360) { "hue must be in range [0..360] but was $deg" }
        require(s.toInt() in 0..100) { "saturation must be in range [0..100] but was $s" }
        require(l.toInt() in 0..100) { "lightness must be in range [0..100] but was $l" }
        require(a.toInt() in 0..1) { "alpha must be in range [0..1] but was $a" }
    }

    override fun transparentize(a: Number): Color = HSL(h, s, l, a)

    override fun toRGB(): RGB {
        val h: Double = h.toString().removeSuffix("deg").toDouble() / 360.0
        val s: Double = s.toDouble() / 100.0
        val l: Double = l.toDouble() / 100.0
        val q: Double = if (l < 0.5) l * (1 + s) else l + s - s * l
        val p = 2 * l - q
        val r: Double = maxOf(0.0, hueToRgb(p, q, h + 1.0 / 3.0) * 255.0)
        val g: Double = maxOf(0.0, hueToRgb(p, q, h) * 255.0)
        val b: Double = maxOf(0.0, hueToRgb(p, q, h - 1.0 / 3.0) * 255.0)
        return RGB(r, g, b, a.toDouble())
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
        if (a.toDouble() >= 1.0) hsl(h, s, l).toString()
        else hsla(h, s, l, a).toString()
}
