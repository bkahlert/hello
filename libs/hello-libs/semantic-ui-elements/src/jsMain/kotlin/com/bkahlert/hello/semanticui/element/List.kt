package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.attributes.SemanticAttrsScope
import com.bkahlert.hello.semanticui.core.attributes.Variation
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElementScope
import org.w3c.dom.HTMLDivElement

public interface ListElement : SemanticElement<HTMLDivElement>

/** [Horizontal](https://semantic-ui.com/elements/list.html#horizontal) variation of a [list](https://semantic-ui.com/elements/list.html). */
@Suppress("unused", "UnusedReceiverParameter")
public val <TSemantic : ListElement> SemanticAttrsScope<TSemantic>.horizontal: Variation get() = Variation.Horizontal

/** [Inverted](https://semantic-ui.com/elements/list.html#inverted) variation of a [list](https://semantic-ui.com/elements/list.html). */
@Suppress("unused", "UnusedReceiverParameter")
public val <TSemantic : ListElement> SemanticAttrsScope<TSemantic>.inverted: Variation get() = Variation.Inverted

/** [Selection](https://semantic-ui.com/elements/list.html#selection) variation of a [list](https://semantic-ui.com/elements/list.html). */
@Suppress("unused", "UnusedReceiverParameter")
public val <TSemantic : ListElement> SemanticAttrsScope<TSemantic>.selection: Variation get() = Variation.Selection

/** [Animated](https://semantic-ui.com/elements/list.html#animated) variation of a [list](https://semantic-ui.com/elements/list.html). */
@Suppress("unused", "UnusedReceiverParameter")
public val <TSemantic : ListElement> SemanticAttrsScope<TSemantic>.animated: Variation get() = Variation.Animated

/** [Relaxed](https://semantic-ui.com/elements/list.html#relaxed) variation of a [list](https://semantic-ui.com/elements/list.html). */
@Suppress("unused", "UnusedReceiverParameter")
public val <TSemantic : ListElement> SemanticAttrsScope<TSemantic>.relaxed: Variation get() = Variation.Relaxed

/** [Divided](https://semantic-ui.com/elements/list.html#divided) variation of a [list](https://semantic-ui.com/elements/list.html). */
@Suppress("unused", "UnusedReceiverParameter")
public val <TSemantic : ListElement> SemanticAttrsScope<TSemantic>.divided: Variation get() = Variation.Divided

/** [Celled](https://semantic-ui.com/elements/list.html#celled) variation of a [list](https://semantic-ui.com/elements/list.html). */
@Suppress("unused", "UnusedReceiverParameter")
public val <TSemantic : ListElement> SemanticAttrsScope<TSemantic>.celled: Variation get() = Variation.Celled

/** [Size](https://semantic-ui.com/elements/list.html#size) variation of a [list](https://semantic-ui.com/elements/list.html). */
@Suppress("unused", "UnusedReceiverParameter")
public val <TSemantic : ListElement> SemanticAttrsScope<TSemantic>.size: Variation.Size get() = Variation.Size

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

/** [Vertically aligned](https://semantic-ui.com/elements/list.html#vertically-aligned) variation of [list](https://semantic-ui.com/elements/list.html#content). */
@Suppress("unused", "UnusedReceiverParameter")
public val <TSemantic : ListItemContentElement> SemanticAttrsScope<TSemantic>.verticallyAligned: Variation.VerticallyAligned
    get() = Variation.VerticallyAligned

/** [Floated](https://semantic-ui.com/elements/list.html#floated) variation of [list](https://semantic-ui.com/elements/list.html#content). */
@Suppress("unused", "UnusedReceiverParameter")

public val <TSemantic : ListItemContentElement> SemanticAttrsScope<TSemantic>.floated: Variation.Floated get() = Variation.Floated

/** Creates a [SemanticUI list item content](https://semantic-ui.com/elements/list.html#content). */
@Composable
@Suppress("unused", "UnusedReceiverParameter")
public fun SemanticElementScope<ListItemElement>.Content(
    attrs: SemanticAttrBuilderContext<ListItemContentElement>? = null,
    content: SemanticContentBuilder<ListItemContentElement>? = null,
): Unit = SemanticDivElement({ attrs?.invoke(this); classes("content") }, content)
