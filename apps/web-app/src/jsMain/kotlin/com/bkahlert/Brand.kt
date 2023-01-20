package com.bkahlert

import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.color.Colors

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

    val colors: Colors = Colors

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
