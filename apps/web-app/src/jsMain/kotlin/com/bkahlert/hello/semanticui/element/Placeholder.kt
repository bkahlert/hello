package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
import com.bkahlert.hello.semanticui.dom.SemanticElementScope
import org.w3c.dom.HTMLDivElement

interface PlaceholderElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI placeholder](https://semantic-ui.com/elements/placeholder.html).
 */
@Composable
fun Placeholder(
    attrs: SemanticAttrBuilderContext<PlaceholderElement>? = null,
    content: SemanticContentBuilder<PlaceholderElement>? = null,
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
fun SemanticElementScope<PlaceholderElement>.Line(
    attrs: SemanticAttrBuilderContext<PlaceholderElement>? = null,
    content: SemanticContentBuilder<PlaceholderElement>? = null,
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
fun SemanticElementScope<PlaceholderElement>.Header(
    attrs: SemanticAttrBuilderContext<PlaceholderElement>? = null,
    content: SemanticContentBuilder<PlaceholderElement>? = null,
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
fun SemanticElementScope<PlaceholderElement>.ImageHeader(
    attrs: SemanticAttrBuilderContext<PlaceholderElement>? = null,
    content: SemanticContentBuilder<PlaceholderElement>? = null,
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
fun SemanticElementScope<PlaceholderElement>.Paragraph(
    attrs: SemanticAttrBuilderContext<PlaceholderElement>? = null,
    content: SemanticContentBuilder<PlaceholderElement>? = null,
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
fun SemanticElementScope<PlaceholderElement>.Image(
    attrs: SemanticAttrBuilderContext<PlaceholderElement>? = null,
    content: SemanticContentBuilder<PlaceholderElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("image")
    }, content)
}
