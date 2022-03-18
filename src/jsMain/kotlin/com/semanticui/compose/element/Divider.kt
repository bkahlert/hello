package com.semanticui.compose.element

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
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
