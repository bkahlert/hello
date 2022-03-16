package com.semanticui.compose

import kotlin.js.Json
import kotlin.js.json

/**
 * An interface for the [jQuery library](https://jquery.com).
 */
external class jQuery(deep: Any?) {
    fun dropdown(options: Json = definedExternally): jQuery
}

/**
 * An interface to create a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html)
 * using the specified [options].
 *
 * @see <a href="https://semantic-ui.com/modules/dropdown.html#initializing-existing-html">Initializing</a>
 */
fun jQuery.dropdown(vararg options: Pair<String, Any?>) = dropdown(json(*options))
