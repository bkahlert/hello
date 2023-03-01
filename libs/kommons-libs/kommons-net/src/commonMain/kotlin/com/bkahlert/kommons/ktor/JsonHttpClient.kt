package com.bkahlert.kommons.ktor

import com.bkahlert.kommons.json.LenientJson
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.JsonNull

public fun JsonHttpClient(
    logLevel: LogLevel = LogLevel.INFO,
    logger: (HttpClientConfig<HttpClientEngineConfig>) -> Logger = ::HttpClientLogger,
    config: (HttpClientConfig<HttpClientEngineConfig>.() -> Unit)? = null,
): HttpClient = HttpClient(JsonHttpClientEngineFactory) {
    install(Logging) {
        level = logLevel
        this.logger = logger(this@HttpClient)
    }
    install(ContentNegotiation) { json(LenientJson) }
    install(NoContentAs) { noContentResponse = JsonNull }
    expectSuccess = true
    config?.invoke(this)
}

internal expect val JsonHttpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
