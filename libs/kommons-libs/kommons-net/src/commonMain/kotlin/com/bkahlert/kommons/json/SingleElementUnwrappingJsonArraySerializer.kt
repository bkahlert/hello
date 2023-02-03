package com.bkahlert.kommons.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * [Json] serializer that represents lists with a single element
 * as the [JsonElement] itself (unwrapped), and otherwise as a [JsonArray].
 */
public class SingleElementUnwrappingJsonArraySerializer<T>(serializer: KSerializer<T>) : JsonTransformingSerializer<List<T>>(ListSerializer(serializer)) {
    override fun transformSerialize(element: JsonElement): JsonElement {
        return if (element is JsonArray && element.size == 1) element.first() else element
    }

    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element !is JsonArray) JsonArray(listOf(element)) else element
}
