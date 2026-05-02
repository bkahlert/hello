package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

public interface ButtonElement<out TElement : Element> : SemanticElement<TElement>
public interface ButtonDivElement : ButtonElement<HTMLDivElement>


/**
 * Creates a [SemanticUI button](https://semantic-ui.com/elements/button.html).
 */
@Composable
public fun Button(
    attrs: SemanticAttrBuilderContext<ButtonDivElement>? = null,
    content: SemanticContentBuilder<ButtonDivElement>? = null,
): Unit = SemanticDivElement({
    classes("ui")
    attrs?.invoke(this)
    classes("button")
}, content)

/**
 * Creates a [SemanticUI icon button](https://semantic-ui.com/elements/button.html#icon).
 */
@Suppress("NOTHING_TO_INLINE") // = avoidance of unnecessary recomposition scope
@Composable
public inline fun IconButton(
    noinline attrs: SemanticAttrBuilderContext<ButtonDivElement>? = null,
    noinline content: SemanticContentBuilder<ButtonDivElement>? = null,
): Unit = Button({
    classes("icon")
    attrs?.invoke(this)
}, content)

/**
 * Creates a [SemanticUI basic button](https://semantic-ui.com/elements/button.html#basic).
 */
@Suppress("NOTHING_TO_INLINE") // = avoidance of unnecessary recomposition scope
@Composable
public inline fun BasicButton(
    noinline attrs: SemanticAttrBuilderContext<ButtonDivElement>? = null,
    noinline content: SemanticContentBuilder<ButtonDivElement>? = null,
): Unit = Button({
    classes("basic")
    attrs?.invoke(this)
}, content)
