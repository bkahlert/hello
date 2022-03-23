package com.semanticui.compose.module

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import org.w3c.dom.HTMLDivElement

interface DimmerElement : SemanticElement

/**
 * Creates a [SemanticUI dimmer](https://semantic-ui.com/modules/dimmer.html#/definition).
 */
@Composable
fun Dimmer(
    attrs: SemanticAttrBuilder<DimmerElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DimmerElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("dimmer")
    }, content)
}
