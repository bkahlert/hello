package com.bkahlert.semanticui.custom

import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitLength
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.whiteSpace
import org.w3c.dom.Element

/**
 * Truncates overflowing text using the specified [marker].
 *
 * For truncation to happen, the text must be forced
 * to overflow.
 * This can be done by [forceOverflow].
 */
public fun StyleScope.textOverflow(
    marker: String = "ellipsis",
    forceOverflow: Boolean = true,
) {
    if (forceOverflow) {
        whiteSpace("nowrap")
        overflow("hidden")
    }
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
