package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.attributes.SemanticAttrsScope
import com.bkahlert.hello.semanticui.core.attributes.State
import com.bkahlert.hello.semanticui.core.attributes.Variation
import com.bkahlert.hello.semanticui.core.attributes.Variation.Size
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import org.jetbrains.compose.web.dom.A
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLImageElement

public interface ImageElement<out TElement : Element> : SemanticElement<TElement>
public interface ImageDivElement : ImageElement<HTMLDivElement>
public interface ImageImageElement : ImageElement<HTMLImageElement>
public interface ImageAnchorElement : SemanticElement<HTMLAnchorElement>

/** [Hidden](https://semantic-ui.com/elements/image.html#hidden) state of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.hidden: State get() = State.Hidden

/** [Disabled](https://semantic-ui.com/elements/image.html#disabled) state of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.disabled: State get() = State.Disabled

/** [Avatar](https://semantic-ui.com/elements/image.html#avatar) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.avatar: Variation get() = Variation.Avatar

/** [Bordered](https://semantic-ui.com/elements/image.html#bordered) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.bordered: Variation get() = Variation.Bordered

/** [Fluid](https://semantic-ui.com/elements/image.html#fluid) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.fluid: Variation get() = Variation.Fluid

/** [Rounded](https://semantic-ui.com/elements/image.html#rounded) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.rounded: Variation get() = Variation.Rounded

/** [Circular](https://semantic-ui.com/elements/image.html#circular) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.circular: Variation get() = Variation.Circular

/** [Vertically aligned](https://semantic-ui.com/elements/image.html#vertically-aligned) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.verticallyAligned: Variation get() = Variation.VerticallyAligned

/** [Centered](https://semantic-ui.com/elements/image.html#centered) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.centered: Variation get() = Variation.Centered

/** [Spaced](https://semantic-ui.com/elements/image.html#spaced) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.spaced: Variation get() = Variation.Spaced

/** [Floated](https://semantic-ui.com/elements/image.html#floated) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.floated: Variation get() = Variation.Floated

/** [Size](https://semantic-ui.com/elements/image.html#size) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused")
public val <TSemantic : ImageElement<Element>> SemanticAttrsScope<TSemantic>.size: Size get() = Variation.Size


/** Creates a [SemanticUI image](https://semantic-ui.com/elements/image.html). */
@Composable
public fun Image(
    attrs: SemanticAttrBuilderContext<ImageDivElement>? = null,
    content: SemanticContentBuilder<ImageDivElement>? = null,
): Unit = SemanticDivElement({ classes("ui"); attrs?.invoke(this); classes("image"); }, content)

/** Creates a [bulleted](https://semantic-ui.com/elements/image.html#bulleted) [SemanticUI image](https://semantic-ui.com/elements/image.html). */
@Composable
public fun ImageAnchor(
    href: String? = null,
    attrs: SemanticAttrBuilderContext<ImageAnchorElement>? = null,
    content: SemanticContentBuilder<ImageAnchorElement>? = null,
): Unit = SemanticElement({ classes("ui"); attrs?.invoke(this); classes("image") }, content) { a, c -> A(href, a, c) }
