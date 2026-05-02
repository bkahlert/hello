package com.bkahlert.semanticui.custom

import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.overflow
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
