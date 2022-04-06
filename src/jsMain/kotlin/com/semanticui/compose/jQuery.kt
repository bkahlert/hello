package com.semanticui.compose

import kotlin.js.Json
import kotlin.js.json

/**
 * An interface for the [jQuery library](https://jquery.com).
 */
external class jQuery(deep: Any?) {
    fun accordion(options: Json = definedExternally): jQuery
    fun dimmer(behavior: String, vararg args: Any? = definedExternally): jQuery
    fun dropdown(options: Json = definedExternally): jQuery
    fun dropdown(behavior: String, vararg args: Any? = definedExternally): jQuery
    fun popup(options: Json = definedExternally): jQuery
    fun popup(behavior: String, vararg args: Any? = definedExternally): jQuery
    fun transition(options: Json = definedExternally): jQuery
    fun transition(behavior: String, vararg args: Any? = definedExternally): jQuery
    fun modal(options: Json = definedExternally): jQuery
    fun modal(behavior: String, vararg args: Any? = definedExternally): jQuery
    fun progress(options: Json = definedExternally): jQuery
    fun progress(behavior: String, vararg args: Any? = definedExternally): jQuery

    fun attr(propertyName: String): String?
    fun attr(propertyName: String, value: Any?): jQuery
    fun children(selector: String = definedExternally): jQuery
    fun find(selector: String = definedExternally): jQuery
    fun css(propertyName: String, value: Any?): jQuery
    fun focus(): jQuery
}

/**
 * An interface to interact with a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html)
 * using the specified [options].
 *
 * @see <a href="https://semantic-ui.com/modules/dropdown.html#initializing-existing-html">Initializing</a>
 */
fun jQuery.dropdown(vararg options: Pair<String, Any?>) = dropdown(json(*options))

/**
 * An interface to interact with a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html)
 * using the specified [options].
 *
 * @see <a href="https://semantic-ui.com/modules/popup.html#initializing-a-popup">Initializing</a>
 */
fun jQuery.popup(vararg options: Pair<String, Any?>) = popup(json(*options))

/**
 * An interface to interact with a [SemanticUI modal](https://semantic-ui.com/modules/modal.html)
 * using the specified [options].
 */
fun jQuery.modal(vararg options: Pair<String, Any?>) = modal(json(*options))

/**
 * An interface to interact with a [SemanticUI progress](https://semantic-ui.com/modules/progress.html)
 * using the specified [options].
 */
fun jQuery.progress(vararg options: Pair<String, Any?>) = progress(json(*options))

/**
 * Helper function to convert collections to something that jQuery / Semantic UI
 * accepts as an array.
 */
fun <T> Iterable<T>.toJsonArray(transform: (T) -> String = { it.toString() }): Array<String> =
    map { transform(it) }.toTypedArray()
