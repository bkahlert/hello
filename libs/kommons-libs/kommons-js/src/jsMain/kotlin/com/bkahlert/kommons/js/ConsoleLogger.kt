package com.bkahlert.kommons.js

import kotlinx.browser.localStorage
import kotlin.reflect.KCallable
import kotlin.reflect.KProperty

/**
 * Browser optimized logger that is completely implemented with inline functions.
 * This lets browsers correctly display the use-site.
 */
public class ConsoleLogger(
    /** The name of this logger. */
    public val name: String,
) {
    init {
        if (localStorage.getItem("debug") == null) {
            localStorage.setItem("debug", "hello:*")
        }
    }

    private val namespace = name.replace('.', ':')

    private inner class DebuggerDelegate(outputStream: dynamic) {
        val debug = com.bkahlert.kommons.js.debug(namespace)

        init {
            debug.log = outputStream
        }

        operator fun invoke(vararg args: Any?) {
            debug.apply(this@ConsoleLogger, args)
        }
    }

    private val consoleErrorUsingDebug = DebuggerDelegate(consoleErrorFn)
    private val consoleWarnUsingDebug = DebuggerDelegate(consoleWarnFn)
    private val consoleInfoUsingDebug = DebuggerDelegate(consoleInfo)
    private val consoleDebugUsingDebug = DebuggerDelegate(consoleDebug)

    /** Logs an error with the specified [args]. */
    public fun error(vararg args: Any?) {
        consoleErrorUsingDebug(*args)
    }

    /** Logs a warning with the specified [args]. */
    public fun warn(vararg args: Any?) {
        consoleWarnUsingDebug(*args)
    }

    /** Logs the specified info [args]. */
    public fun info(vararg args: Any?) {
        consoleInfoUsingDebug(*args)
    }

    /** Logs the specified debug [args]. */
    public fun debug(vararg args: Any?) {
        consoleDebugUsingDebug(*args)
    }

    /** Logs the specified trace [args]. */
    public fun trace(vararg args: Any?) {
        consoleDebugUsingDebug(*args) // console.trace has a different meaning
    }

    public companion object {
        private val consoleErrorFn: dynamic = js("console.error.bind(console)")
        private val consoleWarnFn: dynamic = js("console.warn.bind(console)")
        private val consoleInfo: dynamic = js("console.info.bind(console)")
        private val consoleDebug: dynamic = js("console.debug.bind(console)")
    }
}

/**
 * Runs the specified [block] wrapped by an optionally [collapsed] group
 * with the specified [operation].
 */
public inline fun <R> ConsoleLogger.grouping(
    operation: String?,
    vararg args: Any?,
    collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED,
    block: () -> R,
): R = console.grouping(buildString {
    append(name)
    if (operation != null) append(".$operation(")
    args.joinTo(this)
    if (operation != null) append(")")
}, collapsed, block)

/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */
public inline fun <R> ConsoleLogger.grouping(
    operation: KCallable<R>,
    vararg args: Any?,
    collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED,
    block: () -> R,
): R = grouping(
    operation = operation.name,
    args = args,
    collapsed = collapsed,
    block = block
)

/**
 * Displays the specified [data] as a table
 * optionally filtered to the specified [columns].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/table">console.table()</a>
 */
public fun <T> ConsoleLogger.table(
    data: Collection<T>,
    vararg columns: KProperty<*>,
    name: String? = null,
    label: (T) -> String = { it.toString() },
) {
    table(data.associateBy(label), *columns, name = name)
}

/**
 * Displays the specified [data] as a table
 * optionally filtered to the specified [columns].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/table">console.table()</a>
 */
public fun ConsoleLogger.table(
    data: Map<String, Any?>,
    vararg columns: KProperty<*>,
    name: String? = null,
) {
    val table: () -> Unit = {
        if (columns.isEmpty()) {
            console.table(json(data))
        } else {
            console.table(json(data), columns.map { it.name }.toTypedArray())
        }
    }

    if (name != null) console.grouping(name, block = table)
    else table()
}
