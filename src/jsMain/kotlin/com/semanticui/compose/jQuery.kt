package com.semanticui.compose

import kotlin.js.Json
import kotlin.js.json

/**
 * An interface for the [jQuery library](https://jquery.com).
 */
external class jQuery(deep: Any?) {
    fun accordion(options: Json = definedExternally): jQuery
    fun dropdown(options: Json = definedExternally): jQuery
    fun popup(options: Json = definedExternally): jQuery
    fun popup(behavior: String, vararg args: Any? = definedExternally): jQuery
    fun transition(options: Json = definedExternally): jQuery
    fun transition(behavior: String, vararg args: Any? = definedExternally): jQuery
    fun modal(options: Json = definedExternally): jQuery
    fun modal(behavior: String, vararg args: Any? = definedExternally): jQuery
    fun progress(options: Json = definedExternally): jQuery
    fun progress(behavior: String, vararg args: Any? = definedExternally): jQuery
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
fun jQuery.popup(vararg options: Pair<String, Any?>) = dropdown(json(*options))


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
