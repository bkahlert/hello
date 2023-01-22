package com.bkahlert.hello.client

import com.bkahlert.kommons.json.LenientAndPrettyJson
import com.bkahlert.kommons.ktor.JsonHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * A map of environment variables and their values.
 */
public class Environment(
    private val map: Map<String, String?>,
) : Map<String, String?> by map {

    override fun toString(): String = buildString {
        append("Environment ")
        append(LenientAndPrettyJson.encodeToString(map))
    }

    public companion object {

        /** An empty [Environment]. */
        public val EMPTY: Environment = Environment(emptyMap())

        /**
         * Loads an [Environment]
         * using the specified [uri], and
         * the specified [httpClient].
         */
        public suspend fun load(
            uri: String = "./environment.json",
            httpClient: HttpClient = JsonHttpClient(),
        ): Environment = Environment(httpClient.get(uri) { expectSuccess = true }.body<JsonObject>().mapValues {
            when (val value = it.value) {
                is JsonPrimitive -> value.content
                else -> value.toString()
            }
        })

        /**
         * Loads an [Environment]
         * using the specified [uri], and
         * the specified [httpClient].
         */
        public suspend fun loadOrEmpty(
            uri: String = "./environment.json",
            httpClient: HttpClient = JsonHttpClient(),
        ): Environment = kotlin.runCatching { load(uri, httpClient) }.getOrDefault(EMPTY)
    }
}
