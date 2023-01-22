package com.bkahlert.hello.ui

import com.bkahlert.semanticui.custom.BreakPoints
import com.bkahlert.semanticui.custom.Length
import org.jetbrains.compose.web.css.px

object ViewportDimension {

    /**
     * Small tablets and large smartphones (landscape view)
     */
    inline val small: Length get() = 576.px

    /**
     * Small tablets (portrait view)
     */
    inline val medium: Length get() = BreakPoints.TabletBreakpoint

    /**
     * Tablets and small desktops
     */
    inline val large: Length get() = BreakPoints.ComputerBreakpoint

    /**
     * Large tablets and desktops
     */
    inline val xLarge: Length get() = BreakPoints.LargeMonitorBreakpoint
}
