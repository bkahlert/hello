package com.bkahlert.hello.semanticui.module

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

public interface DimmerElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI dimmer](https://semantic-ui.com/modules/dimmer.html#/definition).
 */
@Composable
public fun Dimmer(
    attrs: SemanticAttrBuilderContext<DimmerElement>? = null,
    content: SemanticContentBuilder<DimmerElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("dimmer")
    }, content)
}
