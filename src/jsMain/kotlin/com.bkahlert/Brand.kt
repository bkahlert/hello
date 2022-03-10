package com.bkahlert

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.Color.HSL
import com.bkahlert.kommons.Color.RGB
import org.jetbrains.compose.web.css.deg

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
        val black: Color = HSL(0.deg, 0, 5)
        val red: Color = HSL(329.deg, 73.2, 43.9)
        val green: Color = HSL(101.deg, 45.2, 49.4)
        val yellow: Color = HSL(49.deg, 100.0, 57.8)
        val blue: Color = HSL(198.deg, 76.7, 51.2)
        val magenta: Color = HSL(294.deg, 73.2, 43.9)
        val cyan: Color = HSL(186.deg, 98.6, 28.2)
        val white: Color = HSL(0.deg, 0.0, 86.3)

        val primary: Color = blue
        val secondary: Color = magenta

        val border: Color = RGB("#5f6368").toHSL()
        val input: Color = HSL(0.deg, 100, 100)
    }

    val rainbow: List<Color> = listOf(
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
