package com.bkahlert.hello.fritz2.app.env

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
    private val map: Map<String, String>,
) : Map<String, String> by map {

    /**
     * Returns the value belonging to the first key matching the
     * provided [keyPredicate] or throws an [NoSuchElementException]
     * with the specified [label] otherwise.
     */
    public fun search(label: String, keyPredicate: (String) -> Boolean): String =
        firstNotNullOfOrNull { (key, value) ->
            value.takeIf { keyPredicate(key) }
        } ?: throw NoSuchElementException("Unable to find $label")

    /**
     * Returns the value belonging to the first key containing the
     * provided [keySubstring] or throws an [NoSuchElementException]
     * with the specified [label] otherwise.
     */
    public fun search(label: String, keySubstring: String, ignoreCase: Boolean = true): String =
        search(label) { it.contains(keySubstring, ignoreCase = ignoreCase) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Environment

        if (map != other.map) return false

        return true
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = buildString {
        append("Environment ")
        append(LenientAndPrettyJson.encodeToString(map))
    }

    public companion object {

        /** An empty [Environment]. */
        public val EMPTY: Environment = Environment(emptyMap())

        public fun of(vararg pairs: Pair<String, String>): Environment = Environment(pairs.toMap())

        public suspend fun load(
            uri: String = "environment.json",
            httpClient: HttpClient = JsonHttpClient(),
        ): Environment {
            val values = httpClient.get(uri) { expectSuccess = true }.body<JsonObject>().mapValues {
                when (val value = it.value) {
                    is JsonPrimitive -> value.content
                    else -> value.toString()
                }
            }
            return Environment(values)
        }
    }
}
