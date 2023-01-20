package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

public interface ContainerElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI container](https://semantic-ui.com/elements/container.html).
 */
@Composable
public fun Container(
    attrs: SemanticAttrBuilderContext<ContainerElement>? = null,
    content: SemanticContentBuilder<ContainerElement>? = null,
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
public fun TextContainer(
    attrs: SemanticAttrBuilderContext<ContainerElement>? = null,
    content: SemanticContentBuilder<ContainerElement>? = null,
) {
    Container({
        attrs?.invoke(this)
        classes("text")
    }, content)
}
