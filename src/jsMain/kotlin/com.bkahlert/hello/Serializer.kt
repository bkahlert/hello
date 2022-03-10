package com.bkahlert.hello

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
