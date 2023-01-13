package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
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
