package com.semanticui.compose.element

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementType
import org.w3c.dom.HTMLDivElement

interface SegmentElement : SemanticElement
enum class SegmentElementType(override vararg val classNames: String) : SemanticElementType<SegmentElement> {
    Placeholder("placeholder"),
    Raised("raised"),
    Stacked("stacked"),
    Piled("piled"),
    Vertical("vertical"),
}

/**
 * Creates a [SemanticUI segment](https://semantic-ui.com/elements/segment.html)
 * with the specified [type].
 */
@Composable
fun Segment(
    type: SegmentElementType?,
    attrs: SemanticAttrBuilder<SegmentElement, HTMLDivElement>? = null,
    content: SemanticBuilder<SegmentElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        type?.invoke(this)
        classes("segment")
    }, content)
}

/**
 * Creates a [SemanticUI segment](https://semantic-ui.com/elements/segment.html).
 */
@Composable
fun Segment(
    attrs: SemanticAttrBuilder<SegmentElement, HTMLDivElement>? = null,
    content: SemanticBuilder<SegmentElement, HTMLDivElement>? = null,
) {
    Segment(null, attrs, content)
}

/**
 * Creates a [SemanticUI segments](https://semantic-ui.com/elements/segment.html#segments)
 * with the specified [type].
 */
@Composable
fun Segments(
    type: SegmentElementType?,
    attrs: SemanticAttrBuilder<SegmentElement, HTMLDivElement>? = null,
    content: SemanticBuilder<SegmentElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        type?.invoke(this)
        classes("segments")
    }, content)
}

/**
 * Creates a [SemanticUI segments](https://semantic-ui.com/elements/segment.html#segments).
 */
@Composable
fun Segments(
    attrs: SemanticAttrBuilder<SegmentElement, HTMLDivElement>? = null,
    content: SemanticBuilder<SegmentElement, HTMLDivElement>? = null,
) {
    Segments(null, attrs, content)
}
