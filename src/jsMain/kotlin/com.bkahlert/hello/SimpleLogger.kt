package com.bkahlert.hello

import com.bkahlert.kommons.toSimpleClassName
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
import io.ktor.client.plugins.logging.Logger as PluginLogger

class SimpleLogger(private val name: String) : PluginLogger, Logger by KtorSimpleLogger(name) {

    fun log(message: CharSequence): Unit = log(message.toString())

    override fun log(message: String) {
        info("[$name] $message")
    }

    companion object {
        private const val CLASS_PREFIX = "class "
        fun Any.simpleLogger(): SimpleLogger =
            SimpleLogger(toSimpleClassName().removePrefix(CLASS_PREFIX))
    }
}
