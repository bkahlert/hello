package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

interface DividerElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI divider](https://semantic-ui.com/elements/divider.html#divider).
 */
@Composable
fun Divider(
    attrs: SemanticAttrBuilderContext<DividerElement>? = null,
    content: SemanticContentBuilder<DividerElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("divider")
    }, content)
}
