package com.bkahlert.hello.semanticui.module

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.SemanticAttrBuilder
import com.bkahlert.hello.semanticui.SemanticBuilder
import com.bkahlert.hello.semanticui.SemanticDivElement
import com.bkahlert.hello.semanticui.SemanticElement
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
