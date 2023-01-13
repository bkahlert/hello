package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticElement
import org.jetbrains.compose.web.dom.I
import org.w3c.dom.HTMLElement

interface IconElement : SemanticElement<HTMLElement>

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/elements/icon.html#/definition)
 * using the specified [name].
 *
 * @see <a href="https://semantic-ui.com/elements/icon.html#/icon">Icons</a>
 */
@Composable
fun Icon(
    vararg name: String,
    attrs: SemanticAttrBuilderContext<IconElement>? = null,
) {
    SemanticElement<IconElement>({
        attrs?.invoke(this)
        classes(*name, "icon")
    }) { a, c -> I(a, c) }
}

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/elements/icon.html#/definition)
 * using the specified [names].
 *
 * @see <a href="https://semantic-ui.com/elements/icon.html#/icon">Icons</a>
 */
@Composable
fun Icon(
    names: List<String>,
    attrs: SemanticAttrBuilderContext<IconElement>? = null,
) {
    SemanticElement<IconElement>({
        attrs?.invoke(this)
        classes(*names.toTypedArray(), "icon")
    }) { a, c -> I(a, c) }
}

/**
 * Creates a [SemanticUI icon group](https://semantic-ui.com/elements/icon.html#icons)
 * using the specified [modifiers] and icons created by the specified [content].
 *
 * @see <a href="https://semantic-ui.com/elements/icon.html#/icon">Icons</a>
 */
@Composable
fun IconGroup(
    attrs: SemanticAttrBuilderContext<IconElement>? = null,
    content: SemanticContentBuilder<IconElement>? = null,
) {
    SemanticElement({
        attrs?.invoke(this)
        classes("icons")
    }, content) { a, c -> I(a, c) }
}
