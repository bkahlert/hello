package com.bkahlert.hello

import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitLength
import org.jetbrains.compose.web.css.px

object ViewportDimension {

    /**
     * Small tablets and large smartphones (landscape view)
     */
    inline val small: CSSSizeValue<out CSSUnitLength> get() = 576.px

    /**
     * Small tablets (portrait view)
     */
    inline val medium: CSSSizeValue<out CSSUnitLength> get() = 768.px

    /**
     * Tablets and small desktops
     */
    inline val large: CSSSizeValue<out CSSUnitLength> get() = 992.px

    /**
     * Large tablets and desktops
     */
    inline val xLarge: CSSSizeValue<out CSSUnitLength> get() = 1200.px
}
