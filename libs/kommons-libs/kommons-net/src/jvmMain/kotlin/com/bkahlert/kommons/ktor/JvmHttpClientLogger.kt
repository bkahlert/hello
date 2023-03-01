package com.bkahlert.kommons.ktor

import com.bkahlert.kommons.logging.SLF4J
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.logging.Logger

public actual class HttpClientLogger actual constructor(
    name: String,
) : Logger {
    private val logger: org.slf4j.Logger = SLF4J.getLogger(name)

    public actual constructor() : this("HttpClient")
    public actual constructor(config: HttpClientConfig<HttpClientEngineConfig>) : this("HttpClient")

    override fun log(message: String) {
        when (message.substringBefore(":")) {
            "TRACE" -> logger.trace(message.substringAfter(":"))
            "DEBUG" -> logger.debug(message.substringAfter(":"))
            "INFO" -> logger.info(message.substringAfter(":"))
            "WARN" -> logger.warn(message.substringAfter(":"))
            "WARNING" -> logger.warn(message.substringAfter(":"))
            "ERROR" -> logger.error(message.substringAfter(":"))
        }
    }
}
