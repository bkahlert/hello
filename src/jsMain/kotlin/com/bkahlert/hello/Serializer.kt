package com.bkahlert.hello

import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val JsonSerializer: Json by lazy {
    Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}

inline fun <reified T> T.serialize(): String = JsonSerializer.encodeToString(this)
inline fun <reified T> String.deserialize(): T = JsonSerializer.decodeFromString(trimIndent())

fun <T> T.serialize(serializer: KSerializer<T>): String = JsonSerializer.encodeToString(serializer, this)
fun <T> String.deserialize(serializer: KSerializer<T>): T = JsonSerializer.decodeFromString(serializer, trimIndent())
