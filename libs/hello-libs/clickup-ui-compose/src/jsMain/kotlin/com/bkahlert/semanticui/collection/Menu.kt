package com.bkahlert.semanticui.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bkahlert.semanticui.core.attributes.SemanticAttrsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
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
    val semanticAttrs: SemanticAttrsScope<MenuElement>.() -> Unit = {
        classes("ui")
        attrs?.invoke(this)
        classes("menu")
    }

    val semanticContent: SemanticContentBuilder<MenuElement>? = when (content) {
        null -> null
        else -> {
            {
                content()
                DisposableEffect(attrs, content) {
                    // If the added menu is effectively a sub menu, remove its `ui` class.
                    // Hacky but pragmatic since collection components can exist on their own
                    // but "as well contain their own content" (https://semantic-ui.com/introduction/glossary.html#project-terminology).
                    if (scopeElement.parentElement?.classList?.contains("menu") == true) {
                        scopeElement.classList.remove("ui")
                    }
                    onDispose { }
                }
            }
        }
    }
    SemanticDivElement(
        semanticAttrs = semanticAttrs,
        semanticContent = semanticContent,
    )
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
 * Creates a [SemanticUI link item](https://semantic-ui.com/collections/menu.html#link-item).
 * @see [AnchorItem]
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
public fun SemanticElementScope<MenuElement>.AnchorItem(
    href: String? = null,
    attrs: SemanticAttrBuilderContext<MenuItemAnchorElement>? = null,
    content: SemanticContentBuilder<MenuItemAnchorElement>? = null,
) {
    SemanticElement({
        attrs?.invoke(this)
        classes("item")
    }, content) { a, c -> A(href, a, c) }
}
