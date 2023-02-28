package com.bkahlert.semanticui.core

import androidx.compose.runtime.Composable
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.js.table
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticDivElement
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

/**
 * A semantic UI element of the form `<div class="$classes">$content</div>` that
 * can be used as a fallback for not yet implemented Semantic UI features. */
@Composable
public fun S(
    vararg classes: String?,
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
) {
    SemanticDivElement({
        attrs?.invoke(this)
        classes(*classes.filterNotNull().flatMap { it.split(' ') }.toTypedArray())
    }, content)
}

private val logger = ConsoleLogger("Semantic UI")

public fun updateDebugSettings(configure: (String, DebugSettings) -> Unit) {
    logger.grouping(::updateDebugSettings) {
        val jQueryFn = js("jQuery").fn
        val jQueryFnKeys = js("Object").keys(jQueryFn).unsafeCast<Array<String>>()
        val updated = jQueryFnKeys.mapNotNull { key ->
            val fn = jQueryFn[key]
            if (fn !== undefined) key to fn else null
        }.filterNot { (_, fn) ->
            fn.settings === undefined
        }.map { (name, fn) ->
            fn.settings.unsafeCast<DebugSettings>().also { configure(name, it) }
        }
        logger.table(
            updated,
            DebugSettings::debug,
            DebugSettings::performance,
            DebugSettings::verbose,
        ) { it.name }
    }
}

public external class DebugSettings {
    /** Name used in debug logs */
    public var name: String

    /** Provides standard debug output to console */
    public var debug: Boolean?

    /** Provides standard debug output to console */
    public var performance: Boolean?

    /** Provides ancillary debug output to console */
    public var verbose: Boolean?
}
