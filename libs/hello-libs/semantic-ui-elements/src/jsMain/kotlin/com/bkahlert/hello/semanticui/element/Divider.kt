package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
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
