package playground.components.bookmark

import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import dev.fritz2.core.href
import dev.fritz2.core.storeOf
import dev.fritz2.headless.components.disclosure
import dev.fritz2.headless.components.tabGroup
import kotlinx.coroutines.flow.map
import playground.components.bookmark.BookmarkTreeNode.Bookmark
import playground.components.bookmark.BookmarkTreeNode.Folder

private fun Folder.formatTitle(): String =
    title ?: "—"

private fun Bookmark.formatTitle(): String =
    title?.takeUnless { it == uri?.toString() } ?: uri?.toString()?.removePrefix("https://")?.removePrefix("http://") ?: "—"

enum class BookmarksStyle {

    ListStyle {
        override val title: String = "List"
        override fun RenderContext.render(nodes: List<BookmarkTreeNode>) {
            ul(
                classes(
                    "px-4 py-2.5",
                    "overflow-x-hidden overflow-y-auto",
                    "bg-default/60 text-default dark:bg-invert/60 dark:text-invert",
                )
            ) {
                nodes.forEach {
                    li { render(it) }
                }
            }
        }

        override fun RenderContext.render(node: BookmarkTreeNode) {
            when (node) {
                is Folder -> disclosure {
                    openState(storeOf(true))
                    disclosureButton("flex items-center gap-2") {
                        icon("w-4 h-4", opened.map { if (it) SolidHeroIcons.folder_open else SolidHeroIcons.folder })
                        span("truncate") { +node.formatTitle() }
                    }
                    disclosurePanel(tag = RenderContext::ul) {
                        node.children.forEach { child ->
                            li("ml-6") { render(child) }
                        }
                    }
                }

                is Bookmark -> a("flex items-center gap-2 truncate") {
                    icon("w-4 h-4", node.icon ?: OutlineHeroIcons.link)
                    span("truncate") { +node.formatTitle() }
                    node.uri?.also { href(node.uri.toString()) }
                }
            }
        }
    },

    TabStyle {
        override val title: String = "Tabs"
        override fun RenderContext.render(nodes: List<BookmarkTreeNode>) {
            tabGroup(
                classes(
                    "p-2 grid grid-cols-1 grid-rows-[auto,_minmax(min(20rem,_100%),1fr)] max-h-[500px] debug gap-2",
                    "bg-default/20 text-default dark:bg-invert/20 dark:text-invert",
                )
            ) {
                tabList(
                    classes(
                        "flex gap-1",
                        "rounded-xl",
                        "shadow-lg dark:shadow-xl bg-glass text-default dark:text-invert",
                        "focus:outline-none",
                    )
                ) {
                    nodes.filterIsInstance<Folder>().let { folders ->
                        val topLevelBookmarks = nodes.filterIsInstance<Folder>()
                        if (topLevelBookmarks.isEmpty()) folders
                        else folders + Folder("Bookmarks", topLevelBookmarks)
                    }.forEachIndexed { index, folder ->
                        tab(
                            classes(
                                "w-full p-2",
                                "font-medium rounded-xl",
                                "focus:outline-none",
                                "truncate",
                            )
                        ) {
                            className(selected.map { s ->
                                if (s == index) "bg-glass text-default dark:text-invert"
                                else ""
                            })
                            +folder.formatTitle()
                        }
                    }
                }
                tabPanels("grid items-stretch content-stretch") {
                    nodes.forEachIndexed { index, node ->
                        panel(
                            classes(
                                "grid items-stretch content-stretch",
                                "debug",
                                "rounded-xl",
                                "overflow-y-hidden",
                            )
                        ) { render(node) }
                    }
                }
            }
        }

        override fun RenderContext.render(node: BookmarkTreeNode) {
            when (node) {
                is Folder -> with(ListStyle) { render(node.children) }
                is Bookmark -> with(ListStyle) { render(node) }
            }
        }
    };

    abstract val title: String
    abstract fun RenderContext.render(nodes: List<BookmarkTreeNode>)
    abstract fun RenderContext.render(node: BookmarkTreeNode)

}
