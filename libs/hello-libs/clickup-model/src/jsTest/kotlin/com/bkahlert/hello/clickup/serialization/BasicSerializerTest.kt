package com.bkahlert.hello.clickup.serialization

import com.bkahlert.kommons.json.Lenient
import io.kotest.assertions.throwables.shouldNotThrow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
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
            Json.Lenient.decodeFromString(serializer, json)
            shouldNotThrow<Throwable> {
                Json.Lenient.decodeFromString(serializer, json)
            }
        }
    }
}
