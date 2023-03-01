package com.bkahlert.semanticui.core

import kotlinx.browser.window
import org.jetbrains.compose.web.css.CSSMediaQuery
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaFeature
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit.px
import org.jetbrains.compose.web.css.GenericStyleSheetBuilder
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.px
import org.w3c.dom.MediaQueryList

// TODO move to Semantic UI Site module
public object Breakpoints {
    public const val MOBILE_BREAKPOINT: Int = 320
    public const val TABLET_BREAKPOINT: Int = 768
    public const val COMPUTER_BREAKPOINT: Int = 992
    public const val LARGE_MONITOR_BREAKPOINT: Int = 1200
    public const val WIDESCREEN_MONITOR_BREAKPOINT: Int = 1920
}

public enum class Device(
    public val breakPoint: CSSSizeValue<px>,
) {
    Unknown(0.px),
    Mobile(Breakpoints.MOBILE_BREAKPOINT.px),
    Tablet(Breakpoints.TABLET_BREAKPOINT.px),
    LargeMonitor(Breakpoints.LARGE_MONITOR_BREAKPOINT.px),
    ;

    public val smallerDevice: Device? by lazy { values().getOrNull(ordinal - 1) }
    public val largerDevice: Device? by lazy { values().getOrNull(ordinal + 1) }
    public val nextBreakpoint: CSSSizeValue<px>? by lazy { largerDevice?.breakPoint }

    public val minWidthMediaFeature: MediaFeature by lazy { MediaFeature("min-width", breakPoint) }
    public val maxWidthMediaFeature: MediaFeature by lazy { MediaFeature("max-width", nextBreakpoint?.let { it - 1.px } ?: Int.MAX_VALUE.px) }
    public val rangeWidthMediaFeature: CSSMediaQuery by lazy { CSSMediaQuery.And(mutableListOf(minWidthMediaFeature, maxWidthMediaFeature)) }

    public val rangeWithMediaQuery: MediaQueryList by lazy { window.matchMedia(rangeWidthMediaFeature.toString()) }

    public fun isActive(): Boolean = rangeWithMediaQuery.matches

    public companion object {
        public val Active: Device get() = values().first { it.isActive() }
        public val HoverFeature: MediaFeature = MediaFeature("hover", StylePropertyValue("hover"))
        public val NoHoverFeature: MediaFeature = MediaFeature("hover", StylePropertyValue("none"))
    }
}

public val CSSMediaQuery.matches: Boolean get() = window.matchMedia(this.toString()).matches

public fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.mediaMinDeviceWidth(device: Device): MediaFeature = device.minWidthMediaFeature
public fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.mediaMaxDeviceWidth(device: Device): MediaFeature = device.maxWidthMediaFeature
