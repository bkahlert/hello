package com.bkahlert.hello.semanticui.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.SemanticAttrBuilder
import com.bkahlert.hello.semanticui.SemanticBuilder
import com.bkahlert.hello.semanticui.SemanticDivElement
import com.bkahlert.hello.semanticui.SemanticElement
import com.bkahlert.hello.semanticui.SemanticElementScope
import org.w3c.dom.HTMLDivElement

interface ItemElement : SemanticElement
interface ItemsElement : SemanticElement

/**
 * Creates a [SemanticUI items](https://semantic-ui.com/views/item.html#items).
 */
@Composable
fun Items(
    attrs: SemanticAttrBuilder<ItemsElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ItemsElement, HTMLDivElement>? = null,
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
@Suppress("unused")
@Composable
fun SemanticElementScope<ItemsElement, *>.Item(
    attrs: SemanticAttrBuilder<ItemElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ItemElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("item")
    }, content)
}
