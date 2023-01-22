package com.bkahlert.semanticui.custom

import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitLength
import org.jetbrains.compose.web.css.StylePropertyEnum
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.value
import org.jetbrains.compose.web.css.whiteSpace
import org.w3c.dom.Element

/**
 * Truncates overflowing text using the specified [marker].
 *
 * Sets [whiteSpace] to `nowrap` (recommended), and
 * [overflow] to `hidden` (required) for the text to actually be truncated.
 */
public fun StyleScope.textOverflow(
    marker: String = "ellipsis",
    whiteSpace: String? = "nowrap",
    overflow: String? = "hidden",
) {
    if (whiteSpace != null) whiteSpace(whiteSpace)
    if (overflow != null) overflow(overflow)
    property("text-overflow", if (marker == "ellipsis" || marker == "…") "ellipsis" else "\"$marker\"")
}

/** CSS length, e.g. `42.em` */
public typealias Length = CSSSizeValue<out CSSUnitLength>

/**
 * [data] adds arbitrary `data` attribute to the Element.
 * If it called twice for the same attribute name, attribute value will be resolved to the last call.
 *
 * The following calls are equivalent:
 * ```kotlin
 * attr("data-custom", "value")
 * data("custom", "value")
 * ```
 */
public fun AttrsScope<Element>.data(dataAttr: String, value: String): AttrsScope<Element> = attr("data-$dataAttr", value)


public fun StyleScope.mixBlendMode(mode: MixBlendMode) {
    property("mix-blend-mode", mode.value)
}

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/mix-blend-mode#formal_syntax">mix-blend-mode</a>
 */
public interface MixBlendMode : StylePropertyEnum {
    public companion object {
        public inline val Normal: MixBlendMode get() = MixBlendMode("normal")
        public inline val Multiply: MixBlendMode get() = MixBlendMode("multiply")
        public inline val Screen: MixBlendMode get() = MixBlendMode("screen")
        public inline val Overlay: MixBlendMode get() = MixBlendMode("overlay")
        public inline val Darken: MixBlendMode get() = MixBlendMode("darken")
        public inline val Lighten: MixBlendMode get() = MixBlendMode("lighten")
        public inline val ColorDodge: MixBlendMode get() = MixBlendMode("color-dodge")
        public inline val ColorBurn: MixBlendMode get() = MixBlendMode("color-burn")
        public inline val HardLight: MixBlendMode get() = MixBlendMode("hard-light")
        public inline val SoftLight: MixBlendMode get() = MixBlendMode("soft-light")
        public inline val Difference: MixBlendMode get() = MixBlendMode("difference")
        public inline val Exclusion: MixBlendMode get() = MixBlendMode("exclusion")
        public inline val Hue: MixBlendMode get() = MixBlendMode("hue")
        public inline val Saturation: MixBlendMode get() = MixBlendMode("saturation")
        public inline val Color: MixBlendMode get() = MixBlendMode("color")
        public inline val Luminosity: MixBlendMode get() = MixBlendMode("luminosity")
    }
}

@Suppress("NOTHING_TO_INLINE")
public inline fun MixBlendMode(value: String): MixBlendMode = value.unsafeCast<MixBlendMode>()


public fun StyleScope.zIndex(value: Int) {
    property("z-index", value)
}
