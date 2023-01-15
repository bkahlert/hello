package com.bkahlert.hello.ui.compose

import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.Element

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
