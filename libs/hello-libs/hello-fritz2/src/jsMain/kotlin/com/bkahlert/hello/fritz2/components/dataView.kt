@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.components

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.SyncState
import com.bkahlert.hello.fritz2.components.heroicons.MiniHeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.syncState
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.IdProvider
import dev.fritz2.core.Lens
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.classes
import dev.fritz2.core.lensOf
import dev.fritz2.core.placeholder
import dev.fritz2.core.storeOf
import dev.fritz2.core.title
import dev.fritz2.core.type
import dev.fritz2.headless.components.dataCollection
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.foundation.SortDirection.ASC
import dev.fritz2.headless.foundation.SortDirection.DESC
import dev.fritz2.headless.foundation.SortDirection.NONE
import dev.fritz2.headless.foundation.utils.scrollintoview.ScrollPosition.center
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement

public fun <T> RenderContext.dataView(
    name: String,
    store: Store<List<T>>,
    lenses: List<Lens<T, String>> = listOf(lensOf("Value", { it.toString() }, { p, _ -> p })),
    controls: ContentBuilder<HTMLDivElement>? = null,
) {

    val idProvider: IdProvider<T, String> = { lenses.first().get(it) }

    val columnClasses = object : AbstractList<String>() {
        override val size: Int get() = lenses.size
        override fun get(index: Int): String {
            return if (index == 0) "truncate font-medium sm:text-sm text-gray-500"
            else "truncate mt-1 sm:text-sm text-gray-900 sm:col-span-2 sm:mt-0"
        }
    }

    dataCollection("bg-white shadow rounded-lg") {
        div(
            classes(
                "px-4 py-5 sm:px-6 flex flex-col",
                "sm:flex-row sm:flex-wrap sm:items-center gap-4"
            )
        ) {
            h3("text-base font-semibold leading-6 text-gray-900") { +name }
            store.data.render { data ->
                data(data, idProvider)
                if (data.isEmpty()) {
                    span("italic sm:text-sm text-gray-500 sm:ml-2") { +"Empty" }
                } else {
                    val filterStore = storeOf("")
                    filterStore.data handledBy filterByText()
                    inputField("flex-grow relative") {
                        value(filterStore)
                        keydowns.map {
                            if (it.key == "Escape") {
                                (it.target as? HTMLInputElement)?.blur()
                                ""
                            } else (it.target as? HTMLInputElement)?.value.orEmpty()
                        } handledBy filterStore.update
                        div("absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none") {
                            icon("w-4 h-4 text-gray-500", OutlineHeroIcons.magnifying_glass)
                        }
                        inputTextfield("block w-full p-2 pl-10 text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500") {
                            type("search")
                            placeholder("Filter...")
                        }
                    }
                }
            }
            controls?.invoke(this)
        }

        store.data.render { _ ->
            div("relative max-h-96 overflow-auto focus:outline-none") {

                val rowClasses = classes(
                    "even:bg-gray-50 odd:bg-white",
                    "px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6"
                )

                dl("sticky top-0 shadow-md") {
                    className(store.data.map { if (it.isEmpty()) "hidden" else "" })
                    div(rowClasses) {
                        lenses.forEachIndexed { index, lens ->
                            dataCollectionSortButton(
                                comparatorAscending = compareBy { lens.get(it) },
                                comparatorDescending = compareByDescending { lens.get(it) },
                                classes = classes(columnClasses[index], "select-all block font-medium sm:text-sm focus:outline-none"),
                            ) {
                                div("flex justify-between") {
                                    span { +lens.id }
                                    direction.render {
                                        icon(
                                            "h-4 w-4 mt-1 mr-2", when (it) {
                                                NONE -> MiniHeroIcons.chevron_up_down
                                                ASC -> MiniHeroIcons.bars_arrow_up
                                                DESC -> MiniHeroIcons.bars_arrow_down
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                dataCollectionItems(tag = RenderContext::dl) {
                    scrollIntoView(vertical = center)
                    items.renderEach(idProvider, into = this, batch = true) { item: T ->
                        val id = idProvider(item)
                        dataCollectionItem(
                            item,
                            id = id,
                            classes = rowClasses,
                            tag = RenderContext::div
                        ) {
                            lenses.forEachIndexed { index, lens ->
                                className("relative")
                                div("absolute inset-y-2 left-3 w-1 rounded-full pointer-events-none opacity-50 bg-[--tw-ring-color]") {
                                    syncState((item as? Triple<*, *, *>)?.third?.let { it as? Flow<*> }?.filterIsInstance<SyncState<*>>() ?: flowOf(null))
                                }
                                val tag: RenderContext.(HtmlTag<HTMLSpanElement>.() -> Unit) -> Unit = {
                                    if (index == 0) dt(columnClasses[0], "$id-${lens.id}", {}, it)
                                    else dd(columnClasses[1], "$id-${lens.id}", {}, it)
                                }
                                tag {
                                    val value = lens.get(item)
                                    +value
                                    title(value)
                                    className("select-all")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
