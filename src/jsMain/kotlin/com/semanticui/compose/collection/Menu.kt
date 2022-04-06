package com.semanticui.compose.collection

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.module.Dropdown
import com.semanticui.compose.module.DropdownElement
import org.jetbrains.compose.web.dom.A
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement

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
 * Creates a [text](https://semantic-ui.com/collections/menu.html#text) [SemanticUI menu](https://semantic-ui.com/collections/menu.html).
 */
@Composable
fun TextMenu(
    attrs: SemanticAttrBuilder<MenuElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MenuElement, HTMLDivElement>? = null,
) {
    Menu({
        attrs?.invoke(this)
        classes("text")
    }, content)
}


/**
 * Creates a [SemanticUI sub menu](https://semantic-ui.com/collections/menu.html#sub-menu).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement, *>.Menu(
    attrs: SemanticAttrBuilder<MenuElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MenuElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("menu")
    }, content)
}

interface MenuItemElement : SemanticElement

/**
 * Creates a [SemanticUI item](https://semantic-ui.com/collections/menu.html#content).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement, *>.Item(
    attrs: SemanticAttrBuilder<MenuItemElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MenuItemElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("item")
    }, content)
}

/**
 * Creates a [SemanticUI header item](https://semantic-ui.com/collections/menu.html#header).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement, *>.Header(
    attrs: SemanticAttrBuilder<MenuItemElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MenuItemElement, HTMLDivElement>? = null,
) {
    Item({
        attrs?.invoke(this)
        classes("header")
    }, content)
}


/**
 * Creates a [SemanticUI link item](https://semantic-ui.com/collections/menu.html#link-item).
 * @see [AnkerItem]
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement, *>.LinkItem(
    attrs: SemanticAttrBuilder<MenuItemElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MenuItemElement, HTMLDivElement>? = null,
) {
    Item({
        attrs?.invoke(this)
        classes("link")
    }, content)
}

/**
 * Creates a [SemanticUI link item](https://semantic-ui.com/collections/menu.html#link-item).
 * @see [LinkItem]
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement, *>.AnkerItem(
    href: String? = null,
    attrs: SemanticAttrBuilder<MenuItemElement, HTMLAnchorElement>? = null,
    content: SemanticBuilder<MenuItemElement, HTMLAnchorElement>? = null,
) {
    SemanticElement({
        attrs?.invoke(this)
        classes("item")
    }, content) { a, c -> A(href, a, c) }
}


/**
 * Creates a [SemanticUI dropdown item](https://semantic-ui.com/collections/menu.html#dropdown-item).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement, *>.DropdownItem(
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
) {
    Dropdown({
        attrs?.invoke(this)
        classes("item")
    }, content)
}
