package com.bkahlert.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

public interface DividerElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI divider](https://semantic-ui.com/elements/divider.html#divider).
 */
@Composable
public fun Divider(
    attrs: SemanticAttrBuilderContext<DividerElement>? = null,
    content: SemanticContentBuilder<DividerElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("divider")
    }, content)
}
