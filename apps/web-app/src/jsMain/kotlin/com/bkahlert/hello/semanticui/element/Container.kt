package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

interface ContainerElement : SemanticElement

/**
 * Creates a [SemanticUI container](https://semantic-ui.com/elements/container.html).
 */
@Composable
fun Container(
    attrs: SemanticAttrBuilderContext<ContainerElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<ContainerElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("container")
    }, content)
}


/**
 * Creates a [SemanticUI text container](https://semantic-ui.com/elements/container.html).
 */
@Composable
fun TextContainer(
    attrs: SemanticAttrBuilderContext<ContainerElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<ContainerElement, HTMLDivElement>? = null,
) {
    Container({
        attrs?.invoke(this)
        classes("text")
    }, content)
}
