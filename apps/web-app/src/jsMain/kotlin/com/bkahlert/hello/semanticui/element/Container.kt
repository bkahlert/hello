package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.SemanticAttrBuilder
import com.bkahlert.hello.semanticui.SemanticBuilder
import com.bkahlert.hello.semanticui.SemanticDivElement
import com.bkahlert.hello.semanticui.SemanticElement
import org.w3c.dom.HTMLDivElement

interface ContainerElement : SemanticElement

/**
 * Creates a [SemanticUI container](https://semantic-ui.com/elements/container.html).
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
 * Creates a [SemanticUI text container](https://semantic-ui.com/elements/container.html).
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
