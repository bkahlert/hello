package com.semanticui.compose.element

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import org.w3c.dom.HTMLDivElement

interface InputElement : SemanticElement

/**
 * Creates a [SemanticUI input](https://semantic-ui.com/elements/input.html).
 */
@Composable
fun Input(
    attrs: SemanticAttrBuilder<InputElement, HTMLDivElement>? = null,
    content: SemanticBuilder<InputElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("input")
    }, content)
}
