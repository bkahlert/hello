package com.bkahlert.semanticui.module

import com.bkahlert.semanticui.core.jQuery
import kotlin.js.Json
import kotlin.js.json

private fun jQuery.popup(options: Json): jQuery =
    asDynamic().popup(options).unsafeCast<jQuery>()

public fun jQuery.popup(behavior: String, vararg args: Any?): jQuery =
    asDynamic().popup.apply(this, arrayOf(behavior, *args)).unsafeCast<jQuery>()

/**
 * An interface to interact with a [SemanticUI dropdown](https://semantic-ui.com/modules/dropdown.html)
 * using the specified [options].
 *
 * @see <a href="https://semantic-ui.com/modules/popup.html#initializing-a-popup">Initializing</a>
 */
public fun jQuery.popup(vararg options: Pair<String, Any?>): jQuery = popup(json(*options))
