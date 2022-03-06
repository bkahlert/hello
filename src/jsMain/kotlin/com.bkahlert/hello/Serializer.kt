package com.bkahlert.hello

import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val SerializerJson by lazy {
    Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
}

val Serializer by lazy {
    KotlinxSerializer(SerializerJson)
}

inline fun <reified T> T.serialize(): String = SerializerJson.encodeToString(this)
inline fun <reified T> String.deserialize(): T = SerializerJson.decodeFromString(trimIndent())
