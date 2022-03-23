package com.semanticui.compose.element

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
import org.w3c.dom.HTMLDivElement

interface PlaceholderElement : SemanticElement

/**
 * Creates a [SemanticUI placeholder](https://semantic-ui.com/elements/placeholder.html).
 */
@Composable
fun Placeholder(
    attrs: SemanticAttrBuilder<PlaceholderElement, HTMLDivElement>? = null,
    content: SemanticBuilder<PlaceholderElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("placeholder")
    }, content)
}

/**
 * Creates a [SemanticUI placeholder line](https://semantic-ui.com/elements/placeholder.html#line).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<PlaceholderElement, HTMLDivElement>.Line(
    attrs: SemanticAttrBuilder<PlaceholderElement, HTMLDivElement>? = null,
    content: SemanticBuilder<PlaceholderElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("line")
    }, content)
}


/**
 * Creates a [SemanticUI placeholder header](https://semantic-ui.com/elements/placeholder.html#headers).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<PlaceholderElement, HTMLDivElement>.Header(
    attrs: SemanticAttrBuilder<PlaceholderElement, HTMLDivElement>? = null,
    content: SemanticBuilder<PlaceholderElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("header")
    }, content)
}

/**
 * Creates a [SemanticUI placeholder image header](https://semantic-ui.com/elements/placeholder.html#headers).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<PlaceholderElement, HTMLDivElement>.ImageHeader(
    attrs: SemanticAttrBuilder<PlaceholderElement, HTMLDivElement>? = null,
    content: SemanticBuilder<PlaceholderElement, HTMLDivElement>? = null,
) {
    Header({
        attrs?.invoke(this)
        classes("image")
    }, content)
}


/**
 * Creates a [SemanticUI placeholder paragraph](https://semantic-ui.com/elements/placeholder.html#paragraph).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<PlaceholderElement, HTMLDivElement>.Paragraph(
    attrs: SemanticAttrBuilder<PlaceholderElement, HTMLDivElement>? = null,
    content: SemanticBuilder<PlaceholderElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("paragraph")
    }, content)
}


/**
 * Creates a [SemanticUI placeholder image](https://semantic-ui.com/elements/placeholder.html#image).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<PlaceholderElement, HTMLDivElement>.Image(
    attrs: SemanticAttrBuilder<PlaceholderElement, HTMLDivElement>? = null,
    content: SemanticBuilder<PlaceholderElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("image")
    }, content)
}
