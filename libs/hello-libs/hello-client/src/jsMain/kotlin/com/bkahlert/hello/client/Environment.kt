package com.bkahlert.hello.client

import com.bkahlert.kommons.ktor.JsonHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

public class Environment(map: Map<String, String?>) : Map<String, String?> by map {
    public companion object {
        public suspend fun load(
            url: String = "./environment.json",
            httpClient: HttpClient = JsonHttpClient(),
        ): Environment = runCatching {
            Environment(httpClient.get(url) { expectSuccess = true }.body<JsonObject>().mapValues {
                when (val value = it.value) {
                    is JsonPrimitive -> value.content
                    else -> value.toString()
                }
            })
        }.getOrElse {
            console.error("Error loading $url", it.message)
            Environment(emptyMap())
        }
    }
}
