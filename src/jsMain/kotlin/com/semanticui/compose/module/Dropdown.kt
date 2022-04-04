package com.semanticui.compose.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.dom.data
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticAttrsScope
import com.semanticui.compose.SemanticAttrsScope.Companion.or
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.Variation
import com.semanticui.compose.dropdown
import com.semanticui.compose.jQuery
import com.semanticui.compose.toJsonArray
import org.jetbrains.compose.web.attributes.AttrsScopeBuilder
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.paddingRight
import org.w3c.dom.HTMLDivElement
import kotlin.js.json

interface DropdownElement : SemanticElement

/** Whether to provide standard debug output to console. */
var <TSemantic : DropdownElement> SemanticAttrsScope<TSemantic, *>.debug: Boolean? by SemanticAttrsScope or null

/** Messages to appear on the dropdown. */
var <TSemantic : DropdownElement> SemanticAttrsScope<TSemantic, *>.message: Map<String, String?> by SemanticAttrsScope or emptyMap()

/** What to display if nothing is selected. */
var <TSemantic : DropdownElement> SemanticAttrsScope<TSemantic, *>.placeholder: String? by SemanticAttrsScope or null

/** Is called when the selection changed. */
var <TSemantic : DropdownElement> SemanticAttrsScope<TSemantic, *>.onChange: ((String) -> Unit)? by SemanticAttrsScope or null

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

/**
 * Creates a [SemanticUI inline dropdown](https://semantic-ui.com/modules/dropdown.html#inline)
 * using the specified [key] to determine if the visual representation needs to be re-created.
 */
@Composable
fun InlineDropdown(
    key: Any?,
    attrs: SemanticAttrBuilder<DropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<DropdownElement, HTMLDivElement>? = null,
) {
    val scope = SemanticAttrsScope.of<DropdownElement, HTMLDivElement>(AttrsScopeBuilder()).apply { attrs?.invoke(this) }
    SemanticDivElement<DropdownElement>({
        classes("ui", "inline")
        attrs?.invoke(this)
        classes("dropdown")
    }) {
        content?.invoke(this)
        DisposableEffect(key) {
            jQuery(scopeElement).dropdown(
                "debug" to scope.debug,
                "message" to json(*scope.message.toList().toTypedArray()),
                "placeholder" to scope.placeholder,
                "onChange" to fun(value: String) {
                    if (scope.debug == true) console.log("selection changed by dropdown to $value")
                    scope.onChange?.invoke(value)
                },
            )
            onDispose { }
        }
    }
}

interface MultipleDropdownElement : DropdownElement

/** Whether to use labels to show selected values. */
var <TSemantic : MultipleDropdownElement> SemanticAttrsScope<TSemantic, *>.useLabels: Boolean? by SemanticAttrsScope or null

@Stable
interface MultipleDropdownState {
    val availableValues: List<String>
    var selectedValues: List<String>

    var selectedValuesString: String
        get() = selectedValues.joinToString(",")
        set(value) {
            selectedValues = value.split(",").filter { it.isNotEmpty() }
        }

    val noValues: Boolean get() = selectedValues.isEmpty()
    var allValues: Boolean
}

class MultipleDropdownStateImpl(
    availableValues: List<String> = emptyList(),
    selectedValues: List<String> = emptyList(),
) : MultipleDropdownState {
    override val availableValues by mutableStateOf(availableValues)

    private var _selectedValues by mutableStateOf(selectedValues)
    override var selectedValues: List<String>
        get() = if (_allValues) availableValues else _selectedValues
        set(value) {
            _selectedValues = value
            if (!availableValues.all { _selectedValues.contains(it) }) {
                _allValues = false
            }
        }

    private var _allValues by mutableStateOf(false)
    override var allValues: Boolean
        get() = _allValues || availableValues.all { _selectedValues.contains(it) }
        set(value) {
            _allValues = value && !availableValues.all { _selectedValues.contains(it) }
        }
}

@Composable
fun rememberMultipleDropdownState(
    vararg availableValues: String = emptyArray(),
    select: (String) -> Boolean = { false },
): MultipleDropdownState {
    val selectedValues = availableValues.filter(select)
    return remember(selectedValues, availableValues) {
        MultipleDropdownStateImpl(
            availableValues = availableValues.toList(),
            selectedValues = selectedValues,
        )
    }
}

/**
 * Creates a [SemanticUI inline multiple dropdown](https://semantic-ui.com/modules/dropdown.html#inline)
 * using the specified [state].
 */
@Composable
fun InlineMultipleDropdown(
    state: MultipleDropdownState = rememberMultipleDropdownState(),
    attrs: SemanticAttrBuilder<MultipleDropdownElement, HTMLDivElement>? = null,
    content: SemanticBuilder<MultipleDropdownElement, HTMLDivElement>? = null,
) {
    val scope = SemanticAttrsScope.of<MultipleDropdownElement, HTMLDivElement>(AttrsScopeBuilder()).apply { attrs?.invoke(this) }
    SemanticDivElement<MultipleDropdownElement>({
        classes("ui", "inline")
        attrs?.invoke(this)
        classes("multiple", "dropdown")
        style { paddingRight(.35714286.em) }
    }) {
        content?.invoke(this)
        DisposableEffect(state) {
            jQuery(scopeElement).dropdown(
                "debug" to scope.debug,
                "message" to json(*scope.message.toList().toTypedArray()),
                "placeholder" to scope.placeholder,
                "useLabels" to scope.useLabels,
                "onChange" to fun(value: String) {
                    if (scopeElement.data("muted") == null) {
                        if (scope.debug == true) console.log("selection changed by dropdown to $value")
                        state.selectedValuesString = value
                        scope.onChange?.invoke(value)
                    }
                },
            )
            onDispose { }
        }
        DisposableEffect(state.selectedValues) {
            jQuery(scopeElement)
                .attr("data-muted", true)
                .dropdown("set exactly", state.selectedValues.toJsonArray())
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
