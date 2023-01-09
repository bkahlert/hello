package com.bkahlert.kommons.serialization

import com.bkahlert.kommons.json.deserialize
import com.bkahlert.kommons.json.serialize
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import kotlinx.serialization.KSerializer
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
            serialized.deserialize(serializer) shouldBe deserialized
        }
    }

    @Test
    fun serialize() {
        mappings.forEach { (serialized, deserialized) ->
            deserialized.serialize(serializer, pretty = true) shouldBe serialized
        }
    }
}

abstract class BasicSerializerTest<T>(
    val serializer: KSerializer<T>,
    val jsons: List<String>,
) {
    constructor(
        serializer: KSerializer<T>,
        vararg jsons: String,
    ) : this(serializer, jsons.toList())

    @Test
    fun deserialize() {
        jsons.forEach { json ->
            json.deserialize(serializer)
            shouldNotThrow<Throwable> {
                json.deserialize(serializer)
            }
        }
    }
}
