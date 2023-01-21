package com.bkahlert.semanticui.collection

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.SemanticAttrsScope
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.A
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement

public interface MenuElement : SemanticElement<HTMLDivElement>

/**
 * Creates a [SemanticUI menu](https://semantic-ui.com/collections/menu.html).
 */
@Composable
public fun Menu(
    attrs: SemanticAttrBuilderContext<MenuElement>? = null,
    content: SemanticContentBuilder<MenuElement>? = null,
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
public fun TextMenu(
    attrs: SemanticAttrBuilderContext<MenuElement>? = null,
    content: SemanticContentBuilder<MenuElement>? = null,
) {
    Menu({
        attrs?.invoke(this)
        classes("text")
    }, content)
}


/**
 * Creates a [SemanticUI sub menu](https://semantic-ui.com/collections/menu.html#sub-menu).
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<MenuElement>.Menu(
    attrs: SemanticAttrBuilderContext<MenuElement>? = null,
    content: SemanticContentBuilder<MenuElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("menu")
    }, content)
}


public interface MenuItemElement<out TElement : Element> : SemanticElement<Element>
public interface MenuItemDivElement : MenuItemElement<HTMLDivElement>
public interface MenuItemAnchorElement : MenuItemElement<HTMLAnchorElement>

/** [Variation.Borderless](https://semantic-ui.com/collections/menu.html#borderless) */
public fun VariationsScope<MenuElement>.borderless(): VariationsScope<MenuElement> = +Modifier.Variation.Borderless

/** [Variation.Borderless](https://semantic-ui.com/collections/menu.html#borderless) */
public fun VariationsScope<MenuItemElement<Element>>.borderless(): VariationsScope<MenuItemElement<Element>> = +Modifier.Variation.Borderless

/**
 * Creates a [SemanticUI item](https://semantic-ui.com/collections/menu.html#content).
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<MenuElement>.Item(
    attrs: SemanticAttrBuilderContext<MenuItemDivElement>? = null,
    content: SemanticContentBuilder<MenuItemDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("item")
    }, content)
}

/**
 * Creates a [SemanticUI header item](https://semantic-ui.com/collections/menu.html#header).
 */
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<MenuElement>.Header(
    attrs: SemanticAttrBuilderContext<MenuItemDivElement>? = null,
    content: SemanticContentBuilder<MenuItemDivElement>? = null,
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
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<MenuElement>.LinkItem(
    attrs: SemanticAttrBuilderContext<MenuItemDivElement>? = null,
    content: SemanticContentBuilder<MenuItemDivElement>? = null,
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
@Suppress("unused", "UnusedReceiverParameter")
@Composable
public fun SemanticElementScope<MenuElement>.AnkerItem(
    href: String? = null,
    attrs: SemanticAttrBuilderContext<MenuItemAnchorElement>? = null,
    content: SemanticContentBuilder<MenuItemAnchorElement>? = null,
) {
    SemanticElement({
        attrs?.invoke(this)
        classes("item")
    }, content) { a, c -> A(href, a, c) }
}


public fun SemanticAttrsScope<MenuItemAnchorElement>.target(value: ATarget = ATarget.Self): AttrsScope<Element> =
    attr("target", value.targetStr)
