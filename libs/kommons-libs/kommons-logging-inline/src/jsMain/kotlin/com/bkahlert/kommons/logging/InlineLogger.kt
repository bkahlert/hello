package com.bkahlert.kommons.logging

import kotlin.js.Console

/**
 * Browser optimized logger that that is completely implemented with inline functions.
 * This lets browsers correctly display the use-site.
 */
@Suppress("NOTHING_TO_INLINE")
public class InlineLogger(
    /** The name of this logger. */
    public val name: String,
) {

    /** Logs an error with the specified [message]. */
    public inline fun error(message: String) {
        console.error(name, message)
    }

    /** Logs an error with the specified [message] and [cause]. */
    public inline fun error(message: String, cause: Throwable) {
        console.error(name, "$message, cause: $cause")
    }

    /** Logs a warning with the specified [message]. */
    public inline fun warn(message: String) {
        console.warn(name, message)
    }

    /** Logs a warning with the specified [message] and [cause]. */
    public inline fun warn(message: String, cause: Throwable) {
        console.warn(name, "$message, cause: $cause")
    }

    /** Logs the specified info [message]. */
    public inline fun info(message: String) {
        console.info(name, message)
    }

    /** Logs the specified info [message] and [cause]. */
    public inline fun info(message: String, cause: Throwable) {
        console.info(name, "$message, cause: $cause")
    }

    /** Logs the specified debug [message]. */
    public inline fun debug(message: String) {
        console.debug(name, message)
    }

    /** Logs the specified debug [message] and [cause]. */
    public inline fun debug(message: String, cause: Throwable) {
        console.debug(name, "$message, cause: $cause")
    }

    /** Logs the specified trace [message]. */
    public inline fun trace(message: String) {
        console.trace(name, message)
    }

    /** Logs the specified trace [message] and [cause]. */
    public inline fun trace(message: String, cause: Throwable) {
        console.trace(name, "$message, cause: $cause")
    }
}

/**
 * Outputs a message to this [Console] at the "debug" log level.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/debug">console.debug()</a>
 */
public inline fun Console.debug(vararg objects: Any?) {
    asDynamic().debug.apply(null, objects)
}

/**
 * Outputs a stack trace to this [Console].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/trace">console.trace()</a>
 */
public inline fun Console.trace(vararg objects: Any?) {
    asDynamic().trace.apply(null, objects)
}
