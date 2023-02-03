package com.bkahlert.semanticui.view

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import org.w3c.dom.HTMLDivElement

public interface ItemElement : SemanticElement<HTMLDivElement>
public interface ItemsElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI items](https://semantic-ui.com/views/item.html#items).
 */
@Composable
public fun Items(
    attrs: SemanticAttrBuilderContext<ItemsElement>? = null,
    content: SemanticContentBuilder<ItemsElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("items")
    }, content)
}

/**
 * Creates a [SemanticUI item](https://semantic-ui.com/views/item.html).
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<ItemsElement>.Item(
    attrs: SemanticAttrBuilderContext<ItemElement>? = null,
    content: SemanticContentBuilder<ItemElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("item")
    }, content)
}
