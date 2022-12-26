package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.SemanticAttrBuilder
import com.bkahlert.hello.semanticui.SemanticBuilder
import com.bkahlert.hello.semanticui.SemanticDivElement
import com.bkahlert.hello.semanticui.SemanticElement
import org.w3c.dom.HTMLDivElement

interface DividerElement : SemanticElement

/**
 * Creates a [SemanticUI divider](https://semantic-ui.com/elements/divider.html#divider).
 */
@Composable
fun Divider(
    attrs: SemanticAttrBuilder<DividerElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DividerElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("divider")
    }, content)
}
