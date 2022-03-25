package com.bkahlert.hello

import com.bkahlert.kommons.toSimpleClassName
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
import io.ktor.client.plugins.logging.Logger as PluginLogger

class SimpleLogger(private val name: String) : PluginLogger, Logger by KtorSimpleLogger(name) {

    override fun error(message: String) {
        console.error(message)
    }

    override fun error(message: String, cause: Throwable) {
        console.error("$message, cause: $cause")
    }

    override fun warn(message: String) {
        console.warn(message)
    }

    override fun warn(message: String, cause: Throwable) {
        console.warn("$message, cause: $cause")
    }

    override fun info(message: String) {
        console.info(message)
    }

    override fun info(message: String, cause: Throwable) {
        console.info("$message, cause: $cause")
    }

    override fun debug(message: String) {
        console.log("DEBUG: $message")
    }

    override fun debug(message: String, cause: Throwable) {
        console.log("DEBUG: $message, cause: $cause")
    }

    override fun trace(message: String) {
        console.log("TRACE: $message")
    }

    override fun trace(message: String, cause: Throwable) {
        console.log("TRACE: $message, cause: $cause")
    }

    override fun log(message: String) {
        console.log("LOG[$name]: $message")
    }

    companion object {
        private const val CLASS_PREFIX = "class "
        fun Any.simpleLogger(): SimpleLogger =
            SimpleLogger(toSimpleClassName().removePrefix(CLASS_PREFIX))
    }
}
