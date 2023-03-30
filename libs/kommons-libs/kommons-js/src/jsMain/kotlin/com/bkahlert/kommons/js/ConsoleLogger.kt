package com.bkahlert.kommons.js

import kotlin.reflect.KCallable
import kotlin.reflect.KProperty

/**
 * Browser optimized logger that is completely implemented with inline functions.
 * This lets browsers correctly display the use-site.
 */
@Suppress("NOTHING_TO_INLINE")
public class ConsoleLogger(
    /** The name of this logger. */
    public val name: String,
) {

    /** Logs an error with the specified [args]. */
    public inline fun error(vararg args: Any?) {
        console.error("%c$name", NAME_STYLES, *args)
    }

    /** Logs a warning with the specified [args]. */
    public inline fun warn(vararg args: Any?) {
        console.warn("%c$name", NAME_STYLES, *args)
    }

    /** Logs the specified info [args]. */
    public inline fun info(vararg args: Any?) {
        console.info("%c$name", NAME_STYLES, *args)
    }

    /** Logs the specified debug [args]. */
    public inline fun debug(vararg args: Any?) {
        console.debug("%c$name", DEBUG_NAME_STYLES, *args)
    }

    /** Logs the specified trace [args]. */
    public inline fun trace(vararg args: Any?) {
        console.debug("%c$name", TRACE_NAME_STYLES, *args) // console.trace has a different meaning
    }

    public companion object {
        private const val HELLO_BLUE_FILL: String = "#29aae2"
        private const val BASE: String = "" +
            "display: inline-block;" +
            "border-radius: 4px;" +
            "padding: 0.1em;" +
            "margin-right: 0.15em;" +
            "text-shadow: 0 0 0.5px #ffffff99;" +
            ""
        public const val NAME_STYLES: String = BASE +
            "background-color: $HELLO_BLUE_FILL;" +
            "color: black;" +
            ""
        public const val DEBUG_NAME_STYLES: String = BASE +
            "background-color: ${HELLO_BLUE_FILL}99;" +
            ""
        public const val TRACE_NAME_STYLES: String = BASE +
            "border: 1px solid $HELLO_BLUE_FILL;" +
            ""
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

// @formatter:off
///** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, R> ConsoleLogger.grouping(operation: KCallable<P1, R>, collapsed: Boolean = DEFAULT_COLLAPSED, block: () -> R): R = grouping(type = P1::class, operation=operation.name, args = emptyArray(), collapsed = collapsed, block=block)
///** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, R> ConsoleLogger.grouping(operation: KCallable<P1, P2, R>, p2: P2, collapsed: Boolean = DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2), collapsed=collapsed, block=block)
///** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, R> ConsoleLogger.grouping(operation: KCallable<P1, P2, P3, R>, p2: P2, p3: P3, collapsed: Boolean = DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3), collapsed=collapsed, block=block)
///** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, P4, R> ConsoleLogger.grouping(operation: KCallable<P1, P2, P3, P4, R>, p2: P2, p3: P3, p4: P4, collapsed: Boolean = DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3, p4), collapsed=collapsed, block=block)
///** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, P4, P5, R> ConsoleLogger.grouping(operation: KCallable<P1, P2, P3, P4, P5, R>, p2: P2, p3: P3, p4: P4, p5: P5, collapsed: Boolean = DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3, p4, p5), collapsed=collapsed, block=block)
///** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, R> ConsoleLogger.grouping(operation: KCallable<P1, R>, collapsed: Boolean = DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = emptyArray(), collapsed=collapsed, block=block)
///** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, R> ConsoleLogger.grouping(operation: KCallable<P1, P2, R>, p2: P2, collapsed: Boolean = DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2), collapsed=collapsed, block=block)
///** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, R> ConsoleLogger.grouping(operation: KCallable<P1, P2, P3, R>, p2: P2, p3: P3, collapsed: Boolean = DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3), collapsed=collapsed, block=block)
///** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, P4, R> ConsoleLogger.grouping(operation: KCallable<P1, P2, P3, P4, R>, p2: P2, p3: P3, p4: P4, collapsed: Boolean = DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3, p4), collapsed=collapsed, block=block)
///** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, P4, P5, R> ConsoleLogger.grouping(operation: KCallable<P1, P2, P3, P4, P5, R>, p2: P2, p3: P3, p4: P4, p5: P5, collapsed: Boolean = DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3, p4, p5), collapsed=collapsed, block=block)
// @formatter:on

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
