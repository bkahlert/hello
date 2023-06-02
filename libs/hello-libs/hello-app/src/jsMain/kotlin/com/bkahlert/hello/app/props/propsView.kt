@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.app.props

import com.bkahlert.hello.components.dataView
import com.bkahlert.hello.fritz2.SyncState
import com.bkahlert.hello.fritz2.lensForElementOrNull
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.kommons.dom.ObjectUri
import com.bkahlert.kommons.dom.download
import com.bkahlert.kommons.dom.mapTarget
import com.bkahlert.kommons.json.LenientAndPrettyJson
import dev.fritz2.core.RenderContext
import dev.fritz2.core.accept
import dev.fritz2.core.classes
import dev.fritz2.core.lensOf
import dev.fritz2.core.title
import dev.fritz2.core.type
import io.ktor.http.ContentType.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.files.get

public fun RenderContext.propsView(
    props: PropsStore,
) {
    val jsonFormat = LenientAndPrettyJson

    dataView<Triple<String, JsonElement, Flow<SyncState<JsonElement?>>>>(
        "Props",
        controls = {
            a(
                classes(
                    "relative group cursor-pointer flex items-center justify-center pl-4 py-5",
                    "flex items-center",
                    "text-light disabled:text-gray-500/75",
                    "focus-visible:underline focus-visible:decoration-2 focus-visible:underline-offset-2",
                )
            ) {
                icon("group-hover:stroke-swatch-blue pointer-events-none shrink-0 mr-1 h-5 w-5", OutlineHeroIcons.arrow_down_tray)
                title("Export")
                clicks handledBy {
                    ObjectUri(Application.Json, jsonFormat.encodeToString(props.current)).download("props.json")
                }
            }
            a(
                classes(
                    "relative group cursor-pointer flex items-center justify-center pl-4 py-5",
                    "flex items-center",
                    "text-light disabled:text-gray-500/75",
                    "focus-visible:underline focus-visible:decoration-2 focus-visible:underline-offset-2",
                )
            ) {
                icon("group-hover:stroke-swatch-blue pointer-events-none shrink-0 mr-1 h-5 w-5", OutlineHeroIcons.arrow_up_tray)
                title("Import")
                input("absolute inset-0 w-full h-full p-0 m-0 outline-none opacity-0 cursor-pointer") {
                    accept("application/json")
                    type("file")
                    dragovers.mapTarget<HTMLInputElement>() handledBy { it.parentElement?.classList?.add("animate-pulse") }
                    dragleaves.mapTarget<HTMLInputElement>() handledBy { it.parentElement?.classList?.remove("animate-pulse") }
                    dragends.mapTarget<HTMLInputElement>() handledBy { it.parentElement?.classList?.remove("animate-pulse") }
                    changes.mapTarget<HTMLInputElement>().mapNotNull { it.files?.get(0) } handledBy props.import
                }
            }
        },
        store = props.map(
            lensOf(
                id = "with-sync-state",
                getter = { it.entries.map { (k, v) -> Triple(k, v, props.syncState.map { s -> s.map(lensForElementOrNull(k)) }) } },
                setter = { _, triples -> triples.associate { (k, v, _) -> k to v } },
            )
        ),
        lenses = listOf(
            lensOf("id", { it.first }) { (_, v, s), k -> Triple(k, v, s) },
            lensOf("value", { jsonFormat.encodeToString(it.second) }) { (k, _, s), v -> Triple(k, jsonFormat.parseToJsonElement(v), s) },
        ),
    )
}
