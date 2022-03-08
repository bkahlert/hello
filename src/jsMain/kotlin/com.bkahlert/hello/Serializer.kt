package com.bkahlert.hello

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val SerializerJson by lazy {
    Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}

inline fun <reified T> T.serialize(): String = SerializerJson.encodeToString(this)
inline fun <reified T> String.deserialize(): T = SerializerJson.decodeFromString(trimIndent())
