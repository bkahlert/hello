package com.bkahlert.hello.fritz2

import com.bkahlert.kommons.json.LenientAndPrettyJson
import dev.fritz2.core.Lens
import dev.fritz2.core.Store
import dev.fritz2.core.lensOf
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer

public fun <D> Store<D>.load(block: suspend () -> D): Store<D> =
    apply { handle<Unit> { _, _ -> block() }.invoke() }


/* Null */

public fun <P, V> nullLens(lens: Lens<P, V>): Lens<P?, V?> =
    lensOf("${lens.id}?", { it?.let(lens::get) }, { p, v -> p?.let { v?.let { lens.set(p, v) } } })

public fun <D, X> Store<D?>.mapNull(lens: Lens<D, X>): Store<X?> = map(nullLens(lens))
public fun <D, X> SyncStore<D?>.mapNull(lens: Lens<D, X>): SyncStore<X?> = map(nullLens(lens))


/* Pair */

public fun <A, B> lensForPairFirst(): Lens<Pair<A, B>, A> = lensOf("first", Pair<A, B>::first) { p, v -> Pair(v, p.second) }
public fun <A, B> lensForPairSecond(): Lens<Pair<A, B>, B> = lensOf("first", Pair<A, B>::second) { p, v -> Pair(p.first, v) }

public fun <A, B> Store<Pair<A, B>>.mapFirst(): Store<A> = map(lensForPairFirst())
public fun <A, B> Store<Pair<A, B>>.mapSecond(): Store<B> = map(lensForPairSecond())
public fun <A, B> SyncStore<Pair<A, B>>.mapFirst(): SyncStore<A> = map(lensForPairFirst())
public fun <A, B> SyncStore<Pair<A, B>>.mapSecond(): SyncStore<B> = map(lensForPairSecond())


/* Entry */

private fun <K, V> Entry(key: K, value: V) = object : Map.Entry<K, V> {
    override val key: K = key
    override val value: V = value
}

public fun <K, V> lensForKey(): Lens<Map.Entry<K, V>, K> = lensOf("key", Map.Entry<K, V>::key) { p, v -> Entry(v, p.value) }
public fun <K, V> lensForValue(): Lens<Map.Entry<K, V>, V> = lensOf("value", Map.Entry<K, V>::value) { p, v -> Entry(p.key, v) }

public fun <K, V> Store<Map.Entry<K, V>>.mapKey(): Store<K> = map(lensForKey())
public fun <K, V> Store<Map.Entry<K, V>>.mapValue(): Store<V> = map(lensForValue())
public fun <K, V> SyncStore<Map.Entry<K, V>>.mapKey(): SyncStore<K> = map(lensForKey())
public fun <K, V> SyncStore<Map.Entry<K, V>>.mapValue(): SyncStore<V> = map(lensForValue())


/* Map */

public fun <K, V> lensForPairs(): Lens<Map<K, V>, List<Pair<K, V>>> = lensOf("pairs", { it.map { (k, v) -> k to v } }) { _, v -> v.toMap() }
public fun <K, V> lensForEntries(): Lens<Map<K, V>, List<Map.Entry<K, V>>> =
    lensOf("pairs", { it.entries.toList() }) { _, v -> v.associate { it.key to it.value } }

public fun <K, V> lensForElementOrNull(key: K): Lens<Map<K, V>, V?> = object : Lens<Map<K, V>, V?> {
    override val id: String = key.toString()
    override fun get(parent: Map<K, V>): V? = parent[key]
    override fun set(parent: Map<K, V>, value: V?): Map<K, V> = if (value != null) parent + (key to value) else parent
}

public fun <K, V> lensForElementOrDefault(key: K, default: () -> V): Lens<Map<K, V>, V> = object : Lens<Map<K, V>, V> {
    override val id: String = key.toString()
    override fun get(parent: Map<K, V>): V = parent[key] ?: default()
    override fun set(parent: Map<K, V>, value: V): Map<K, V> = parent + (key to value)
}

public fun <K, V> Store<Map<K, V>>.mapPairs(): Store<List<Pair<K, V>>> = map(lensForPairs())
public fun <K, V> Store<Map<K, V>>.mapEntries(): Store<List<Map.Entry<K, V>>> = map(lensForEntries())
public fun <K, V> Store<Map<K, V>>.mapElementOrNull(key: K): Store<V?> = map(lensForElementOrNull(key))
public fun <K, V> Store<Map<K, V>>.mapElementOrDefault(key: K, default: () -> V): Store<V> = map(lensForElementOrDefault(key, default))
public fun <K, V> SyncStore<Map<K, V>>.mapPairs(): SyncStore<List<Pair<K, V>>> = map(lensForPairs())
public fun <K, V> SyncStore<Map<K, V>>.mapEntries(): SyncStore<List<Map.Entry<K, V>>> = map(lensForEntries())
public fun <K, V> SyncStore<Map<K, V>>.mapElementOrNull(key: K): SyncStore<V?> = map(lensForElementOrNull(key))
public fun <K, V> SyncStore<Map<K, V>>.mapElementOrDefault(key: K, default: () -> V): SyncStore<V> = map(lensForElementOrDefault(key, default))


/* Serialization */

public fun <P> lensForSerialization(
    stringFormat: StringFormat,
    serializer: SerializationStrategy<P>,
    deserializer: DeserializationStrategy<P>,
    id: String = "serialization",
): Lens<P, String> = lensOf(id, { stringFormat.encodeToString(serializer, it) }, { _, v -> stringFormat.decodeFromString(deserializer, v) })

public inline fun <reified P> lensForSerialization(
    stringFormat: StringFormat,
    id: String = "serialization",
): Lens<P, String> = serializer<P>().let { lensForSerialization(stringFormat, it, it, id) }

public fun <P> Store<P>.mapSerialization(
    stringFormat: StringFormat,
    serializer: SerializationStrategy<P>,
    deserializer: DeserializationStrategy<P>,
    id: String = "serialization",
): Store<String> = map(lensForSerialization(stringFormat, serializer, deserializer, id))

public inline fun <reified P> Store<P>.mapSerialization(
    stringFormat: StringFormat,
    id: String = "serialization",
): Store<String> = serializer<P>().let { mapSerialization(stringFormat, it, it, id) }

public fun <P> SyncStore<P>.mapSerialization(
    stringFormat: StringFormat,
    serializer: SerializationStrategy<P>,
    deserializer: DeserializationStrategy<P>,
    id: String = "serialization",
): SyncStore<String> = map(lensForSerialization(stringFormat, serializer, deserializer, id))

public inline fun <reified P> SyncStore<P>.mapSerialization(
    stringFormat: StringFormat,
    id: String = "serialization",
): SyncStore<String> = serializer<P>().let { mapSerialization(stringFormat, it, it, id) }


/* JSON Serialization */

public fun lensForJson(
    json: Json = LenientAndPrettyJson,
    id: String = "json",
): Lens<JsonElement, String> = lensOf(id, { json.encodeToString(it) }, { _, v -> json.parseToJsonElement(v) })

public fun Store<JsonElement>.mapJson(
    json: Json = LenientAndPrettyJson,
    id: String = "json",
): Store<String> = map(lensForJson(json, id))

public fun <P> Store<JsonElement>.mapFromJson(
    json: Json = LenientAndPrettyJson,
    serializer: SerializationStrategy<P>,
    deserializer: DeserializationStrategy<P>,
    id: String = "json",
): Store<P> = map(lensOf(id, { json.decodeFromJsonElement(deserializer, it) }, { _, v -> json.encodeToJsonElement(serializer, v) }))

public inline fun <reified P> Store<JsonElement>.mapFromJson(
    json: Json = LenientAndPrettyJson,
    id: String = "json",
): Store<P> = mapFromJson(json, serializer(), serializer(), id)

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
