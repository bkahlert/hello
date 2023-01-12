package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.compose.Image
import com.bkahlert.hello.semanticui.SemanticAttrBuilder
import com.bkahlert.hello.semanticui.SemanticAttrsScope
import com.bkahlert.hello.semanticui.SemanticBuilder
import com.bkahlert.hello.semanticui.SemanticDivElement
import com.bkahlert.hello.semanticui.SemanticElement
import com.bkahlert.hello.semanticui.State
import com.bkahlert.hello.semanticui.Variation
import com.bkahlert.hello.semanticui.Variation.Size
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Img
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLImageElement

interface ImageElement : SemanticElement

/** [Hidden](https://semantic-ui.com/elements/image.html#hidden) state of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.hidden: State get() = State.Hidden

/** [Disabled](https://semantic-ui.com/elements/image.html#disabled) state of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.disabled: State get() = State.Disabled

/** [Avatar](https://semantic-ui.com/elements/image.html#avatar) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.avatar: Variation get() = Variation.Avatar

/** [Bordered](https://semantic-ui.com/elements/image.html#bordered) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.bordered: Variation get() = Variation.Bordered

/** [Fluid](https://semantic-ui.com/elements/image.html#fluid) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.fluid: Variation get() = Variation.Fluid

/** [Rounded](https://semantic-ui.com/elements/image.html#rounded) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.rounded: Variation get() = Variation.Rounded

/** [Circular](https://semantic-ui.com/elements/image.html#circular) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.circular: Variation get() = Variation.Circular

/** [Vertically aligned](https://semantic-ui.com/elements/image.html#vertically-aligned) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.verticallyAligned: Variation get() = Variation.VerticallyAligned

/** [Centered](https://semantic-ui.com/elements/image.html#centered) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.centered: Variation get() = Variation.Centered

/** [Spaced](https://semantic-ui.com/elements/image.html#spaced) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.spaced: Variation get() = Variation.Spaced

/** [Floated](https://semantic-ui.com/elements/image.html#floated) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.floated: Variation get() = Variation.Floated

/** [Size](https://semantic-ui.com/elements/image.html#size) variation of a [image](https://semantic-ui.com/elements/image.html). */
@Suppress("unused") val <TSemantic : ImageElement> SemanticAttrsScope<TSemantic, *>.size: Size get() = Variation.Size


/** Creates a [SemanticUI image](https://semantic-ui.com/elements/image.html). */
@Composable
fun Image(
    attrs: SemanticAttrBuilder<ImageElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ImageElement, HTMLDivElement>? = null,
) = SemanticDivElement({ classes("ui"); attrs?.invoke(this); classes("image"); }, content)

/** Creates a [SemanticUI image](https://semantic-ui.com/elements/image.html). */
@Composable
fun Image(
    image: Image,
    alt: String = "",
    attrs: SemanticAttrBuilder<ImageElement, HTMLImageElement>? = null,
) = SemanticElement<ImageElement, HTMLImageElement>({ classes("ui"); attrs?.invoke(this); classes("image"); }) { a, _ -> Img(image.dataURI, alt, a) }

/** Creates a [bulleted](https://semantic-ui.com/elements/image.html#bulleted) [SemanticUI image](https://semantic-ui.com/elements/image.html). */
@Composable
fun ImageLink(
    href: String? = null,
    attrs: SemanticAttrBuilder<ImageElement, HTMLAnchorElement>? = null,
    content: SemanticBuilder<ImageElement, HTMLAnchorElement>? = null,
) = SemanticElement({ classes("ui"); attrs?.invoke(this); classes("image") }, content) { a, c -> A(href, a, c) }
