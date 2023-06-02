package com.bkahlert.hello.editor

import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.kommons.json.LenientAndPrettyJson
import dev.fritz2.core.Lens
import dev.fritz2.core.Store
import dev.fritz2.core.lensOf
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer

public fun lensForJson(
    json: Json = LenientAndPrettyJson,
    id: String = "json",
): Lens<JsonElement, String> = lensOf(id, { json.encodeToString(it) }, { _, v -> json.parseToJsonElement(v) })

public fun Store<JsonElement>.mapJson(
    json: Json = LenientAndPrettyJson,
    id: String = "json",
): Store<String> = map(lensForJson(json, id))


public fun <P> lensFromJson(
    serializer: KSerializer<P>,
    json: Json = LenientAndPrettyJson,
    id: String = "json",
): Lens<JsonElement, P> = lensOf(
    id = id,
    getter = { json.decodeFromJsonElement(serializer, it) },
    setter = { _, v -> json.encodeToJsonElement(serializer, v) })

public fun <P> Store<JsonElement>.mapFromJson(
    serializer: KSerializer<P>,
    json: Json = LenientAndPrettyJson,
    id: String = "json",
): Store<P> = map(lensFromJson(serializer, json, id))


public fun <P> SyncStore<JsonElement>.mapFromJson(
    json: Json = LenientAndPrettyJson,
    serializer: SerializationStrategy<P>,
    deserializer: DeserializationStrategy<P>,
    id: String = "json",
): SyncStore<P> = map(lensOf(id, { json.decodeFromJsonElement(deserializer, it) }, { _, v -> json.encodeToJsonElement(serializer, v) }))

public inline fun <reified P> SyncStore<JsonElement>.mapFromJson(
    json: Json = LenientAndPrettyJson,
    id: String = "json",
): SyncStore<P> = mapFromJson(json, serializer(), serializer(), id)
