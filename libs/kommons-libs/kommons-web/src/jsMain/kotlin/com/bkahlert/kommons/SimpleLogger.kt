package com.bkahlert.kommons

import com.bkahlert.kommons.debug.console
import com.bkahlert.kommons.debug.groupCatching
import com.bkahlert.kommons.debug.grouping
import com.bkahlert.kommons.debug.renderType
import com.bkahlert.kommons.debug.toJson

/**
 * Universal logger that logs using [console] using inline
 * functions to allow the actual use-site to be displayed in the browser.
 */
@Suppress("NOTHING_TO_INLINE")
public class SimpleLogger(
    /** The name of this logger. */
    public val name: String,
) {

    /** Logs an error with the specified [message]. */
    public inline fun error(message: String) {
        console.error(message)
    }

    /** Logs an error with the specified [message] and [cause]. */
    public inline fun error(message: String, cause: Throwable) {
        console.error("$message, cause: $cause")
    }

    /** Logs a warning with the specified [message]. */
    public inline fun warn(message: String) {
        console.warn(message)
    }

    /** Logs a warning with the specified [message] and [cause]. */
    public inline fun warn(message: String, cause: Throwable) {
        console.warn("$message, cause: $cause")
    }

    /** Logs the specified info [message]. */
    public inline fun info(message: String) {
        console.info(message)
    }

    /** Logs the specified info [message] and [cause]. */
    public inline fun info(message: String, cause: Throwable) {
        console.info("$message, cause: $cause")
    }

    /** Logs the specified debug [message]. */
    public inline fun debug(message: String) {
        console.log(message)
    }

    /** Logs the specified debug [message] and [cause]. */
    public inline fun debug(message: String, cause: Throwable) {
        console.log("$message, cause: $cause")
    }

    /** Logs the specified trace [message]. */
    public inline fun trace(message: String) {
        console.trace(message)
    }

    /** Logs the specified trace [message] and [cause]. */
    public inline fun trace(message: String, cause: Throwable) {
        console.trace("$message, cause: $cause")
    }

    public companion object {
        /** Creates a [SimpleLogger] for `this` object's [JsClass]. */
        public fun Any.simpleLogger(): SimpleLogger =
            SimpleLogger(renderType())
    }
}

public inline fun <reified R> SimpleLogger.grouping(
    label: String? = null,
    collapsed: Boolean = true,
    render: (R) -> Any? = { it.toJson() },
    crossinline block: () -> R,
): R =
    console.grouping(label, collapsed, render, block)

public suspend inline fun <reified R> SimpleLogger.groupCatching(
    label: String? = null,
    collapsed: Boolean = true,
    render: (R) -> Any? = { it.toJson() },
    crossinline block: suspend () -> R,
): Result<R> =
    console.groupCatching(label, collapsed, render) { block() }
