package com.bkahlert.hello.quicklink

import com.bkahlert.hello.bookmark.BookmarkEditor
import com.bkahlert.hello.bookmark.BookmarkTreeNode
import com.bkahlert.hello.bookmark.formatTitle
import com.bkahlert.hello.bookmark.iconOrDefault
import com.bkahlert.hello.editor.move
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.hello.modal.modal
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.Handler
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.href
import dev.fritz2.core.lensOf
import dev.fritz2.core.storeOf
import dev.fritz2.core.title
import dev.fritz2.core.transition
import dev.fritz2.core.type
import dev.fritz2.headless.components.menu
import dev.fritz2.headless.foundation.utils.popper.Placement
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event

public class QuickLinks(
    store: SyncStore<List<BookmarkTreeNode.Bookmark>>,
) : SyncStore<List<BookmarkTreeNode.Bookmark>> by store {

    public val addOrUpdate: Handler<BookmarkTreeNode.Bookmark> = handle { bookmarks, bookmark ->
        val existing = bookmarks.firstOrNull { it.id == bookmark.id }
        if (existing == null) {
            bookmarks + bookmark
        } else {
            bookmarks.map { if (it.id == bookmark.id) bookmark else it }
        }
    }

    public val rankUp: Handler<BookmarkTreeNode.Bookmark> = handle { links, link ->
        links.move(link, -1)
    }
    public val rankDown: Handler<BookmarkTreeNode.Bookmark> = handle { links, link ->
        links.move(link, +1)
    }

    public val edit: EmittingHandler<BookmarkTreeNode.Bookmark, BookmarkEditor> = handleAndEmit { bookmarks, bookmark ->
        emit(
            BookmarkEditor(
                isNew = bookmarks.none { it.id == bookmark.id },
                bookmark,
            ).also {
                it.addOrUpdate handledBy addOrUpdate
                it.delete handledBy delete
            })
        bookmarks
    }

    public val delete: Handler<BookmarkTreeNode.Bookmark> = handle { bookmarks, bookmark ->
        bookmarks.filter { it.id != bookmark.id }
    }

    public val openInSameWindow: Handler<BookmarkTreeNode.Bookmark> = handle { bookmarks, bookmark ->
        bookmark.open()
        bookmarks
    }

    public fun render(renderContext: RenderContext): HtmlTag<HTMLDivElement> =
        renderContext.div("flex items-center justify-center border border-black/20 dark:border-white/20 rounded") {

            val activeEditor: Store<BookmarkEditor?> = storeOf(null)
            edit handledBy { editor ->
                editor.addOrUpdate.map { null } handledBy activeEditor.update
                editor.delete.map { null } handledBy activeEditor.update
                editor.cancel.map { null } handledBy activeEditor.update
                activeEditor.update(editor)
            }

            activeEditor.data.render { bookmarkEditor ->
                if (bookmarkEditor != null) {
                    modal { labelledbyId ->
                        with(bookmarkEditor) { render(labelledbyId = labelledbyId) }
                        subscribe<Event>("cancel").map { null } handledBy activeEditor.update
                        subscribe<Event>("close").map { null } handledBy activeEditor.update
                    }
                }
            }

            div("flex items-center justify-center bg-black/10 dark:bg-white/10 rounded") {
                val openState = storeOf<BookmarkTreeNode.Bookmark?>(null)
                data.renderEach { bookmark ->
                    menu {
                        openState(openState.map(lensOf("bookmark", { it == bookmark }, { p, v -> if (v) p else null })))

                        menuButton(
                            classes(
                                "inline-flex justify-center transition opacity-60 hover:opacity-100",
                                "focus:outline-none focus-visible:ring-4 focus-visible:ring-white focus-visible:ring-opacity-75",
                                "rounded shrink-0 w-9 h-9 px-2.5 py-2",
                            )
                        ) {
                            icon("w-full h-full", bookmark.iconOrDefault())
                            title(bookmark.formatTitle())
                            clicks.map { bookmark } handledBy openInSameWindow
                            contextmenus.preventDefault().map { bookmark } handledBy openState.update
                            // TODO use https://stackoverflow.com/questions/12304012/preventing-default-context-menu-on-longpress-longclick-in-mobile-safari-ipad
                        }

                        menuItems(
                            classes(
                                "absolute left-0",
                                "w-48",
                                "rounded-md",
                                "shadow-lg dark:shadow-xl bg-glass text-default dark:text-invert",
                                "focus:outline-none"
                            )
                        ) {
                            placement = Placement.bottomStart
                            distance = 5

                            transition(
                                opened,
                                "transition ease-out duration-100",
                                "opacity-0 scale-95",
                                "opacity-100 scale-100",
                                "transition ease-in duration-75",
                                "opacity-100 scale-100",
                                "opacity-0 scale-95",
                            )

                            div("px-1 py-1") {
                                div("flex") {
                                    menuItem("flex-1 rounded-md px-2 py-2 text-center font-medium sm:text-sm block", tag = RenderContext::a) {
                                        className(active.combine(disabled) { a, d -> if (a && !d) "bg-glass text-default dark:text-invert" else if (d) "opacity-50 cursor-default" else "" })
                                        +"Open"
                                        href(bookmark.url.toString())
                                    }
                                    menuItem("rounded-md px-2 py-2 text-center text-red-600 dark:text-red-400 font-medium sm:text-sm") {
                                        className(active.combine(disabled) { a, d -> if (a && !d) "bg-glass text-default dark:text-invert" else if (d) "opacity-50 cursor-default" else "" })
                                        +"Delete"
                                        selected.map { bookmark } handledBy delete
                                    }
                                }
                                div("flex") {
                                    menuItem("flex-auto rounded-md px-2 py-2 font-medium sm:text-sm") {
                                        val disabled = data.map { it.firstOrNull() == bookmark }
                                        disabled(disabled)
                                        className(active.combine(disabled) { a, d -> if (a && !d) "bg-glass text-default dark:text-invert" else if (d) "opacity-50 cursor-default" else "" })
                                        icon("w-4 h-4 mx-auto", SolidHeroIcons.arrow_left)
                                        title("Move left")
                                        selected.map { bookmark } handledBy rankUp
                                    }
                                    menuItem("flex-auto rounded-md px-2 py-2 text-center font-medium sm:text-sm") {
                                        className(active.combine(disabled) { a, d -> if (a && !d) "bg-glass text-default dark:text-invert" else if (d) "opacity-50 cursor-default" else "" })
                                        +"Edit..."
                                        selected.map { bookmark } handledBy edit
                                    }
                                    menuItem("flex-auto rounded-md px-2 py-2 font-medium sm:text-sm") {
                                        val disabled = data.map { it.lastOrNull() == bookmark }
                                        disabled(disabled)
                                        className(active.combine(disabled) { a, d -> if (a && !d) "bg-glass text-default dark:text-invert" else if (d) "opacity-50 cursor-default" else "" })
                                        icon("w-4 h-4 mx-auto", SolidHeroIcons.arrow_right)
                                        title("Move right")
                                        selected.map { bookmark } handledBy rankDown
                                    }
                                }
                            }
                        }
                    }
                }
            }

            button {
                className(
                    classes(
                        "rounded",
                        "shrink-0 w-9 h-9 px-2.5 py-2",
                        "inline-flex justify-center",
                        "text-white/95 dark:text-white/75",
                        "hover:bg-glass hover:text-default dark:hover:text-invert",
                        "focus:outline-none focus-visible:ring-4 focus-visible:ring-white focus-visible:ring-opacity-75",
                    )
                )
                type("button")
                clicks.map { BookmarkTreeNode.Bookmark() } handledBy edit
                title("Create bookmark...")
                icon(classes("w-full h-full"), SolidHeroIcons.plus)
            }
        }

    override fun toString(): String {
        return "QuickLinks(links=$current)"
    }

    public companion object {
        public val DefaultLinks: List<BookmarkTreeNode.Bookmark> = listOf(
            BookmarkTreeNode.Bookmark(
                title = "GitHub",
                url = Uri("https://github.com/bkahlert"),
                icon = Uri(
                    "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzIiIGhlaWdodD0iMzIiIHZpZXdCb3g9IjAgMCAzMiAzMiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZmlsbC1ydWxlPSJldmVub2RkIiBjbGlwLXJ1bGU9ImV2ZW5vZGQiIGQ9Ik0xNiAwQzcuMTYgMCAwIDcuMTYgMCAxNkMwIDIzLjA4IDQuNTggMjkuMDYgMTAuOTQgMzEuMThDMTEuNzQgMzEuMzIgMTIuMDQgMzAuODQgMTIuMDQgMzAuNDJDMTIuMDQgMzAuMDQgMTIuMDIgMjguNzggMTIuMDIgMjcuNDRDOCAyOC4xOCA2Ljk2IDI2LjQ2IDYuNjQgMjUuNTZDNi40NiAyNS4xIDUuNjggMjMuNjggNSAyMy4zQzQuNDQgMjMgMy42NCAyMi4yNiA0Ljk4IDIyLjI0QzYuMjQgMjIuMjIgNy4xNCAyMy40IDcuNDQgMjMuODhDOC44OCAyNi4zIDExLjE4IDI1LjYyIDEyLjEgMjUuMkMxMi4yNCAyNC4xNiAxMi42NiAyMy40NiAxMy4xMiAyMy4wNkM5LjU2IDIyLjY2IDUuODQgMjEuMjggNS44NCAxNS4xNkM1Ljg0IDEzLjQyIDYuNDYgMTEuOTggNy40OCAxMC44NkM3LjMyIDEwLjQ2IDYuNzYgOC44MiA3LjY0IDYuNjJDNy42NCA2LjYyIDguOTggNi4yIDEyLjA0IDguMjZDMTMuMzIgNy45IDE0LjY4IDcuNzIgMTYuMDQgNy43MkMxNy40IDcuNzIgMTguNzYgNy45IDIwLjA0IDguMjZDMjMuMSA2LjE4IDI0LjQ0IDYuNjIgMjQuNDQgNi42MkMyNS4zMiA4LjgyIDI0Ljc2IDEwLjQ2IDI0LjYgMTAuODZDMjUuNjIgMTEuOTggMjYuMjQgMTMuNCAyNi4yNCAxNS4xNkMyNi4yNCAyMS4zIDIyLjUgMjIuNjYgMTguOTQgMjMuMDZDMTkuNTIgMjMuNTYgMjAuMDIgMjQuNTIgMjAuMDIgMjYuMDJDMjAuMDIgMjguMTYgMjAgMjkuODggMjAgMzAuNDJDMjAgMzAuODQgMjAuMyAzMS4zNCAyMS4xIDMxLjE4QzI3LjQyIDI5LjA2IDMyIDIzLjA2IDMyIDE2QzMyIDcuMTYgMjQuODQgMCAxNiAwVjBaIiBmaWxsPSIjMjQyOTJFIi8+Cjwvc3ZnPgoK"
                ),
            ),
            BookmarkTreeNode.Bookmark(
                title = "Dr. Björn Kahlert – UX Specialist / Certified Scrum Master / Visual Faciliator / Software Developer",
                url = Uri("https://github.com/bkahlert"),
                icon = Uri("https://bkahlert.com"),
            ),
            BookmarkTreeNode.Bookmark(
                title = "Playground",
                url = Uri("/playground"),
                icon = Uri("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iY3VycmVudENvbG9yIiBjbGFzcz0ic2l6ZS02Ij4KICA8cGF0aCBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGQ9Ik0xMC41IDMuNzk4djUuMDJhMyAzIDAgMCAxLS44NzkgMi4xMjFsLTIuMzc3IDIuMzc3YTkuODQ1IDkuODQ1IDAgMCAxIDUuMDkxIDEuMDEzIDguMzE1IDguMzE1IDAgMCAwIDUuNzEzLjYzNmwuMjg1LS4wNzEtMy45NTQtMy45NTVhMyAzIDAgMCAxLS44NzktMi4xMjF2LTUuMDJhMjMuNjE0IDIzLjYxNCAwIDAgMC0zIDBabTQuNS4xMzhhLjc1Ljc1IDAgMCAwIC4wOTMtMS40OTVBMjQuODM3IDI0LjgzNyAwIDAgMCAxMiAyLjI1YTI1LjA0OCAyNS4wNDggMCAwIDAtMy4wOTMuMTkxQS43NS43NSAwIDAgMCA5IDMuOTM2djQuODgyYTEuNSAxLjUgMCAwIDEtLjQ0IDEuMDZsLTYuMjkzIDYuMjk0Yy0xLjYyIDEuNjIxLS45MDMgNC40NzUgMS40NzEgNC44OCAyLjY4Ni40NiA1LjQ0Ny42OTggOC4yNjIuNjk4IDIuODE2IDAgNS41NzYtLjIzOSA4LjI2Mi0uNjk3IDIuMzczLS40MDYgMy4wOTItMy4yNiAxLjQ3LTQuODgxTDE1LjQ0IDkuODc5QTEuNSAxLjUgMCAwIDEgMTUgOC44MThWMy45MzZaIiBjbGlwLXJ1bGU9ImV2ZW5vZGQiIC8+Cjwvc3ZnPgo="),
            ),
        )
    }
}
