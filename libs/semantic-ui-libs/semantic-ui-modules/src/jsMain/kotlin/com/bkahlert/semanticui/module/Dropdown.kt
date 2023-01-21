package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bkahlert.semanticui.collection.MenuElement
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.VariationsScope
import com.bkahlert.semanticui.core.dataAttr
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.core.dropdown
import com.bkahlert.semanticui.core.jQuery
import com.bkahlert.semanticui.core.toJsonArray
import com.bkahlert.semanticui.core.toJsonArrayOrEmpty
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.paddingRight
import org.w3c.dom.HTMLDivElement

public interface DropdownElement : SemanticElement<HTMLDivElement>

/** [Variation.Scrolling](https://semantic-ui.com/modules/dropdown.html#scrolling) */
public fun VariationsScope<DropdownElement>.scrolling(): VariationsScope<DropdownElement> = +Modifier.Variation.Scrolling

/**
 * Creates a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html#/definition).
 */
@Composable
public fun Dropdown(
    attrs: SemanticAttrBuilderContext<DropdownElement>? = null,
    content: SemanticContentBuilder<DropdownElement>? = null,
) {
    SemanticDivElement<DropdownElement>({
        classes("ui")
        attrs?.invoke(this)
        classes("dropdown")
    }) {
        content?.invoke(this)
        DisposableEffect(Unit) {
            jQuery(scopeElement).dropdown()
            onDispose { }
        }
    }
}

@Stable
public interface DropdownState<T> {
    public val options: Map<String, Any?>
    public val values: List<T>
    public var selection: T?
    public val onSelect: (old: T?, new: T?) -> Unit
    public val serializer: (T) -> String
    public val deserializer: (String) -> T

    public var selectionString: String
        get() = selection?.let(serializer) ?: ""
        set(value) {
            selection = value.takeIf { it.isNotEmpty() }?.let(deserializer)
        }
}

public class DropdownStateImpl<T>(
    override val options: Map<String, Any?>,
    override val values: List<T>,
    selection: T?,
    override val onSelect: (old: T?, new: T?) -> Unit,
    override val serializer: (T) -> String,
    override val deserializer: (String) -> T,
) : DropdownState<T> {
    private var _selection by mutableStateOf(selection)
    override var selection: T?
        get() = _selection
        set(value) {
            val old = _selection
            if (old != value) onSelect(old, value)
            _selection = value
        }
}

private const val MUTED_ATTRIBUTE_NAME = "muted"

/**
 * Creates a [SemanticUI inline dropdown](https://semantic-ui.com/modules/dropdown.html#inline)
 * using the specified [state] to determine if the visual representation needs to be re-created.
 */
@Composable
public fun <T> InlineDropdown(
    state: DropdownState<T>,
    attrs: SemanticAttrBuilderContext<DropdownElement>? = null,
    content: SemanticContentBuilder<DropdownElement>? = null,
) {

    SemanticDivElement<DropdownElement>({
        classes("ui", "inline")
        attrs?.invoke(this)
        classes("dropdown")
        style { paddingRight(.35714286.em) }
    }) {
        content?.invoke(this)
        DisposableEffect(state) {
            jQuery(scopeElement).dropdown(
                *state.options.toList().toTypedArray(),
                "onChange" to fun(value: String) {
                    if (scopeElement.getAttribute("data-$MUTED_ATTRIBUTE_NAME") == null) {
                        if (state.options["debug"] == true) console.log("selection changed by dropdown to $value")
                        state.selectionString = value
                    }
                },
            )
            onDispose { }
        }
        DisposableEffect(state.selection) {
            jQuery(scopeElement)
                .dataAttr(MUTED_ATTRIBUTE_NAME, true)
                .dropdown("set exactly", state.selection.toJsonArrayOrEmpty(state.serializer))
                .dataAttr(MUTED_ATTRIBUTE_NAME, null)
            onDispose { }
        }
    }
}

public interface MultipleDropdownElement : DropdownElement

@Stable
public interface MultipleDropdownState<T> {
    public val options: Map<String, Any?>
    public val values: List<T>
    public var selection: List<T>
    public val onSelect: (old: List<T>, new: List<T>) -> Unit
    public val serializer: (T) -> String
    public val deserializer: (String) -> T

    public var selectionString: String
        get() = selection.joinToString(",") { serializer(it) }
        set(value) {
            selection = value.split(",").filter { it.isNotEmpty() }.map(deserializer)
        }

    public val noValues: Boolean get() = selection.isEmpty()
    public var allValues: Boolean
}

public class MultipleDropdownStateImpl<T>(
    override val options: Map<String, Any?>,
    override val values: List<T> = emptyList(),
    selection: List<T> = emptyList(),
    override val onSelect: (old: List<T>, new: List<T>) -> Unit,
    override val serializer: (T) -> String,
    override val deserializer: (String) -> T,
) : MultipleDropdownState<T> {
    private var _selection by mutableStateOf(selection)
    override var selection: List<T>
        get() = if (_allValues) values else _selection
        set(value) {
            val old = _selection
            if (old != value) onSelect(old, value)
            _selection = value
            if (!values.all { _selection.contains(it) }) {
                _allValues = false
            }
        }

    private var _allValues by mutableStateOf(false)
    override var allValues: Boolean
        get() = _allValues || values.all { _selection.contains(it) }
        set(value) {
            _allValues = value && !values.all { _selection.contains(it) }
        }
}

/**
 * Creates a [SemanticUI inline multiple dropdown](https://semantic-ui.com/modules/dropdown.html#inline)
 * using the specified [state].
 */
@Composable
public fun <T> InlineMultipleDropdown(
    state: MultipleDropdownState<T>,
    attrs: SemanticAttrBuilderContext<MultipleDropdownElement>? = null,
    content: SemanticContentBuilder<MultipleDropdownElement>? = null,
) {
    SemanticDivElement<MultipleDropdownElement>({
        classes("ui", "inline")
        attrs?.invoke(this)
        classes("multiple", "dropdown")
        style { paddingRight(.35714286.em) }
    }) {
        content?.invoke(this)
        DisposableEffect(state) {
            jQuery(scopeElement).dropdown(
                *state.options.toList().toTypedArray(),
                "onChange" to fun(value: String) {
                    if (scopeElement.getAttribute("data-$MUTED_ATTRIBUTE_NAME") == null) {
                        if (state.options["debug"] == true) console.log("selection changed by dropdown to $value")
                        state.selectionString = value
                    }
                },
            )
            onDispose { }
        }
        DisposableEffect(state.selection) {
            jQuery(scopeElement)
                .dataAttr(MUTED_ATTRIBUTE_NAME, true)
                .dropdown("set exactly", state.selection.toJsonArray(state.serializer))
                .dataAttr(MUTED_ATTRIBUTE_NAME, null)
            onDispose { }
        }
    }
}

/**
 * Creates a text element for a [SemanticUI inline dropdown](https://semantic-ui.com/modules/dropdown.html#inline).
 */
@Suppress("unused")
@Composable
public fun SemanticElementScope<DropdownElement>.Text(
    attrs: SemanticAttrBuilderContext<DropdownElement>? = null,
    content: SemanticContentBuilder<DropdownElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("text")
    }, content)
}

/**
 * Creates a [header](https://semantic-ui.com/modules/dropdown.html#header) element for a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html).
 */
@Suppress("unused")
@Composable
public fun SemanticElementScope<DropdownElement>.Header(
    attrs: SemanticAttrBuilderContext<DropdownElement>? = null,
    content: SemanticContentBuilder<DropdownElement>? = null,
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
public fun SemanticElementScope<DropdownElement>.Divider(
    attrs: SemanticAttrBuilderContext<DropdownElement>? = null,
) {
    SemanticDivElement<DropdownElement>({
        attrs?.invoke(this)
        classes("divider")
    })
}

public interface DropdownMenuElement : DropdownElement

/**
 * Creates a [SemanticUI dropdown menu](https://semantic-ui.com/modules/dropdown.html#dropdown).
 */
@Suppress("unused")
@Composable
public fun SemanticElementScope<DropdownElement>.Menu(
    attrs: SemanticAttrBuilderContext<DropdownMenuElement>? = null,
    content: SemanticContentBuilder<DropdownMenuElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("menu")
    }, content)
}


public interface DropdownMenuItemElement : DropdownElement

/**
 * Creates a [SemanticUI dropdown menu item](https://semantic-ui.com/modules/dropdown.html#dropdown).
 */
@Suppress("unused")
@Composable
public fun SemanticElementScope<DropdownMenuElement>.Item(
    attrs: SemanticAttrBuilderContext<DropdownMenuItemElement>? = null,
    content: SemanticContentBuilder<DropdownMenuItemElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("item")
    }, content)
}

/**
 * Creates a [SemanticUI dropdown item](https://semantic-ui.com/collections/menu.html#dropdown-item).
 */
@Suppress("unused")
@Composable
public fun SemanticElementScope<MenuElement>.DropdownItem(
    attrs: SemanticAttrBuilderContext<DropdownElement>? = null,
    content: SemanticContentBuilder<DropdownElement>? = null,
) {
    Dropdown({
        attrs?.invoke(this)
        classes("item")
    }, content)
}
