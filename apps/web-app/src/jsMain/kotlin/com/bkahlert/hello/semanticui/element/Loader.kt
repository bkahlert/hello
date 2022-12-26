package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.SemanticAttrBuilder
import com.bkahlert.hello.semanticui.SemanticBuilder
import com.bkahlert.hello.semanticui.SemanticDivElement
import com.bkahlert.hello.semanticui.SemanticElement
import org.w3c.dom.HTMLDivElement

interface LoaderElement : SemanticElement

/**
 * Creates a [SemanticUI loader](https://semantic-ui.com/elements/loader.html).
 */
@Composable
fun Loader(
    attrs: SemanticAttrBuilder<LoaderElement, HTMLDivElement>? = null,
    content: SemanticBuilder<LoaderElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilder<LoaderElement, HTMLDivElement>? = null,
    content: SemanticBuilder<LoaderElement, HTMLDivElement>? = null,
) {
    Loader({
        attrs?.invoke(this)
        classes("text")
    }, content)
}
