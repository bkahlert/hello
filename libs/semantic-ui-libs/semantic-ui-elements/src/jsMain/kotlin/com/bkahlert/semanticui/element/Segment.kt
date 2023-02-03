package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.Modifier.State.Disabled
import com.bkahlert.semanticui.core.attributes.Modifier.State.Loading
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached
import com.bkahlert.semanticui.core.attributes.StatesScope
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

public interface SegmentElement : SemanticElement<HTMLDivElement>

/** [State.Disabled](https://semantic-ui.com/elements/segment.html#disabled) */
public fun StatesScope<SegmentElement>.disabled(): StatesScope<SegmentElement> = +Disabled

/** [State.Loading](https://semantic-ui.com/elements/segment.html#loading) */
public fun StatesScope<SegmentElement>.loading(): StatesScope<SegmentElement> = +Loading

/** [Variation.Inverted](https://semantic-ui.com/elements/segment.html#inverted) */
public fun VariationsScope<SegmentElement>.inverted(): VariationsScope<SegmentElement> = +Variation.Inverted

/** [Variation.Attached.VerticallyAttached](https://semantic-ui.com/elements/segment.html#attached) */
public fun VariationsScope<SegmentElement>.attached(): VariationsScope<SegmentElement> = +Attached

/** [Variation.Attached.VerticallyAttached](https://semantic-ui.com/elements/segment.html#attached) */
public fun VariationsScope<SegmentElement>.attached(value: Attached.VerticallyAttached): VariationsScope<SegmentElement> = +value

/** [Variation.Padded](https://semantic-ui.com/elements/segment.html#padded) */
public fun VariationsScope<SegmentElement>.padded(): VariationsScope<SegmentElement> = +Variation.Padded

/** [Variation.Padded](https://semantic-ui.com/elements/segment.html#padded) */
public fun VariationsScope<SegmentElement>.veryPadded(): VariationsScope<SegmentElement> = +Variation.VeryPadded

/** [Variation.Compact](https://semantic-ui.com/elements/segment.html#compact) */
public fun VariationsScope<SegmentElement>.compact(): VariationsScope<SegmentElement> = +Variation.Compact

/** [Variation.Colored](https://semantic-ui.com/elements/segment.html#colored) */
public fun VariationsScope<SegmentElement>.colored(value: Variation.Colored): VariationsScope<SegmentElement> = +value

/** [Variation.Emphasis](https://semantic-ui.com/elements/segment.html#emphasis) */
public fun VariationsScope<SegmentElement>.emphasis(value: Variation.Emphasis): VariationsScope<SegmentElement> = +value

/** [Variation.Circular](https://semantic-ui.com/elements/segment.html#circular) */
public fun VariationsScope<SegmentElement>.circular(): VariationsScope<SegmentElement> = +Variation.Circular

/** [Variation.Clearing](https://semantic-ui.com/elements/segment.html#clearing) */
public fun VariationsScope<SegmentElement>.clearing(): VariationsScope<SegmentElement> = +Variation.Clearing

/** [Variation.Floated](https://semantic-ui.com/elements/segment.html#floated) */
public fun VariationsScope<SegmentElement>.floated(value: Variation.Floated): VariationsScope<SegmentElement> = +value

/** [Variation.TextAlignment](https://semantic-ui.com/elements/segment.html#text-alignment) */
public fun VariationsScope<SegmentElement>.aligned(value: Variation.TextAlignment): VariationsScope<SegmentElement> = +value

/** [Variation.Basic](https://semantic-ui.com/elements/segment.html#basic) */
public fun VariationsScope<SegmentElement>.basic(): VariationsScope<SegmentElement> = +Variation.Basic

/**
 * Creates a [SemanticUI segment](https://semantic-ui.com/elements/segment.html).
 */
@Composable
public fun Segment(
    attrs: SemanticAttrBuilderContext<SegmentElement>? = null,
    content: SemanticContentBuilder<SegmentElement>? = null,
): Unit = SemanticDivElement({
    classes("ui")
    attrs?.invoke(this)
    classes("segment")
}, content)

/**
 * Creates a [SemanticUI placeholder segment](https://semantic-ui.com/elements/segment.html#placeholder).
 */
@Composable
public fun PlaceholderSegment(
    attrs: SemanticAttrBuilderContext<SegmentElement>? = null,
    content: SemanticContentBuilder<SegmentElement>? = null,
): Unit = Segment({
    classes("placeholder")
    attrs?.invoke(this)
}, content)

/**
 * Creates a [SemanticUI raised segment](https://semantic-ui.com/elements/segment.html#raised).
 */
@Composable
public fun RaisedSegment(
    attrs: SemanticAttrBuilderContext<SegmentElement>? = null,
    content: SemanticContentBuilder<SegmentElement>? = null,
): Unit = Segment({
    classes("raised")
    attrs?.invoke(this)
}, content)

/**
 * Creates a [SemanticUI stacked segment](https://semantic-ui.com/elements/segment.html#stacked).
 */
@Composable
public fun StackedSegment(
    attrs: SemanticAttrBuilderContext<SegmentElement>? = null,
    content: SemanticContentBuilder<SegmentElement>? = null,
): Unit = Segment({
    classes("stacked")
    attrs?.invoke(this)
}, content)

/**
 * Creates a [SemanticUI piled segment](https://semantic-ui.com/elements/segment.html#piled).
 */
@Composable
public fun PiledSegment(
    attrs: SemanticAttrBuilderContext<SegmentElement>? = null,
    content: SemanticContentBuilder<SegmentElement>? = null,
): Unit = Segment({
    classes("piled")
    attrs?.invoke(this)
}, content)

/**
 * Creates a [SemanticUI vertical segment](https://semantic-ui.com/elements/segment.html#vertical).
 */
@Composable
public fun VerticalSegment(
    attrs: SemanticAttrBuilderContext<SegmentElement>? = null,
    content: SemanticContentBuilder<SegmentElement>? = null,
): Unit = Segment({
    classes("vertical")
    attrs?.invoke(this)
}, content)


public interface SegmentGroupElement : SemanticElement<HTMLDivElement>

/** [Variation.Horizontal](https://semantic-ui.com/elements/segment.html#horizontal-segments) */
public fun VariationsScope<SegmentGroupElement>.horizontal(): VariationsScope<SegmentGroupElement> = +Variation.Horizontal

/**
 * Creates a [SemanticUI segment group](https://semantic-ui.com/elements/segment.html#segments).
 */
@Composable
public fun Segments(
    attrs: SemanticAttrBuilderContext<SegmentGroupElement>? = null,
    content: SemanticContentBuilder<SegmentGroupElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("segments")
    }, content)
}

/**
 * Creates a [SemanticUI raised segment group](https://semantic-ui.com/elements/segment.html#raised-segments).
 */
@Composable
public fun RaisedSegments(
    attrs: SemanticAttrBuilderContext<SegmentGroupElement>? = null,
    content: SemanticContentBuilder<SegmentGroupElement>? = null,
) {
    Segments({
        classes("raised")
        attrs?.invoke(this)
    }, content)
}

/**
 * Creates a [SemanticUI stacked segment group](https://semantic-ui.com/elements/segment.html#stacked-segments).
 */
@Composable
public fun StackedSegments(
    attrs: SemanticAttrBuilderContext<SegmentGroupElement>? = null,
    content: SemanticContentBuilder<SegmentGroupElement>? = null,
) {
    Segments({
        classes("stacked")
        attrs?.invoke(this)
    }, content)
}

/**
 * Creates a [SemanticUI piled segment group](https://semantic-ui.com/elements/segment.html#piled-segments).
 */
@Composable
public fun PiledSegments(
    attrs: SemanticAttrBuilderContext<SegmentGroupElement>? = null,
    content: SemanticContentBuilder<SegmentGroupElement>? = null,
) {
    Segments({
        classes("piled")
        attrs?.invoke(this)
    }, content)
}
