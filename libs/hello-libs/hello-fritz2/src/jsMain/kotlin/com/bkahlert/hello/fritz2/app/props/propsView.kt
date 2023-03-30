@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.app.props

import com.bkahlert.hello.fritz2.EditorAction
import com.bkahlert.hello.fritz2.SyncState
import com.bkahlert.hello.fritz2.components.dataView
import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.inputEditor
import com.bkahlert.hello.fritz2.lensForElementOrNull
import com.bkahlert.hello.fritz2.shortcut
import com.bkahlert.hello.fritz2.shortcuts
import com.bkahlert.hello.fritz2.textFieldEditor
import com.bkahlert.hello.fritz2.validateCatching
import com.bkahlert.hello.fritz2.validateJson
import com.bkahlert.kommons.dom.ObjectUri
import com.bkahlert.kommons.dom.download
import com.bkahlert.kommons.dom.mapTarget
import com.bkahlert.kommons.json.LenientAndPrettyJson
import com.bkahlert.kommons.randomString
import dev.fritz2.core.RenderContext
import dev.fritz2.core.WithJob
import dev.fritz2.core.accept
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.lensOf
import dev.fritz2.core.title
import dev.fritz2.core.type
import dev.fritz2.validation.ValidatingStore
import dev.fritz2.validation.ValidationMessage
import io.ktor.http.ContentType.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
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
            lensOf<Map<String, JsonElement>, List<Triple<String, JsonElement, Flow<SyncState<JsonElement?>>>>>(
                id = "with-sync-state",
                getter = { it.entries.map { (k, v) -> Triple(k, v, props.syncState.map { s -> s.map(lensForElementOrNull(k)) }) } },
                setter = { _, triples -> triples.associate { (k, v, _) -> k to v } },
            )
        ),
        lenses = listOf(
            lensOf("id", { it.first }) { (_, v, s), k -> Triple(k, v, s) },
            lensOf("value", { jsonFormat.encodeToString(it.second) }) { (k, _, s), v -> Triple(k, jsonFormat.parseToJsonElement(v), s) },
        ),
        creator = {
            val id = randomString()
            Triple(id, JsonNull, props.syncState.map { it.map(lensForElementOrNull(id)) })
        },
        editor = { item, lens ->
            { close ->

                val actions = listOf<EditorAction<Pair<String, JsonElement>>>(
                    EditorAction.Save<Pair<String, JsonElement>> {
                        props.remove(item.first)
                        props.set(it)
                    },
                    EditorAction.Cancel<Pair<String, JsonElement>>(),
                    EditorAction.Delete<Pair<String, JsonElement>> {
                        props.remove(item.first)
                    },
                )

                val (store: ValidatingStore<String, Unit, ValidationMessage>, editor) = if (lens.id.equals("id", ignoreCase = true)) {
                    val s = ValidatingStore(lens.get(item), validateCatching { inspector ->
                        require(inspector.data.isNotBlank()) { "ID must not be blank" }
                    })
                    val e = inputEditor("font-mono", s)
                    s to e
                } else {
                    val s = ValidatingStore(lens.get(item), validateJson(jsonFormat))
                    val e = textFieldEditor("font-mono", s)
                    s to e
                }

                val performAction: WithJob.(EditorAction<Pair<String, JsonElement>>) -> Unit = { action ->
                    action.handle(
                        if (lens.id.equals("id", ignoreCase = true)) {
                            store.current to item.second
                        } else {
                            item.first to jsonFormat.parseToJsonElement(store.current)
                        }
                    )
                    close()
                }

                editor.shortcuts.mapNotNull { (event, shortcut) ->
                    actions.firstNotNullOfOrNull { a -> if (a.shortcut == shortcut) event to a else null }
                } handledBy { (event, action) ->
                    event.stopImmediatePropagation()
                    event.preventDefault()
                    performAction(action)
                }

                div("mt-1 flex flex-wrap items-center justify-between") {
//                    store.messages.render { messages ->
//                        ul("text-xs text-red-500") {
//                            messages.forEach { message ->
//                                li { +message.toString() }
//                            }
//                        }
//                    }
                    actions.forEach { action ->
                        button(
                            classes(
                                "inline-flex items-center justify-center px-3 py-1",
                                "disabled:opacity-50 text-xs font-semibold",
                                "transition [&:not(:disabled):hover]:font-bold",
                            )
                        ) {
                            type("button")
                            +action.name
                            shortcut(action.shortcut)
                            disabled(action.disabled(store.messages))
                            clicks.map { action } handledBy { performAction(it) }
                        }
                    }
                }
            }
        }
    )
}
