package com.bkahlert.semanticui.core

import kotlin.js.Json
import kotlin.js.json

/**
 * An interface for the [jQuery library](https://jquery.com).
 */
public external class jQuery(deep: Any?) {
    // built-in
    public fun attr(propertyName: String): String?
    public fun attr(propertyName: String, value: Any?): jQuery
    public fun children(selector: String = definedExternally): jQuery
    public fun click(): jQuery
    public fun closest(selector: String = definedExternally): jQuery
    public fun css(propertyName: String, value: Any?): jQuery
    public fun find(selector: String = definedExternally): jQuery
    public fun focus(): jQuery
    public fun siblings(selector: String = definedExternally): jQuery

    // Semantic UI
    public fun accordion(options: Json = definedExternally): jQuery
    public fun dimmer(options: Json = definedExternally): jQuery
    public fun dimmer(behavior: String, vararg args: Any? = definedExternally): jQuery
    public fun dropdown(options: Json = definedExternally): jQuery
    public fun dropdown(behavior: String, vararg args: Any? = definedExternally): jQuery
    public fun popup(options: Json = definedExternally): jQuery
    public fun popup(behavior: String, vararg args: Any? = definedExternally): jQuery
    public fun transition(options: Json = definedExternally): jQuery
    public fun transition(behavior: String, vararg args: Any? = definedExternally): jQuery
    public fun modal(options: Json = definedExternally): jQuery
    public fun modal(behavior: String, vararg args: Any? = definedExternally): jQuery
    public fun progress(options: Json = definedExternally): jQuery
    public fun progress(behavior: String, vararg args: Any? = definedExternally): jQuery
}

/** Convenience shortcut for [jQuery.attr] and [key] prefixed with `data-`. */
public fun jQuery.dataAttr(key: String): String? =
    attr("data-$key")

/** Convenience shortcut for [jQuery.attr] and [key] prefixed with `data-`. */
public fun jQuery.dataAttr(key: String, value: Any?): jQuery =
    attr("data-$key", value)

/**
 * An interface to interact with a [SemanticUI dimmer](https://semantic-ui.com/modules/dimmer.html)
 * using the specified [options].
 */
public fun jQuery.dimmer(vararg options: Pair<String, Any?>): jQuery = dimmer(json(*options))

/**
 * An interface to interact with a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html)
 * using the specified [options].
 *
 * @see <a href="https://semantic-ui.com/modules/dropdown.html#initializing-existing-html">Initializing</a>
 */
public fun jQuery.dropdown(vararg options: Pair<String, Any?>): jQuery = dropdown(json(*options))

/**
 * An interface to interact with a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html)
 * using the specified [options].
 *
 * @see <a href="https://semantic-ui.com/modules/popup.html#initializing-a-popup">Initializing</a>
 */
public fun jQuery.popup(vararg options: Pair<String, Any?>): jQuery = popup(json(*options))

/**
 * An interface to interact with a [SemanticUI modal](https://semantic-ui.com/modules/modal.html)
 * using the specified [options].
 */
public fun jQuery.modal(vararg options: Pair<String, Any?>): jQuery = modal(json(*options))

/**
 * An interface to interact with a [SemanticUI modal](https://semantic-ui.com/modules/modal.html)
 * using the specified [options].
 */
public fun jQuery.modal(options: Map<String, Any?>): jQuery = modal(json(*options.toList().toTypedArray()))

/**
 * An interface to interact with a [SemanticUI progress](https://semantic-ui.com/modules/progress.html)
 * using the specified [options].
 */
public fun jQuery.progress(vararg options: Pair<String, Any?>): jQuery = progress(json(*options))


/**
 * Helper function to convert an optional value to something that jQuery / Semantic UI
 * accepts as an array.
 */
public fun <T> T?.toJsonArrayOrEmpty(transform: (T) -> String = { it.toString() }): Array<String> =
    this?.let { arrayOf(transform(it)) } ?: emptyArray()

/**
 * Helper function to convert collections to something that jQuery / Semantic UI
 * accepts as an array.
 */
public fun <T> Iterable<T>.toJsonArray(transform: (T) -> String = { it.toString() }): Array<String> =
    map { transform(it) }.toTypedArray()
