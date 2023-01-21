package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Animated
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Celled
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Divided
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Horizontal
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Inverted
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Relaxed
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Selection
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import org.w3c.dom.HTMLDivElement

public interface ListElement : SemanticElement<HTMLDivElement>

/** [Variation.Horizontal](https://semantic-ui.com/elements/list.html#horizontal) */
public fun VariationsScope<ListElement>.horizontal(): VariationsScope<ListElement> = +Horizontal

/** [Variation.Inverted](https://semantic-ui.com/elements/list.html#inverted) */
public fun VariationsScope<ListElement>.inverted(): VariationsScope<ListElement> = +Inverted

/** [Variation.Selection](https://semantic-ui.com/elements/list.html#selection) */
public fun VariationsScope<ListElement>.selection(): VariationsScope<ListElement> = +Selection

/** [Variation.Animated](https://semantic-ui.com/elements/list.html#animated) */
public fun VariationsScope<ListElement>.animated(): VariationsScope<ListElement> = +Animated

/** [Variation.Relaxed](https://semantic-ui.com/elements/list.html#relaxed) */
public fun VariationsScope<ListElement>.relaxed(): VariationsScope<ListElement> = +Relaxed

/** [Variation.Divided](https://semantic-ui.com/elements/list.html#divided) */
public fun VariationsScope<ListElement>.divided(): VariationsScope<ListElement> = +Divided

/** [Variation.Celled](https://semantic-ui.com/elements/list.html#celled) */
public fun VariationsScope<ListElement>.celled(): VariationsScope<ListElement> = +Celled

/** [Variation.Size](https://semantic-ui.com/elements/list.html#size) */
public fun VariationsScope<ListElement>.size(value: Variation.Size): VariationsScope<ListElement> = +value

/** Creates a [SemanticUI list](https://semantic-ui.com/elements/list.html). */
@Composable
public fun List(
    attrs: SemanticAttrBuilderContext<ListElement>? = null,
    content: SemanticContentBuilder<ListElement>? = null,
): Unit = SemanticDivElement({ classes("ui"); attrs?.invoke(this); classes("list"); }, content)

/** Creates a [bulleted](https://semantic-ui.com/elements/list.html#bulleted) [SemanticUI list](https://semantic-ui.com/elements/list.html). */
@Composable
public fun BulletedList(
    attrs: SemanticAttrBuilderContext<ListElement>? = null,
    content: SemanticContentBuilder<ListElement>? = null,
): Unit = List({ attrs?.invoke(this); classes("bulleted") }, content)

/** Creates a [ordered](https://semantic-ui.com/elements/list.html#ordered) [SemanticUI list](https://semantic-ui.com/elements/list.html). */
@Composable
public fun OrderedList(
    attrs: SemanticAttrBuilderContext<ListElement>? = null,
    content: SemanticContentBuilder<ListElement>? = null,
): Unit = List({ attrs?.invoke(this); classes("ordered") }, content)

/** Creates a [link](https://semantic-ui.com/elements/list.html#link) [SemanticUI list](https://semantic-ui.com/elements/list.html). */
@Composable
public fun LinkList(
    attrs: SemanticAttrBuilderContext<ListElement>? = null,
    content: SemanticContentBuilder<ListElement>? = null,
): Unit = List({ attrs?.invoke(this); classes("link") }, content)


public interface ListItemElement : SemanticElement<HTMLDivElement>

/** Creates a [SemanticUI list item](https://semantic-ui.com/elements/list.html#item). */
@Composable
@Suppress("unused", "UnusedReceiverParameter")
public fun SemanticElementScope<ListElement>.Item(
    attrs: SemanticAttrBuilderContext<ListItemElement>? = null,
    content: SemanticContentBuilder<ListItemElement>? = null,
): Unit = SemanticDivElement({ attrs?.invoke(this); classes("item") }, content)

/** Creates a [SemanticUI list item description](https://semantic-ui.com/elements/list.html#description). */
@Composable
@Suppress("unused", "UnusedReceiverParameter")
public fun SemanticElementScope<ListItemElement>.Description(
    attrs: SemanticAttrBuilderContext<ListItemElement>? = null,
    content: SemanticContentBuilder<ListItemElement>? = null,
): Unit = SemanticDivElement({ attrs?.invoke(this); classes("content") }, content)

/** Creates a [SemanticUI list item header](https://semantic-ui.com/elements/list.html#header). */
@Composable
@Suppress("unused", "UnusedReceiverParameter")
public fun SemanticElementScope<ListItemElement>.Header(
    attrs: SemanticAttrBuilderContext<ListItemElement>? = null,
    content: SemanticContentBuilder<ListItemElement>? = null,
): Unit = SemanticDivElement({ attrs?.invoke(this); classes("content") }, content)


public interface ListItemContentElement : SemanticElement<HTMLDivElement>

/** [Variation.VerticallyAligned](https://semantic-ui.com/elements/list.html#vertically-aligned) variation of [list](https://semantic-ui.com/elements/list.html#content). */
public fun VariationsScope<ListItemContentElement>.verticallyAligned(value: Variation.VerticallyAligned): VariationsScope<ListItemContentElement> = +value

/** [Variation.Floated](https://semantic-ui.com/elements/list.html#floated) variation of [list](https://semantic-ui.com/elements/list.html#content). */
public fun VariationsScope<ListItemContentElement>.floated(value: Variation.Floated): VariationsScope<ListItemContentElement> = +value

/** Creates a [SemanticUI list item content](https://semantic-ui.com/elements/list.html#content). */
@Composable
@Suppress("unused", "UnusedReceiverParameter")
public fun SemanticElementScope<ListItemElement>.Content(
    attrs: SemanticAttrBuilderContext<ListItemContentElement>? = null,
    content: SemanticContentBuilder<ListItemContentElement>? = null,
): Unit = SemanticDivElement({ attrs?.invoke(this); classes("content") }, content)
