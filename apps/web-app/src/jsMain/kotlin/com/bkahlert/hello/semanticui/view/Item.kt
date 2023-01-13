package com.bkahlert.hello.semanticui.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
import com.bkahlert.hello.semanticui.dom.SemanticElementScope
import org.w3c.dom.HTMLDivElement

interface ItemElement : SemanticElement<HTMLDivElement>
interface ItemsElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI items](https://semantic-ui.com/views/item.html#items).
 */
@Composable
fun Items(
    attrs: SemanticAttrBuilderContext<ItemsElement>? = null,
    content: SemanticContentBuilder<ItemsElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("item")
    }, content)
}

/**
 * Creates a [SemanticUI item](https://semantic-ui.com/views/item.html).
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
fun SemanticElementScope<ItemsElement>.Item(
    attrs: SemanticAttrBuilderContext<ItemElement>? = null,
    content: SemanticContentBuilder<ItemElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("item")
    }, content)
}
