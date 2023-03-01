package com.bkahlert.kommons.ktor

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.logging.Logger

public expect class HttpClientLogger() : Logger {
    public constructor(name: String)
    public constructor(config: HttpClientConfig<HttpClientEngineConfig>)
}
