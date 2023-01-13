package com.bkahlert.hello.semanticui.collection

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
import com.bkahlert.hello.semanticui.dom.SemanticElementScope
import com.bkahlert.hello.semanticui.module.Dropdown
import com.bkahlert.hello.semanticui.module.DropdownElement
import org.jetbrains.compose.web.dom.A
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement

interface MenuElement : SemanticElement

/**
 * Creates a [SemanticUI menu](https://semantic-ui.com/collections/menu.html).
 */
@Composable
fun Menu(
    attrs: SemanticAttrBuilderContext<MenuElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<MenuElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilderContext<MenuElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<MenuElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilderContext<MenuElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<MenuElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilderContext<MenuItemElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<MenuItemElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilderContext<MenuItemElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<MenuItemElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilderContext<MenuItemElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<MenuItemElement, HTMLDivElement>? = null,
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
    attrs: SemanticAttrBuilderContext<MenuItemElement, HTMLAnchorElement>? = null,
    content: SemanticContentBuilder<MenuItemElement, HTMLAnchorElement>? = null,
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
    attrs: SemanticAttrBuilderContext<DropdownElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<DropdownElement, HTMLDivElement>? = null,
) {
    Dropdown({
        attrs?.invoke(this)
        classes("item")
    }, content)
}
