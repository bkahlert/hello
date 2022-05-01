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
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.Variation
import com.semanticui.compose.dropdown
import com.semanticui.compose.jQuery
import com.semanticui.compose.toJsonArray
import com.semanticui.compose.toJsonArrayOrEmpty
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.paddingRight
import org.w3c.dom.HTMLDivElement
import kotlin.js.json

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
interface DropdownState {
    val availableValues: List<String>
    var selectedValue: String?
    val onSelect: (oldSelectedValue: String?, newSelectedValue: String?) -> Unit

    /** Whether to provide standard debug output to console. */
    val debug: Boolean

    /** Messages to appear on the dropdown. */
    val message: Map<String, String?>

    /** What to display if nothing is selected. */
    val placeholder: String?
}

class DropdownStateImpl(
    override val availableValues: List<String>,
    selectedValue: String?,
    override val onSelect: (oldSelectedValue: String?, newSelectedValue: String?) -> Unit,
    override val debug: Boolean,
    override val message: Map<String, String?>,
    override val placeholder: String?,
) : DropdownState {
    private var _selectedValue by mutableStateOf(selectedValue)
    override var selectedValue
        get() = _selectedValue
        set(value) {
            val oldSelectedValue = _selectedValue
            val newSelectedValue = value?.takeUnless { it.isEmpty() }
            if (oldSelectedValue != newSelectedValue) onSelect(oldSelectedValue, newSelectedValue)
            _selectedValue = newSelectedValue
        }
}

@Composable
fun rememberDropdownState(
    vararg availableValues: String = emptyArray(),
    select: (String) -> Boolean = { false },
    onSelect: (oldSelectedValue: String?, newSelectedValue: String?) -> Unit = { old, new -> console.log("selection changed from $old to $new") },
    debug: Boolean = false,
    message: Map<String, String?> = emptyMap(),
    placeholder: String? = null,
): DropdownState {
    val selectedValue = availableValues.firstOrNull(select)
    return remember(selectedValue, availableValues, onSelect, debug, message, placeholder) {
        DropdownStateImpl(
            availableValues = availableValues.toList(),
            selectedValue = selectedValue,
            onSelect = onSelect,
            debug = debug,
            message = message,
            placeholder = placeholder,
        )
    }
}

/**
 * Creates a [SemanticUI inline dropdown](https://semantic-ui.com/modules/dropdown.html#inline)
 * using the specified [state] to determine if the visual representation needs to be re-created.
 */
@Composable
fun InlineDropdown(
    state: DropdownState = rememberDropdownState(),
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
                "debug" to state.debug,
                "message" to json(* state.message.toList().toTypedArray()),
                "placeholder" to state.placeholder,
                "onChange" to fun(value: String) {
                    if (scopeElement.data("muted") == null) {
                        if (state.debug) console.log("selection changed by dropdown to $value")
                        state.selectedValue = value.takeUnless { it.isEmpty() }
                    }
                },
            )
            onDispose { }
        }
        DisposableEffect(state.selectedValue) {
            jQuery(scopeElement)
                .attr("data-muted", true)
                .dropdown("set exactly", state.selectedValue.toJsonArrayOrEmpty())
                .attr("data-muted", null)
            onDispose { }
        }
    }
}

interface MultipleDropdownElement : DropdownElement

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

    val onSelect: (oldSelectedValues: List<String>, newSelectedValues: List<String>) -> Unit

    /** Whether to provide standard debug output to console. */
    val debug: Boolean

    /** Messages to appear on the dropdown. */
    val message: Map<String, String?>

    /** What to display if nothing is selected. */
    val placeholder: String?

    /** Whether to use labels to show selected values. */
    val useLabels: Boolean?
}

class MultipleDropdownStateImpl(
    override val availableValues: List<String> = emptyList(),
    selectedValues: List<String> = emptyList(),
    override val onSelect: (oldSelectedValues: List<String>, newSelectedValues: List<String>) -> Unit,
    override val debug: Boolean,
    override val message: Map<String, String?>,
    override val placeholder: String?,
    override val useLabels: Boolean?,
) : MultipleDropdownState {
    private var _selectedValues by mutableStateOf(selectedValues)
    override var selectedValues: List<String>
        get() = if (_allValues) availableValues else _selectedValues
        set(value) {
            val oldSelectedValues = _selectedValues
            if (oldSelectedValues != value) onSelect(oldSelectedValues, value)
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
    onSelect: (oldSelectedValues: List<String>, newSelectedValues: List<String>) -> Unit = { old, new -> console.log("selection changed from $old to $new") },
    debug: Boolean = false,
    message: Map<String, String?> = emptyMap(),
    placeholder: String? = null,
    useLabels: Boolean? = null,
): MultipleDropdownState {
    val selectedValues = availableValues.filter(select)
    return remember(selectedValues, availableValues) {
        MultipleDropdownStateImpl(
            availableValues = availableValues.toList(),
            selectedValues = selectedValues,
            onSelect = onSelect,
            debug = debug,
            message = message,
            placeholder = placeholder,
            useLabels = useLabels,
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
    SemanticDivElement<MultipleDropdownElement>({
        classes("ui", "inline")
        attrs?.invoke(this)
        classes("multiple", "dropdown")
        style { paddingRight(.35714286.em) }
    }) {
        content?.invoke(this)
        DisposableEffect(state) {
            jQuery(scopeElement).dropdown(
                "debug" to state.debug,
                "message" to json(*state.message.toList().toTypedArray()),
                "placeholder" to state.placeholder,
                "useLabels" to state.useLabels,
                "onChange" to fun(value: String) {
                    if (scopeElement.data("muted") == null) {
                        if (state.debug) console.log("selection changed by dropdown to $value")
                        state.selectedValuesString = value
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
