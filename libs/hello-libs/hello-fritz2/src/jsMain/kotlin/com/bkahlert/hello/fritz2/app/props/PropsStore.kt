@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.app.props

import com.bkahlert.hello.fritz2.RootSyncStore
import com.bkahlert.hello.fritz2.SubSyncStore
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.app.props.StoragePropsDataSource.Companion.InMemoryPropsDataSource
import com.bkahlert.hello.fritz2.lensForElementOrDefault
import com.bkahlert.kommons.dom.readText
import com.bkahlert.kommons.json.LenientAndPrettyJson
import dev.fritz2.core.Handler
import dev.fritz2.core.Lens
import dev.fritz2.core.Store
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.w3c.files.File

public class PropsStore(
    initialData: Map<String, JsonElement> = emptyMap(),
    private val propsDataSource: PropsDataSource = InMemoryPropsDataSource(initialData),
) : RootSyncStore<Map<String, JsonElement>>(
    cached = initialData,
    load = { propsDataSource.get() },
    sync = { oldValue, newValue -> propsDataSource.set(newValue, cachedProps = oldValue) }
) {
    public val set: Handler<Pair<String, JsonElement>> = handle { current, prop ->
        current + prop
    }

    public val remove: Handler<String> = handle { current, id ->
        current.filterKeys { it != id }
    }

    public val import: Handler<File> = handle { current, file ->
        LenientAndPrettyJson
            .runCatching { decodeFromString<Map<String, JsonElement>>(file.readText()) }
            .getOrElse {
                logger.error("Failed to read props from file", it)
                current
            }
    }

    override fun toString(): String = "PropsStore(props=$current)"
}

public fun PropsStore.prop(
    id: String,
    default: JsonElement = JsonNull,
): SyncStore<JsonElement> = SubSyncStore(this, lensForElementOrDefault(id, { default }))

public inline fun <reified P, reified D : P> PropsStore.prop(
    id: String,
    crossinline default: () -> D = { throw NoSuchElementException("Element for $id not found and no default value specified") },
    jsonFormat: Json = LenientAndPrettyJson,
): SyncStore<D> = object : SyncStore<D> by map(object : Lens<Map<String, JsonElement>, D> {
    override val id: String = id
    override fun get(parent: Map<String, JsonElement>): D = parent[id]?.let { jsonFormat.decodeFromJsonElement(it) } ?: default()

    override fun set(parent: Map<String, JsonElement>, value: D): Map<String, JsonElement> = parent + (id to jsonFormat.encodeToJsonElement(value))
}) {
    override fun equals(other: Any?): Boolean {
        return other is Store<*> && other.current == current
    }

    override fun hashCode(): Int {
        return current.hashCode()
    }
}

public inline fun <reified P> PropsStore.propOrNull(id: String, jsonFormat: Json = LenientAndPrettyJson): Store<P?> =
    prop(id, { null }, jsonFormat)
