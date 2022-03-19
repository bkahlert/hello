package com.semanticui.compose.element

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import org.w3c.dom.HTMLDivElement

interface ContainerElement : SemanticElement

/**
 * Creates a [SemanticUI content container](https://semantic-ui.com/elements/container.html).
 */
@Composable
fun Container(
    attrs: SemanticAttrBuilder<ContainerElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ContainerElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("container")
    }, content)
}


/**
 * Creates a [SemanticUI content sub container](https://semantic-ui.com/elements/container.html).
 */
@Composable
fun TextContainer(
    attrs: SemanticAttrBuilder<ContainerElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ContainerElement, HTMLDivElement>? = null,
) {
    Container({
        attrs?.invoke(this)
        classes("text")
    }, content)
}
