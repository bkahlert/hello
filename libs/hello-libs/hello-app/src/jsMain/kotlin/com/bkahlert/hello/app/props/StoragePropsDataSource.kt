package com.bkahlert.hello.app.props

import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.json.LenientJson
import kotlinx.browser.localStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

public class StoragePropsDataSource(
    private val storage: Storage,
    private val jsonFormat: Json = LenientJson,
) : PropsDataSource {
    public constructor(storage: org.w3c.dom.Storage = localStorage) : this(storage.scoped("props"))

    private val logger by ConsoleLogging

    private fun serialize(value: JsonElement): String = jsonFormat.encodeToString(value)
    private fun deserialize(value: String): JsonElement = jsonFormat
        .runCatching { parseToJsonElement(value) }
        .onFailure { logger.error("Failed to read property. Resetting.", it) }
        .getOrNull() ?: JsonNull

    override suspend fun get(): Map<String, JsonElement> = storage.keys.associateWith {
        storage[it]?.let(::deserialize) ?: JsonNull
    }

    override suspend fun get(id: String): JsonElement? = get()[id]
    override suspend fun set(id: String, value: JsonElement): JsonElement {
        storage[id] = serialize(value)
        return value
    }

    override suspend fun remove(id: String): JsonElement? {
        val old = storage[id]
        storage[id] = null
        return old?.let(::deserialize)
    }

    public companion object {
        public fun InMemoryPropsDataSource(
            props: Map<String, JsonElement>,
        ): StoragePropsDataSource {
            val storage = InMemoryStorage()
            return StoragePropsDataSource(storage).apply {
                props.forEach { (key, value) -> storage[key] = serialize(value) }
            }
        }

        public fun InMemoryPropsDataSource(
            vararg props: Pair<String, JsonElement>,
        ): StoragePropsDataSource = InMemoryPropsDataSource(props.toMap())
    }
}
