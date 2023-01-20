package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.jetbrains.compose.web.dom.I
import org.w3c.dom.HTMLElement

public interface IconElement : SemanticElement<HTMLElement>

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/elements/icon.html#/definition)
 * using the specified [name].
 *
 * @see <a href="https://semantic-ui.com/elements/icon.html#/icon">Icons</a>
 */
@Composable
public fun Icon(
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
public fun Icon(
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
public fun IconGroup(
    attrs: SemanticAttrBuilderContext<IconElement>? = null,
    content: SemanticContentBuilder<IconElement>? = null,
) {
    SemanticElement({
        attrs?.invoke(this)
        classes("icons")
    }, content) { a, c -> I(a, c) }
}
