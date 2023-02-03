package com.bkahlert.kommons.auth

import com.bkahlert.kommons.json.LenientJson
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer


public inline fun <reified T> Map<String, JsonElement>.optional(key: String, deserializer: KSerializer<T> = serializer()): T? =
    get(key)?.let { LenientJson.decodeFromJsonElement(deserializer, it) }

public inline fun <reified T> Map<String, JsonElement>.required(key: String, deserializer: KSerializer<T> = serializer()): T =
    optional(key, deserializer) ?: error("Key $key missing; available keys: $keys")
