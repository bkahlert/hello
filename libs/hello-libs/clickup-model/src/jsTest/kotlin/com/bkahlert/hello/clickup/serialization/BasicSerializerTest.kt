package com.bkahlert.hello.clickup.serialization

import io.kotest.assertions.throwables.shouldNotThrow
import kotlinx.serialization.KSerializer
import kotlin.test.Test

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
            LenientJson.decodeFromString(serializer, json)
            shouldNotThrow<Throwable> {
                LenientJson.decodeFromString(serializer, json)
            }
        }
    }
}
