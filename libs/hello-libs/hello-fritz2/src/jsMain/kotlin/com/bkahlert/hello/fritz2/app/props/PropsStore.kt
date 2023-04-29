@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.app.props

import com.bkahlert.hello.fritz2.RootSyncStore
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.app.props.StoragePropsDataSource.Companion.InMemoryPropsDataSource
import com.bkahlert.kommons.dom.readText
import com.bkahlert.kommons.json.LenientAndPrettyJson
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.Handler
import dev.fritz2.core.Lens
import dev.fritz2.core.Store
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
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

    public val import: EmittingHandler<Pair<File, List<String>>, Throwable> = handleAndEmit { current, (file, knownKeys) ->
        LenientAndPrettyJson.runCatching {
            val imported = LenientAndPrettyJson.decodeFromString<Map<String, JsonElement>>(file.readText())
            require(imported.isEmpty() || imported.keys.any { it in knownKeys }) { "No valid settings found. Expected at least one of $knownKeys" }
            imported
        }.getOrElse {
            logger.error("Failed to import ${file.name}", it)
            emit(it)
            current
        }
    }

    public fun <X> map(
        id: String,
        defaultValue: X,
        serializer: KSerializer<X>,
        jsonFormat: Json = LenientAndPrettyJson,
    ): SyncStore<X> = object : SyncStore<X> by map(object : Lens<Map<String, JsonElement>, X> {
        override val id: String = id
        override fun get(parent: Map<String, JsonElement>): X = parent[id]?.let { jsonFormat.decodeFromJsonElement(serializer, it) } ?: defaultValue
        override fun set(parent: Map<String, JsonElement>, value: X): Map<String, JsonElement> =
            parent + (id to jsonFormat.encodeToJsonElement(serializer, value))
    }) {
        override fun equals(other: Any?): Boolean = other is Store<*> && other.current == current
        override fun hashCode(): Int = current.hashCode()
    }

    override fun toString(): String = "PropsStore(props=$current)"
}

public interface PropStoreFactory<D> {
    public val DEFAULT_KEY: String
    public val DEFAULT_VALUE: D
    public operator fun invoke(
        propsStore: PropsStore,
        defaultValue: D = DEFAULT_VALUE,
        id: String = DEFAULT_KEY,
    ): SyncStore<D>
}
