package com.bkahlert.kommons.ktor

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.statement.HttpResponseContainer
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlin.reflect.KClass

public fun JsonHttpClient(config: (HttpClientConfig<HttpClientEngineConfig>.() -> Unit)? = null): HttpClient = HttpClient(JsonHttpClientEngineFactory) {
    install(Logging)
    install(ContentNegotiation) { json(Json { isLenient = true; ignoreUnknownKeys = true; explicitNulls = false }) }
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

public fun HttpClientConfig<HttpClientEngineConfig>.installTokenAuth(token: String) {
    install("Token-Authorization") {
        plugin(HttpSend).intercept { context ->
            context.headers[HttpHeaders.Authorization] = token
            execute(context)
        }
    }
}

internal expect val JsonHttpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
