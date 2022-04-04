package com.semanticui.compose.view

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
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
