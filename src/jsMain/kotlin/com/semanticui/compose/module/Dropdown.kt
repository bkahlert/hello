package com.semanticui.compose.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.semanticui.compose.Modifier
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.classNames
import com.semanticui.compose.collection.MenuElement
import com.semanticui.compose.dropdown
import com.semanticui.compose.jQuery
import com.semanticui.compose.module.Dropdown.Type
import io.ktor.client.fetch.ArrayLike
import org.jetbrains.compose.web.attributes.InputType.Hidden
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

interface DropdownElement : SemanticElement

/**
 * Creates a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html#/definition)
 * using the specified [key] to determine if the visual representation needs to be updated.
 */
@Composable
fun Dropdown(
    key: Any?,
    onChange: (value: String, text: String, selectedItem: ArrayLike<HTMLElement>) -> Unit = { _, _, _ -> },
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
) {
    SemanticDivElement<DropdownElement>({
        classes("ui")
        attrs?.invoke(this)
        classes("dropdown")
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

/**
 * Creates a [header](https://semantic-ui.com/modules/dropdown.html#header) element for a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<DropdownElement, *>.Header(
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("header")
    }, content)
}

/**
 * Creates a [divider](https://semantic-ui.com/modules/dropdown.html#divider) element for a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<DropdownElement, *>.Divider(
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("divider")
    }, content)
}

/**
 * Creates a [SemanticUI inline dropdown](https://semantic-ui.com/modules/dropdown.html#inline)
 * using the specified [key] to determine if the visual representation needs to be updated.
 */
@Composable
fun InlineDropdown(
    key: Any?,
    onChange: (value: String, text: String, selectedItem: ArrayLike<HTMLElement>) -> Unit = { _, _, _ -> },
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
) {
    Dropdown(key, onChange, {
        attrs?.invoke(this)
        classes("inline")
    }, content)
}

/**
 * Creates a text element for a [SemanticUI inline dropdown](https://semantic-ui.com/modules/dropdown.html#inline).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<DropdownElement, *>.DropdownText(
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("text")
    }, content)
}

/**
 * Creates a [SemanticUI dropdown menu](https://semantic-ui.com/modules/dropdown.html#dropdown).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<DropdownElement, *>.DropdownMenu(
    attrs: SemanticAttrBuilder<MenuElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MenuElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("menu")
    }, content)
}

@Deprecated("use dropdown with semantic builder")
object Dropdown {
    sealed class Type(override vararg val classNames: String) : Modifier {
        object Dropdown : Type()
        object Selection : Type("selection")
        object SearchSelection : Type("search", "selection")
        object MultipleSelection : Type("multiple", "selection")
        object MultipleSearchSelection : Type("multiple", "search", "selection")
        object Inline : Type("inline")
        object Pointing : Type("pointing")
        object Floating : Type("floating")
        object Simple : Type("simple")
    }
}

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/modules/dropdown.html#/definition)
 * using the specified [key] to determine if the visual representation needs to be updated.
 */
@Deprecated("use dropdown with semantic builder")
@Composable
fun Dropdown(
    key: Any?,
    defaultText: String? = null,
    type: Type = Type.Dropdown,
    vararg modifiers: Modifier,
    onChange: (value: String, text: String, selectedItem: ArrayLike<HTMLElement>) -> Unit = { _, _, _ -> },
    menuContent: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        classes("ui", *modifiers.classNames, *type.classNames, "dropdown")
    }) {
        Input(Hidden)
        I({
            classes("dropdown", "icon")
        })
        if (defaultText != null) {
            Div({
                classes("default", "text")
            }) { Text(defaultText) }
        }
        Div({
            classes("menu")
        }, menuContent)
        DisposableEffect(key) {
            jQuery(scopeElement).dropdown(
                "action" to "activate",
                "onChange" to onChange,
            )
            onDispose { }
        }
    }
}

/**
 * Creates a [SemanticUI icon](https://semantic-ui.com/modules/dropdown.html#/definition)
 * using the specified [key] to determine if the visual representation needs to be updated.
 */
@Deprecated("use dropdown with semantic builder")
@Composable
fun <E> Dropdown(
    items: Iterable<E>,
    defaultText: String? = null,
    type: Type = Type.Dropdown,
    vararg modifiers: Modifier,
    onChange: (value: String, text: String, selectedItem: ArrayLike<HTMLElement>) -> Unit = { _, _, _ -> },
    itemContent: @Composable (E) -> Unit,
) {
    Dropdown(items, defaultText, type, *modifiers, onChange = onChange) {
        items.forEach { itemContent(it) }
    }
}
