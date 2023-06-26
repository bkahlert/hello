package com.bkahlert.hello.bookmark

import com.bkahlert.hello.bookmark.BookmarkTreeNode.Bookmark
import com.bkahlert.hello.bookmark.BookmarkTreeNode.Folder
import com.bkahlert.hello.icon.heroicons.MiniHeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.classes
import dev.fritz2.core.href
import dev.fritz2.core.storeOf
import dev.fritz2.headless.components.disclosure
import dev.fritz2.headless.components.tabGroup
import kotlinx.coroutines.flow.map

public fun Folder.formatTitle(): String =
    title ?: "—"

public fun Bookmark.formatTitle(): String =
    title?.takeUnless { it == url?.toString() } ?: url?.toString()?.removePrefix("https://")?.removePrefix("http://") ?: "—"

// TODO refactor
private fun favicon(uri: Uri): Uri? = uri.authority?.host?.let { favicon(it) }
private fun favicon(host: String): Uri? = when (host.split(".").takeLast(2).joinToString(".")) {
    "githubusercontent.com" -> Uri(
        "data:image/svg+xml;charset=UTF-8;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iY3VycmVudENvbG9yIj48cGF0aAogICAgICAgICAgICAgICAgICAgICAgICBkPSJNMTIgLjI5N2MtNi42MyAwLTEyIDUuMzczLTEyIDEyIDAgNS4zMDMgMy40MzggOS44IDguMjA1IDExLjM4NS42LjExMy44Mi0uMjU4LjgyLS41NzcgMC0uMjg1LS4wMS0xLjA0LS4wMTUtMi4wNC0zLjMzOC43MjQtNC4wNDItMS42MS00LjA0Mi0xLjYxQzQuNDIyIDE4LjA3IDMuNjMzIDE3LjcgMy42MzMgMTcuN2MtMS4wODctLjc0NC4wODQtLjcyOS4wODQtLjcyOSAxLjIwNS4wODQgMS44MzggMS4yMzYgMS44MzggMS4yMzYgMS4wNyAxLjgzNSAyLjgwOSAxLjMwNSAzLjQ5NS45OTguMTA4LS43NzYuNDE3LTEuMzA1Ljc2LTEuNjA1LTIuNjY1LS4zLTUuNDY2LTEuMzMyLTUuNDY2LTUuOTMgMC0xLjMxLjQ2NS0yLjM4IDEuMjM1LTMuMjItLjEzNS0uMzAzLS41NC0xLjUyMy4xMDUtMy4xNzYgMCAwIDEuMDA1LS4zMjIgMy4zIDEuMjMuOTYtLjI2NyAxLjk4LS4zOTkgMy0uNDA1IDEuMDIuMDA2IDIuMDQuMTM4IDMgLjQwNSAyLjI4LTEuNTUyIDMuMjg1LTEuMjMgMy4yODUtMS4yMy42NDUgMS42NTMuMjQgMi44NzMuMTIgMy4xNzYuNzY1Ljg0IDEuMjMgMS45MSAxLjIzIDMuMjIgMCA0LjYxLTIuODA1IDUuNjI1LTUuNDc1IDUuOTIuNDIuMzYuODEgMS4wOTYuODEgMi4yMiAwIDEuNjA2LS4wMTUgMi44OTYtLjAxNSAzLjI4NiAwIC4zMTUuMjEuNjkuODI1LjU3QzIwLjU2NSAyMi4wOTIgMjQgMTcuNTkyIDI0IDEyLjI5N2MwLTYuNjI3LTUuMzczLTEyLTEyLTEyIi8+PC9zdmc+"
    )

    "youtube.com" -> Uri("https://icongr.am/fontawesome/youtube.svg?size=128&color=ff0000")
    else -> null
}

public fun Bookmark.iconOrNull(): Uri? =
    icon ?: url?.let { favicon(it) }

public fun Bookmark.iconOrDefault(): Uri =
    iconOrNull() ?: MiniHeroIcons.link

public enum class BookmarksStyle {

    ListStyle {
        override val title: String = "List"
        override fun RenderContext.render(nodes: List<BookmarkTreeNode>) {
            ul(
                classes(
                    "px-4 py-2.5",
                    "overflow-x-hidden overflow-y-auto",
                    "rounded-xl",
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
                    icon("w-4 h-4", node.iconOrDefault())
                    span("truncate") { +node.formatTitle() }
                    node.url?.also { href(node.url.toString()) }
                }
            }
        }
    },

    TabStyle {
        override val title: String = "Tabs"
        override fun RenderContext.render(nodes: List<BookmarkTreeNode>) {
            tabGroup(
                classes(
                    "p-2 grid grid-cols-1 grid-rows-[1fr_minmax(1px,100%)] gap-2",
                    "overflow-hidden",
                    "text-default dark:text-invert",
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
                        val topLevelBookmarks = nodes.filterIsInstance<Bookmark>()
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
                    nodes.forEach { node ->
                        panel(
                            classes(
                                "grid items-stretch content-stretch",
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

    public abstract val title: String
    public abstract fun RenderContext.render(nodes: List<BookmarkTreeNode>)
    public abstract fun RenderContext.render(node: BookmarkTreeNode)

}
