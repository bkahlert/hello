@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.app.props

import com.bkahlert.hello.app.props.StoragePropsDataSource.Companion.InMemoryPropsDataSource
import com.bkahlert.hello.fritz2.RootSyncStore
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.kommons.dom.readText
import com.bkahlert.kommons.json.LenientAndPrettyJson
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.Handler
import dev.fritz2.core.lensOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import org.w3c.files.File

public class PropsStore(
    initialData: Map<String, JsonElement> = emptyMap(),
    private val propsDataSource: PropsDataSource = InMemoryPropsDataSource(initialData),
) : RootSyncStore<Map<String, JsonElement>>(initialData) {

    override suspend fun load(): Map<String, JsonElement> = propsDataSource.get()

    override suspend fun sync(
        oldData: Map<String, JsonElement>,
        newData: Map<String, JsonElement>
    ): Map<String, JsonElement> = propsDataSource.set(newData, cachedProps = oldData)

    public val set: Handler<Pair<String, JsonElement>> = handle { current, prop ->
        current + prop
    }

    public val remove: Handler<String> = handle { current, id ->
        current.filterKeys { it != id }
    }

    public val import: EmittingHandler<File, Throwable> = handleAndEmit { current, file ->
        LenientAndPrettyJson.runCatching {
            val imported = LenientAndPrettyJson.decodeFromString<Map<String, JsonElement>>(file.readText())
            require(imported.isEmpty() || imported.keys.any { it in trackedKeys }) { "No valid settings found. Expected at least one of $trackedKeys" }
            imported
        }.getOrElse {
            logger.error("Failed to import ${file.name}", it)
            emit(it)
            current
        }
    }

    private val trackedKeys = mutableSetOf<String>()
    public fun <X> mapByKeyOrDefault(
        key: String,
        defaultValue: X,
        serializer: KSerializer<X>,
        jsonFormat: Json = LenientAndPrettyJson,
    ): SyncStore<X> = map(
        lensOf(
            id = key.also { trackedKeys += it },
            getter = { p -> p[key]?.let { jsonFormat.decodeFromJsonElement(serializer, it) } ?: defaultValue },
            setter = { p, v -> p + (key to jsonFormat.encodeToJsonElement(serializer, v)) },
        )
    )

    override fun toString(): String = "PropsStore(props=$current)"
}

public inline fun <reified X> PropsStore.mapByKeyOrDefault(
    key: String,
    defaultValue: X,
    jsonFormat: Json = LenientAndPrettyJson,
): SyncStore<X> = mapByKeyOrDefault(key, defaultValue, serializer(), jsonFormat)
