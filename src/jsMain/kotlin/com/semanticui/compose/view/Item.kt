package com.semanticui.compose.view

import androidx.compose.runtime.Composable
import com.semanticui.compose.Modifier
import com.semanticui.compose.classNames
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

/**
 * Creates a [SemanticUI item](https://semantic-ui.com/views/item.html).
 */
@Composable
fun Item(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        attrs?.invoke(this)
    }, content)
}

/**
 * Creates a [SemanticUI item](https://semantic-ui.com/views/item.html).
 */
@Composable
fun Item(
    modifier: Modifier, vararg modifiers: Modifier,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Item({
        classes(*modifier.classNames, *modifiers.classNames, "item")
        attrs?.invoke(this)
    }, content)
}

/**
 * Creates a [SemanticUI items](https://semantic-ui.com/views/item.html#items).
 */
@Composable
fun Items(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        classes("ui", *modifiers.classNames, "items")
    }, content)
}
