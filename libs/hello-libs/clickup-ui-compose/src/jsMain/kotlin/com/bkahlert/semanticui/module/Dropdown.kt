package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.semanticui.collection.MenuElement
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.module.SemanticUI.jQuery
import js.core.jso
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.paddingRight
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

public interface DropdownElement : SemanticElement<HTMLDivElement>

public external interface SemanticDropdown : SemanticModule {
    public fun dropdown(behavior: String, vararg args: Any?): dynamic
    public fun attr(key: String): String?
    public fun attr(key: String, value: Any?): SemanticDropdown
}

public fun SemanticDropdown.dataAttr(key: String): String? = attr("data-$key")
public fun SemanticDropdown.dataAttr(key: String, value: Any?): SemanticDropdown = attr("data-$key", value)

public fun Element.dropdown(settings: SemanticDropdownSettings): SemanticDropdown = SemanticUI.create(this, "dropdown", settings)

@Suppress("LocalVariableName")
public external interface SemanticDropdownSettings : SemanticModuleSettings {

    // Frequently used settings
    /**
     * - `auto` converts option with "" (blank string) value to placeholder text
     * - `value` sets string value to placeholder text, leaves "" value
     * - `false` leaves "" value as a selectable option
     */
    public var placeholder: Any?

    // Multiple select settings

    /** Whether multiselect should use labels. Must be set to true when allowAdditions is true */
    public var useLabels: Boolean?

    // Additional settings

    /**
     * - `true` uses a fuzzy full text search
     * - `"exact"` forces the exact search to be matched somewhere in the string
     * - `false` only matches start of string
     */
    public var fullTextSearch: Any?

    // Callbacks

    /** Is called after a dropdown value changes. Receives the name and value of selection and the active menu element */
    public var onChange: ((value: Any?, text: String, `$choice`: JQuery<HTMLElement>) -> Unit)?

    /** Is called after a dropdown selection is added using a multiple select dropdown, only receives the added value */
    public var onAdd: ((addedValue: Any?, addedText: String, `$addedChoice`: JQuery<HTMLElement>) -> Unit)?

    /** Is called after a dropdown selection is removed using a multiple select dropdown, only receives the removed value */
    public var onRemove: ((removedValue: Any?, removedText: String, `$removedChoice`: JQuery<HTMLElement>) -> Unit)?

    /** Allows you to modify a label before it's added. Expects the jQ DOM element for a label to be returned. */
    public var onLabelCreate: ((value: Any?, text: String) -> Unit)?

    /** Called when a label is remove, return false; will prevent the label from being removed. */
    public var onLabelRemove: ((value: Any?) -> Unit)?


    /** Is called after a label is selected by a user */
    public var onLabelSelect: ((`$selectedLabels`: JQuery<HTMLElement>) -> Unit)?

    /** Is called after a dropdown is searched with no matching values */
    public var onNoResults: ((searchValue: String) -> Unit)?


    /** Is called before a dropdown is shown. If false is returned, dropdown will not be shown. */
    public var onShow: (() -> Boolean)?


    /** Is called before a dropdown is hidden. If false is returned, dropdown will not be hidden. */
    public var onHide: (() -> Boolean)?

    // DOM Settings
    public var message: MessageSettings?
}

public external interface MessageSettings {
    /**
     * Default: "Add <b>{term}</b>"
     */
    public var addResult: String?

    /**
     * Default: "{count} selected"
     */
    public var count: String?

    /**
     * Default: "Max {maxCount} selections"
     */
    public var maxSelections: String?

    /**
     * Default: "No results found."
     */
    public var noResults: String?
}

public fun MessageSettings(init: MessageSettings.() -> Unit): MessageSettings = jso(init)

// @formatter:off
/** Sets selected values to exactly specified values, removing current selection */
public inline fun SemanticDropdown.setExactly(vararg values:Any?): SemanticDropdown  =
    dropdown("set exactly", values).unsafeCast<SemanticDropdown>()
// @formatter:on

private fun JQuery<HTMLElement>.dropdown(behavior: String, vararg args: Any?): JQuery<HTMLElement> =
    asDynamic().dropdown.apply(this, arrayOf(behavior, *args)).unsafeCast<JQuery<HTMLElement>>()

/**
 * Creates a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html#/definition).
 */
@Composable
public fun Dropdown(
    attrs: SemanticModuleAttrBuilderContext<DropdownElement, SemanticDropdownSettings>? = null,
    content: SemanticModuleContentBuilder<DropdownElement, SemanticDropdownSettings>? = null,
) {
    SemanticModuleElement<DropdownElement, SemanticDropdownSettings>({
        classes("ui")
        attrs?.invoke(this)
        classes("dropdown")
    }) {
        content?.invoke(this)
        DisposableEffect(Unit) {
            scopeElement.dropdown(settings)
            onDispose { /* cleaned up by Dropdown module automatically */ } // TODO check
        }
    }
}

@Stable
public interface DropdownState<T> {
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

    public val settings: SemanticModuleSettingsBuilder<SemanticDropdownSettings>
}

public class DropdownStateImpl<T>(
    override val values: List<T>,
    selection: T?,
    override val onSelect: (old: T?, new: T?) -> Unit,
    override val serializer: (T) -> String,
    override val deserializer: (String) -> T,
    override val settings: SemanticModuleSettingsBuilder<SemanticDropdownSettings>,
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
    attrs: SemanticModuleAttrBuilderContext<DropdownElement, SemanticDropdownSettings>? = null,
    content: SemanticModuleContentBuilder<DropdownElement, SemanticDropdownSettings>? = null,
) {
    val logger = remember { ConsoleLogger("InlineDropdown") }

    SemanticModuleElement<DropdownElement, SemanticDropdownSettings>({
        classes("ui", "inline")
        attrs?.invoke(this)
        classes("dropdown")
        style { paddingRight(.35714286.em) }
    }) {
        content?.invoke(this)
        DisposableEffect(state) {
            scopeElement.dropdown(state.settings.build().apply {
                onChange = { value, text, `$choice` ->
                    if (scopeElement.getAttribute("data-$MUTED_ATTRIBUTE_NAME") == null) {
                        logger.debug("onChange(value: $value, text: $text, \$choice: $`$choice`")
                        if (debug == true) logger.debug("selection changed by dropdown to $value")
                        state.selectionString = value as String
                    }
                }
            })
            onDispose { /* cleaned up by Accordion module automatically */ } // TODO check
        }
        DisposableEffect(state.selection) {
            jQuery(scopeElement)
                .dataAttr(MUTED_ATTRIBUTE_NAME, true)
                .dropdown("set exactly", state.selection.toJsonArrayOrEmpty(state.serializer))
                .dataAttr(MUTED_ATTRIBUTE_NAME, null)
            onDispose { /* cleaned up by Accordion module automatically */ } // TODO check
        }
    }
}

public interface MultipleDropdownElement : DropdownElement

@Stable
public interface MultipleDropdownState<T> {
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
    public val settings: SemanticModuleSettingsBuilder<SemanticDropdownSettings>
}

public class MultipleDropdownStateImpl<T>(
    override val values: List<T> = emptyList(),
    selection: List<T> = emptyList(),
    override val onSelect: (old: List<T>, new: List<T>) -> Unit,
    override val serializer: (T) -> String,
    override val deserializer: (String) -> T,
    override val settings: SemanticModuleSettingsBuilder<SemanticDropdownSettings>,
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
    attrs: SemanticModuleAttrBuilderContext<MultipleDropdownElement, SemanticDropdownSettings>? = null,
    content: SemanticModuleContentBuilder<MultipleDropdownElement, SemanticDropdownSettings>? = null,
) {
    val logger = remember { ConsoleLogger("InlineMultipleDropdown") }

    SemanticModuleElement<MultipleDropdownElement, SemanticDropdownSettings>({
        classes("ui", "inline")
        attrs?.invoke(this)
        classes("multiple", "dropdown")
        style { paddingRight(.35714286.em) }
    }) {
        content?.invoke(this)
        DisposableEffect(state) {
            scopeElement.dropdown(state.settings.build().apply {
                onChange = { value, text, `$choice` ->
                    if (scopeElement.getAttribute("data-$MUTED_ATTRIBUTE_NAME") == null) {
                        logger.debug("onChange(value: $value, text: $text, \$choice: $`$choice`")
                        if (settings.debug == true) logger.debug("selection changed by dropdown to $value")
                        state.selectionString = value as String
                    }
                }
            })
            onDispose { /* cleaned up by Accordion module automatically */ } // TODO check
        }
        DisposableEffect(state.selection) {
            jQuery(scopeElement)
                .dataAttr(MUTED_ATTRIBUTE_NAME, true)
                .dropdown("set exactly", state.selection.toJsonArray(state.serializer))
                .dataAttr(MUTED_ATTRIBUTE_NAME, null)
            onDispose { /* cleaned up by Accordion module automatically */ } // TODO check
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
