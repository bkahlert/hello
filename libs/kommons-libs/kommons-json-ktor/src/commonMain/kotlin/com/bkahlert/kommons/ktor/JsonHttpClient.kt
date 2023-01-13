package com.bkahlert.kommons.ktor

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.HttpResponseContainer
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlin.reflect.KClass

public fun JsonHttpClient(
    logLevel: LogLevel = LogLevel.HEADERS,
    config: (HttpClientConfig<HttpClientEngineConfig>.() -> Unit)? = null,
): HttpClient = HttpClient(JsonHttpClientEngineFactory) {
    install(Logging) { level = logLevel }
    install(ContentNegotiation) { json(LenientJson) }
    install("TreatNoContentAsJsonNull") {
        responsePipeline.intercept(HttpResponsePipeline.Transform) { (info, body) ->
            if (context.response.status != HttpStatusCode.NoContent) return@intercept
            if (body !is ByteReadChannel) return@intercept
            if (info.type == ByteReadChannel::class) return@intercept
            if (info.kotlinType?.classifier?.let { it as? KClass<*> }?.isInstance(JsonNull) != true) return@intercept
            proceedWith(HttpResponseContainer(info, JsonNull))
        }
    }
    expectSuccess = true
    config?.invoke(this)
}

internal expect val JsonHttpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>

private val LenientJson = Json {
    isLenient = true
    ignoreUnknownKeys = true
    explicitNulls = false
}
