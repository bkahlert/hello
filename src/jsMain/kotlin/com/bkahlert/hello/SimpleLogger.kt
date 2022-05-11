package com.bkahlert.hello

import com.bkahlert.kommons.js.groupCatching
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.js.trace
import com.bkahlert.kommons.toSimpleClassName
import io.ktor.util.logging.Logger
import io.ktor.client.plugins.logging.Logger as PluginLogger

/**
 * Universal logger that logs using [console] using inline
 * functions to allow the actual use-site to be displayed in the browser.
 */
@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
class SimpleLogger(
    /** The name of this logger. */
    val name: String,
) : PluginLogger, Logger {

    /** Logs an error with the specified [message]. */
    override inline fun error(message: String) {
        console.error(message)
    }

    /** Logs an error with the specified [message] and [cause]. */
    override inline fun error(message: String, cause: Throwable) {
        console.error("$message, cause: $cause")
    }

    /** Logs a warning with the specified [message]. */
    override inline fun warn(message: String) {
        console.warn(message)
    }

    /** Logs a warning with the specified [message] and [cause]. */
    override inline fun warn(message: String, cause: Throwable) {
        console.warn("$message, cause: $cause")
    }

    /** Logs the specified info [message]. */
    override inline fun info(message: String) {
        console.info(message)
    }

    /** Logs the specified info [message] and [cause]. */
    override inline fun info(message: String, cause: Throwable) {
        console.info("$message, cause: $cause")
    }

    /** Logs the specified debug [message]. */
    override inline fun debug(message: String) {
        console.log(message)
    }

    /** Logs the specified debug [message] and [cause]. */
    override inline fun debug(message: String, cause: Throwable) {
        console.log("$message, cause: $cause")
    }

    /** Logs the specified trace [message]. */
    override inline fun trace(message: String) {
        console.trace(message)
    }

    /** Logs the specified trace [message] and [cause]. */
    override inline fun trace(message: String, cause: Throwable) {
        console.trace("$message, cause: $cause")
    }

    /** Logs the specified [message]. */
    override inline fun log(message: String) {
        console.log("LOG[${name}]: $message")
    }

    companion object {
        /** Creates a [SimpleLogger] for `this` object's [JsClass]. */
        fun Any.simpleLogger(): SimpleLogger =
            SimpleLogger(toSimpleClassName())
    }
}

inline fun <reified R> SimpleLogger.grouping(label: String? = null, collapsed: Boolean = true, crossinline block: () -> R): R =
    console.grouping(label, collapsed, block)

suspend inline fun <reified R> SimpleLogger.groupCatching(label: String? = null, collapsed: Boolean = true, crossinline block: suspend () -> R): Result<R> =
    console.groupCatching(label, collapsed) { block() }
