package com.bkahlert.hello.color

import com.bkahlert.hello.color.Color.HSL
import com.bkahlert.hello.color.Color.RGB

public object Colors {
    public val black: Color = HSL(0.deg, 0.0, 5.0)
    public val red: Color = HSL(329.deg, 73.2, 43.9)
    public val green: Color = HSL(101.deg, 45.2, 49.4)
    public val yellow: Color = HSL(49.deg, 100.0, 57.8)
    public val blue: Color = HSL(198.deg, 76.7, 51.2)
    public val magenta: Color = HSL(294.deg, 73.2, 43.9)
    public val cyan: Color = HSL(186.deg, 98.6, 28.2)
    public val white: Color = HSL(0.deg, 0.0, 86.3)

    public val primary: Color get() = blue
    public val secondary: Color get() = magenta

    public val border: Color = RGB("#5f6368").toHSL()
}
