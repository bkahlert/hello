package com.semanticui.compose.collection

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.module.Dropdown
import com.semanticui.compose.module.DropdownElement
import com.semanticui.compose.view.ItemElement
import io.ktor.client.fetch.ArrayLike
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

interface MenuElement : SemanticElement

/**
 * Creates a [SemanticUI menu](https://semantic-ui.com/collections/menu.html).
 */
@Composable
fun Menu(
    attrs: SemanticAttrBuilder<MenuElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MenuElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("menu")
    }, content)
}


/**
 * Creates a [SemanticUI sub menu](https://semantic-ui.com/collections/menu.html#sub-menu).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement, *>.SubMenu(
    attrs: SemanticAttrBuilder<MenuElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MenuElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("menu")
    }, content)
}


/**
 * Creates a [SemanticUI link item](https://semantic-ui.com/collections/menu.html#link-item).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement, *>.LinkItem(
    attrs: SemanticAttrBuilder<ItemElement, HTMLDivElement>? = null,
    content: SemanticBuilder<ItemElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("link", "item")
    }, content)
}


/**
 * Creates a [SemanticUI dropdown item](https://semantic-ui.com/collections/menu.html#dropdown-item).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement, *>.DropdownItem(
    key: Any?,
    onChange: (value: String, text: String, selectedItem: ArrayLike<HTMLElement>) -> Unit = { _, _, _ -> },
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
) {
    Dropdown(key, onChange, {
        attrs?.invoke(this)
        classes("item")
    }, content)
}
