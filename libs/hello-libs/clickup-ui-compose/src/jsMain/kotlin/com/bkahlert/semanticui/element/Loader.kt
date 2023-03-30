package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

public interface LoaderElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI loader](https://semantic-ui.com/elements/loader.html).
 */
@Composable
public fun Loader(
    attrs: SemanticAttrBuilderContext<LoaderElement>? = null,
    content: SemanticContentBuilder<LoaderElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("loader")
    }, content)
}

/**
 * Creates a [SemanticUI text loader](https://semantic-ui.com/elements/loader.html).
 */
@Composable
public fun TextLoader(
    attrs: SemanticAttrBuilderContext<LoaderElement>? = null,
    content: SemanticContentBuilder<LoaderElement>? = null,
) {
    Loader({
        attrs?.invoke(this)
        classes("text")
    }, content)
}

/**
 * Creates
 * a [SemanticUI loader](https://semantic-ui.com/elements/loader.html), or
 * a [SemanticUI text loader](https://semantic-ui.com/elements/loader.html),
 * depending on whether a [text] is specified.
 */
@Composable
@Suppress("NOTHING_TO_INLINE")
public inline fun Loader(
    text: String?,
    noinline attrs: SemanticAttrBuilderContext<LoaderElement>? = null,
) {
    when (text) {
        null -> Loader(attrs, null)
        else -> TextLoader(attrs) { Text(text) }
    }
}
