package com.bkahlert.hello.environment.domain

import com.bkahlert.kommons.json.LenientAndPrettyJson
import kotlinx.serialization.encodeToString

/**
 * A map of environment variables and their values.
 */
public class Environment(
    private val map: Map<String, String>,
) : Map<String, String> by map {

    public constructor(vararg pairs: Pair<String, String>) : this(pairs.toMap())

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
    }
}
