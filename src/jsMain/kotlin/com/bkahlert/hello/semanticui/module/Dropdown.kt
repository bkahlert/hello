package com.bkahlert.hello.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bkahlert.hello.semanticui.SemanticAttrBuilder
import com.bkahlert.hello.semanticui.SemanticAttrsScope
import com.bkahlert.hello.semanticui.SemanticBuilder
import com.bkahlert.hello.semanticui.SemanticDivElement
import com.bkahlert.hello.semanticui.SemanticElement
import com.bkahlert.hello.semanticui.SemanticElementScope
import com.bkahlert.hello.semanticui.Variation
import com.bkahlert.hello.semanticui.dropdown
import com.bkahlert.hello.semanticui.jQuery
import com.bkahlert.hello.semanticui.toJsonArray
import com.bkahlert.hello.semanticui.toJsonArrayOrEmpty
import com.bkahlert.kommons.dom.data
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.paddingRight
import org.w3c.dom.HTMLDivElement

interface DropdownElement : SemanticElement

/** [Scrolling](https://semantic-ui.com/modules/dropdown.html#scrolling) variation of [dropdown](https://semantic-ui.com/modules/dropdown.html). */
@Suppress("unused") val <TSemantic : DropdownElement> SemanticAttrsScope<TSemantic, *>.scrolling: Variation.Scrolling get() = Variation.Scrolling

/**
 * Creates a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html#/definition).
 */
@Composable
fun Dropdown(
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
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
interface DropdownState<T> {
    val options: Map<String, Any?>
    val values: List<T>
    var selection: T?
    val onSelect: (old: T?, new: T?) -> Unit
    val serializer: (T) -> String
    val deserializer: (String) -> T

    var selectionString: String
        get() = selection?.let(serializer) ?: ""
        set(value) {
            selection = value.takeIf { it.isNotEmpty() }?.let(deserializer)
        }
}

class DropdownStateImpl<T>(
    override val options: Map<String, Any?>,
    override val values: List<T>,
    selection: T?,
    override val onSelect: (old: T?, new: T?) -> Unit,
    override val serializer: (T) -> String,
    override val deserializer: (String) -> T,
) : DropdownState<T> {
    private var _selection by mutableStateOf(selection)
    override var selection
        get() = _selection
        set(value) {
            val old = _selection
            if (old != value) onSelect(old, value)
            _selection = value
        }
}

/**
 * Creates a [SemanticUI inline dropdown](https://semantic-ui.com/modules/dropdown.html#inline)
 * using the specified [state] to determine if the visual representation needs to be re-created.
 */
@Composable
fun <T> InlineDropdown(
    state: DropdownState<T>,
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
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
                    if (scopeElement.data("muted") == null) {
                        if (state.options["debug"] == true) console.log("selection changed by dropdown to $value")
                        state.selectionString = value
                    }
                },
            )
            onDispose { }
        }
        DisposableEffect(state.selection) {
            jQuery(scopeElement)
                .attr("data-muted", true)
                .dropdown("set exactly", state.selection.toJsonArrayOrEmpty(state.serializer))
                .attr("data-muted", null)
            onDispose { }
        }
    }
}

interface MultipleDropdownElement : DropdownElement

@Stable
interface MultipleDropdownState<T> {
    val options: Map<String, Any?>
    val values: List<T>
    var selection: List<T>
    val onSelect: (old: List<T>, new: List<T>) -> Unit
    val serializer: (T) -> String
    val deserializer: (String) -> T

    var selectionString: String
        get() = selection.joinToString(",") { serializer(it) }
        set(value) {
            selection = value.split(",").filter { it.isNotEmpty() }.map(deserializer)
        }

    val noValues: Boolean get() = selection.isEmpty()
    var allValues: Boolean
}

class MultipleDropdownStateImpl<T>(
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
fun <T> InlineMultipleDropdown(
    state: MultipleDropdownState<T>,
    attrs: SemanticAttrBuilder<MultipleDropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MultipleDropdownElement, HTMLDivElement>? = null,
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
                    if (scopeElement.data("muted") == null) {
                        if (state.options["debug"] == true) console.log("selection changed by dropdown to $value")
                        state.selectionString = value
                    }
                },
            )
            onDispose { }
        }
        DisposableEffect(state.selection) {
            jQuery(scopeElement)
                .attr("data-muted", true)
                .dropdown("set exactly", state.selection.toJsonArray(state.serializer))
                .attr("data-muted", null)
            onDispose { }
        }
    }
}

/**
 * Creates a text element for a [SemanticUI inline dropdown](https://semantic-ui.com/modules/dropdown.html#inline).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<DropdownElement, *>.Text(
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
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
) {
    SemanticDivElement<DropdownElement>({
        attrs?.invoke(this)
        classes("divider")
    })
}

interface DropdownMenuElement : DropdownElement

/**
 * Creates a [SemanticUI dropdown menu](https://semantic-ui.com/modules/dropdown.html#dropdown).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<DropdownElement, *>.Menu(
    attrs: SemanticAttrBuilder<DropdownMenuElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownMenuElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("menu")
    }, content)
}


interface DropdownMenuItemElement : DropdownElement

/**
 * Creates a [SemanticUI dropdown menu item](https://semantic-ui.com/modules/dropdown.html#dropdown).
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<DropdownMenuElement, *>.Item(
    attrs: SemanticAttrBuilder<DropdownMenuItemElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownMenuItemElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes("item")
    }, content)
}
