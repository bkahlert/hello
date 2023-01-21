package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.Modifier.State
import com.bkahlert.semanticui.core.attributes.Modifier.Variation
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Avatar
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Bordered
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Centered
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Circular
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Fluid
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Rounded
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Spaced
import com.bkahlert.semanticui.core.attributes.StatesScope
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Img
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLImageElement

public interface ImageElement<out TElement : Element> : SemanticElement<TElement>
public interface ImageDivElement : ImageElement<HTMLDivElement>
public interface ImageImageElement : ImageElement<HTMLImageElement>
public interface ImageAnchorElement : SemanticElement<HTMLAnchorElement>

/** [State.Hidden](https://semantic-ui.com/elements/image.html#hidden) */
public fun StatesScope<ImageElement<Element>>.hidden(): StatesScope<ImageElement<Element>> = +State.Hidden

/** [State.Disabled](https://semantic-ui.com/elements/image.html#disabled) */
public fun StatesScope<ImageElement<Element>>.disabled(): StatesScope<ImageElement<Element>> = +State.Disabled

/** [Variation.Avatar](https://semantic-ui.com/elements/image.html#avatar) */
public fun VariationsScope<ImageElement<Element>>.avatar(): VariationsScope<ImageElement<Element>> = +Avatar

/** [Variation.Bordered](https://semantic-ui.com/elements/image.html#bordered) */
public fun VariationsScope<ImageElement<Element>>.bordered(): VariationsScope<ImageElement<Element>> = +Bordered

/** [Variation.Fluid](https://semantic-ui.com/elements/image.html#fluid) */
public fun VariationsScope<ImageElement<Element>>.fluid(): VariationsScope<ImageElement<Element>> = +Fluid

/** [Variation.Rounded](https://semantic-ui.com/elements/image.html#rounded) */
public fun VariationsScope<ImageElement<Element>>.rounded(): VariationsScope<ImageElement<Element>> = +Rounded

/** [Variation.Circular](https://semantic-ui.com/elements/image.html#circular) */
public fun VariationsScope<ImageElement<Element>>.circular(): VariationsScope<ImageElement<Element>> = +Circular

/** [Variation.VerticallyAligned aligned](https://semantic-ui.com/elements/image.html#vertically-aligned) */
public fun VariationsScope<ImageElement<Element>>.verticallyAligned(value: Variation.VerticallyAligned): VariationsScope<ImageElement<Element>> = +value

/** [Variation.Centered](https://semantic-ui.com/elements/image.html#centered) */
public fun VariationsScope<ImageElement<Element>>.centered(): VariationsScope<ImageElement<Element>> = +Centered

/** [Variation.Spaced](https://semantic-ui.com/elements/image.html#spaced) */
public fun VariationsScope<ImageElement<Element>>.spaced(): VariationsScope<ImageElement<Element>> = +Spaced

/** [Variation.Floated](https://semantic-ui.com/elements/image.html#floated) */
public fun VariationsScope<ImageElement<Element>>.floated(value: Variation.Floated): VariationsScope<ImageElement<Element>> = +value

/** [Variation.Size](https://semantic-ui.com/elements/image.html#size) */
public fun VariationsScope<ImageElement<Element>>.size(value: Variation.Size): VariationsScope<ImageElement<Element>> = +value


/** Creates a [SemanticUI image](https://semantic-ui.com/elements/image.html). */
@Composable
public fun Image(
    attrs: SemanticAttrBuilderContext<ImageDivElement>? = null,
    content: SemanticContentBuilder<ImageDivElement>? = null,
): Unit = SemanticDivElement({ classes("ui"); attrs?.invoke(this); classes("image"); }, content)

/** Creates a [SemanticUI image](https://semantic-ui.com/elements/image.html). */
@Composable
public fun Image(
    src: CharSequence,
    alt: String = "",
    attrs: SemanticAttrBuilderContext<ImageImageElement>? = null,
): Unit = SemanticElement({ classes("ui"); attrs?.invoke(this); classes("image"); }) { a, _ -> Img(src.toString(), alt, a) }


/** Creates a [SemanticUI image link](https://semantic-ui.com/elements/image.html#image-link). */
@Composable
public fun ImageAnchor(
    href: String? = null,
    attrs: SemanticAttrBuilderContext<ImageAnchorElement>? = null,
    content: SemanticContentBuilder<ImageAnchorElement>? = null,
): Unit = SemanticElement({
    classes("ui")
    attrs?.invoke(this)
    classes("image")
}, content) { a, c -> A(href, a, c) }
