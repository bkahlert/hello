package com.bkahlert.kommons.logging

/**
 * Browser optimized logger that is completely implemented with inline functions.
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
        console.asDynamic().debug(name, message)
    }

    /** Logs the specified debug [message] and [cause]. */
    public inline fun debug(message: String, cause: Throwable) {
        console.asDynamic().debug(name, "$message, cause: $cause")
    }

    /** Logs the specified trace [message]. */
    public inline fun trace(message: String) {
        console.asDynamic().debug(name, message) // console.trace has a different meaning
    }

    /** Logs the specified trace [message] and [cause]. */
    public inline fun trace(message: String, cause: Throwable) {
        console.asDynamic().debug(name, "$message, cause: $cause")  // console.trace has a different meaning
    }
}
