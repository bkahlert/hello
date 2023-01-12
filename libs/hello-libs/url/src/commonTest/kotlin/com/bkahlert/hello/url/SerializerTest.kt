package com.bkahlert.hello.url

import io.kotest.matchers.shouldBe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test

abstract class SerializerTest<T>(
    val serializer: KSerializer<T>,
    val mappings: List<Pair<String, T>>,
) {
    constructor(
        serializer: KSerializer<T>,
        vararg mappings: Pair<String, T>,
    ) : this(serializer, mappings.toList())

    @Test
    fun deserialize() {
        mappings.forEach { (serialized, deserialized) ->
            Json.Default.decodeFromString(serializer, serialized) shouldBe deserialized
        }
    }

    @Test
    fun serialize() {
        mappings.forEach { (serialized, deserialized) ->
            Json.Default.encodeToString(serializer, deserialized) shouldBe serialized
        }
    }
}