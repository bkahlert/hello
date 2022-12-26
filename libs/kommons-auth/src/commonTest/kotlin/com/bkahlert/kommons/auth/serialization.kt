package com.bkahlert.kommons.auth

import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

private val builderAction: JsonBuilder.() -> Unit = {
    isLenient = true
    ignoreUnknownKeys = true
    explicitNulls = false
}

internal val JsonSerializer: Json by lazy {
    Json { builderAction(); prettyPrint = false }
}

internal val PrettyPrintingJsonSerializer: Json by lazy {
    Json { builderAction(); prettyPrint = true }
}

internal inline fun <reified T> T.serialize(pretty: Boolean = false): String =
    (if (pretty) PrettyPrintingJsonSerializer else JsonSerializer).encodeToString(this)

internal inline fun <reified T> String.deserialize(): T =
    JsonSerializer.decodeFromString(trimIndent())

internal fun <T> T.serialize(serializer: KSerializer<T>, pretty: Boolean = false): String =
    (if (pretty) PrettyPrintingJsonSerializer else JsonSerializer).encodeToString(serializer, this)

internal fun <T> String.deserialize(serializer: KSerializer<T>): T =
    JsonSerializer.decodeFromString(serializer, trimIndent())
