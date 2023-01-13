package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

interface LoaderElement : SemanticElement

/**
 * Creates a [SemanticUI loader](https://semantic-ui.com/elements/loader.html).
 */
@Composable
fun Loader(
    attrs: SemanticAttrBuilderContext<LoaderElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<LoaderElement, HTMLDivElement>? = null,
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
fun TextLoader(
    attrs: SemanticAttrBuilderContext<LoaderElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<LoaderElement, HTMLDivElement>? = null,
) {
    Loader({
        attrs?.invoke(this)
        classes("text")
    }, content)
}
