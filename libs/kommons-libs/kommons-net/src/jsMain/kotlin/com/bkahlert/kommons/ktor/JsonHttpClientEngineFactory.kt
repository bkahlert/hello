package com.bkahlert.kommons.ktor

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

internal actual val JsonHttpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
    get() = Js
