package com.bkahlert.hello.user.props

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

data class SimpleSelector(
    val id: String,
    val path: List<String>,
) {

    fun resolve(
        jsonElement: JsonElement?,
        path: List<String> = this.path,
    ): JsonElement? {
        if (jsonElement == null) return null
        val pathElement = path.firstOrNull() ?: return jsonElement
        return when (jsonElement) {
            is JsonArray -> when (val index = pathElement.toIntOrNull()) {
                null -> null
                else -> resolve(jsonElement.getOrNull(index), path.drop(1))
            }

            is JsonObject -> resolve(jsonElement[pathElement], path.drop(1))
            is JsonPrimitive -> null
        }
    }

    companion object {
        fun String.parseSelector(): SimpleSelector {
            val parts = split('.')
            return SimpleSelector(parts.first(), parts.drop(1))
        }
    }
}
