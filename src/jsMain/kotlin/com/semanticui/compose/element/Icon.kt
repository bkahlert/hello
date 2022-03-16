package com.semanticui.compose.element

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticElement
import org.jetbrains.compose.web.dom.I
import org.w3c.dom.HTMLElement

interface IconElement : SemanticElement

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/elements/icon.html#/definition)
 * using the specified [name] and [modifiers].
 *
 * @see <a href="https://semantic-ui.com/elements/icon.html#/icon">Icons</a>
 */
@Composable
fun Icon(
    name: String,
    attrs: SemanticAttrBuilder<IconElement, HTMLElement>? = null,
    content: SemanticBuilder<IconElement, HTMLElement>? = null,
) {
    SemanticElement({
        attrs?.invoke(this)
        classes(name, "icon")
    }, content) { a, c -> I(a, c) }
}

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/elements/icon.html#/definition)
 * using the specified [name1], [name2] and [modifiers].
 *
 * @see <a href="https://semantic-ui.com/elements/icon.html#/icon">Icons</a>
 */
@Composable
fun Icon(
    name1: String,
    name2: String,
    attrs: SemanticAttrBuilder<IconElement, HTMLElement>? = null,
    content: SemanticBuilder<IconElement, HTMLElement>? = null,
) {
    SemanticElement({
        attrs?.invoke(this)
        classes(name1, name2, "icon")
    }, content) { a, c -> I(a, c) }
}

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/elements/icon.html#/definition)
 * using the specified [name1], [name2], [name3] and [modifiers].
 *
 * @see <a href="https://semantic-ui.com/elements/icon.html#/icon">Icons</a>
 */
@Composable
fun Icon(
    name1: String,
    name2: String,
    name3: String,
    attrs: SemanticAttrBuilder<IconElement, HTMLElement>? = null,
    content: SemanticBuilder<IconElement, HTMLElement>? = null,
) {
    SemanticElement({
        attrs?.invoke(this)
        classes(name1, name2, name3, "icon")
    }, content) { a, c -> I(a, c) }
}

/**
 * Creates a [SemanticUI icon group](https://semantic-ui.com/elements/icon.html#icons)
 * using the specified [modifiers] and icons created by the specified [content].
 *
 * @see <a href="https://semantic-ui.com/elements/icon.html#/icon">Icons</a>
 */
@Composable
fun IconGroup(
    attrs: SemanticAttrBuilder<IconElement, HTMLElement>? = null,
    content: SemanticBuilder<IconElement, HTMLElement>? = null,
) {
    SemanticElement({
        attrs?.invoke(this)
        classes("icons")
    }, content) { a, c -> I(a, c) }
}
