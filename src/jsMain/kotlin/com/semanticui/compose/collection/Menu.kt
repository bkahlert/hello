package com.semanticui.compose.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.semanticui.compose.Modifier
import com.semanticui.compose.classNames
import com.semanticui.compose.dropdown
import com.semanticui.compose.jQuery
import io.ktor.client.fetch.ArrayLike
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

/**
 * Creates a [SemanticUI menu](https://semantic-ui.com/collections/menu.html).
 */
@Composable
fun Menu(
    vararg modifiers: Modifier,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        classes("ui", *modifiers.classNames, "menu")
    }, content)
}

/**
 * Creates a [SemanticUI dropdown item](https://semantic-ui.com/collections/menu.html#dropdown-item).
 */
@Composable
fun DropdownItem(
    key: Any?,
    vararg modifiers: Modifier,
    onChange: (value: String, text: String, selectedItem: ArrayLike<HTMLElement>) -> Unit = { _, _, _ -> },
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        classes("ui", *modifiers.classNames, "dropdown", "item")
    }) {
        content?.invoke(this)
        DisposableEffect(key) {
            jQuery(scopeElement).dropdown(
                "action" to "activate",
                "onChange" to onChange,
            )
            onDispose { }
        }
    }
}
