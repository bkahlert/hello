@file:Suppress("RedundantVisibilityModifier")

package playground.components

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.components.heroicons.MiniHeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
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
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLInputElement

private typealias Property = Pair<String, String?>

private val MapToPairListLens: Lens<Map<out String, String>, List<Pair<String, String>>> =
    lensOf("MapToPairListLens", Map<out String, String>::toList) { _, list -> list.toMap() }

public fun RenderContext.dataView(
    name: String,
    data: Store<Map<out String, String>>?,
    controls: ContentBuilder? = null,
) {
    dataView(name, data?.map(MapToPairListLens), controls)
}

public fun <T : Pair<String, String?>> RenderContext.dataView(
    name: String,
    data: Store<List<T>>?,
    controls: ContentBuilder? = null,
) {

    val columnClasses = listOf(
        "select-all truncate font-medium sm:text-sm text-gray-500",
        "select-all truncate mt-1 sm:text-sm text-gray-900 sm:col-span-2 sm:mt-0",
    )

    dataCollection("overflow-hidden bg-white shadow rounded-lg") {
        div(
            classes(
                "px-4 py-5 sm:px-6 flex",
                if (data == null) "items-center" else "flex-col",
                "sm:flex-row sm:items-center gap-4"
            )
        ) {
            h3("text-base font-semibold leading-6 text-gray-900") { +name }
            when (data?.current?.size) {
                null -> loader("Loading ${name.lowercase()}...")
                0 -> span("italic sm:text-sm text-gray-500 sm:ml-2") { +"Empty" }
                else -> {

                    val filterStore = storeOf("")

                    data(data.data, Pair<String, String?>::first)
                    filterStore.data handledBy filterByText()

                    inputField("flex-1 relative") {
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

        if (data?.current?.isNotEmpty() == true) {
            div("relative max-h-96 overflow-auto focus:outline-none") {

                val rowClasses = classes(
                    "even:bg-gray-50 odd:bg-white",
                    "px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6"
                )

                dl("sticky top-0 shadow-md") {
                    div(rowClasses) {
                        dataCollectionSortButton(
                            comparatorAscending = compareBy(Property::first),
                            comparatorDescending = compareByDescending(Property::first),
                            classes = classes(columnClasses[0], "block font-medium sm:text-sm focus:outline-none"),
                        ) {
                            div("flex justify-between") {
                                span { +"Key" }
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
                        dataCollectionSortButton(
                            comparatorAscending = compareBy(Property::second),
                            comparatorDescending = compareByDescending(Property::second),
                            classes = classes(columnClasses[1], "block font-medium sm:text-sm focus:outline-none"),
                        ) {
                            div("flex justify-between") {
                                span { +"Value" }
                                direction.render {
                                    icon(
                                        "h-4 w-4 mt-1 mr-2", when (it) {
                                            NONE -> SolidHeroIcons.chevron_up_down
                                            ASC -> SolidHeroIcons.bars_arrow_up
                                            DESC -> SolidHeroIcons.bars_arrow_down
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                dataCollectionItems(tag = RenderContext::dl) {
                    scrollIntoView(vertical = center)
                    items.renderEach(Property::first, into = this, batch = true) { item: Pair<String, String?> ->
                        dataCollectionItem(
                            item,
                            id = item.first,
                            classes = rowClasses,
                            tag = RenderContext::div
                        ) {
                            dt(columnClasses[0]) {
                                title(item.first)
                                +item.first
                            }
                            dd(columnClasses[1]) {
                                title(item.second ?: "—")
                                +(item.second ?: "—")
                            }
                        }
                    }
                }
            }
        }
    }
}
