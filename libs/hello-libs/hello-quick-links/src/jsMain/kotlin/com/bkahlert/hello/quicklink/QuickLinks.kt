package com.bkahlert.hello.quicklink

import com.bkahlert.hello.bookmark.BookmarkEditor
import com.bkahlert.hello.bookmark.BookmarkTreeNode
import com.bkahlert.hello.bookmark.formatTitle
import com.bkahlert.hello.bookmark.iconOrDefault
import com.bkahlert.hello.editor.move
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
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
                title = "Playground",
                url = Uri("playground/#hello/headless-ui"),
                icon = SolidHeroIcons.beaker,
            ),
            BookmarkTreeNode.Bookmark(
                title = "v1 (SemanticUI + jetpack Compose UI alpha)",
                url = Uri("v1-semantic-compose-alpha/#debug"),
                icon = OutlineHeroIcons.beaker,
            ),
            BookmarkTreeNode.Bookmark(
                title = "GitHub",
                url = Uri("https://github.com/bkahlert"),
                icon = Uri(
                    "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzIiIGhlaWdodD0iMzIiIHZpZXdCb3g9IjAgMCAzMiAzMiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cGF0aCBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGNsaXAtcnVsZT0iZXZlbm9kZCIgZD0iTTE2IDBDNy4xNiAwIDAgNy4xNiAwIDE2QzAgMjMuMDggNC41OCAyOS4wNiAxMC45NCAzMS4xOEMxMS43NCAzMS4zMiAxMi4wNCAzMC44NCAxMi4wNCAzMC40MkMxMi4wNCAzMC4wNCAxMi4wMiAyOC43OCAxMi4wMiAyNy40NEM4IDI4LjE4IDYuOTYgMjYuNDYgNi42NCAyNS41NkM2LjQ2IDI1LjEgNS42OCAyMy42OCA1IDIzLjNDNC40NCAyMyAzLjY0IDIyLjI2IDQuOTggMjIuMjRDNi4yNCAyMi4yMiA3LjE0IDIzLjQgNy40NCAyMy44OEM4Ljg4IDI2LjMgMTEuMTggMjUuNjIgMTIuMSAyNS4yQzEyLjI0IDI0LjE2IDEyLjY2IDIzLjQ2IDEzLjEyIDIzLjA2QzkuNTYgMjIuNjYgNS44NCAyMS4yOCA1Ljg0IDE1LjE2QzUuODQgMTMuNDIgNi40NiAxMS45OCA3LjQ4IDEwLjg2QzcuMzIgMTAuNDYgNi43NiA4LjgyIDcuNjQgNi42MkM3LjY0IDYuNjIgOC45OCA2LjIgMTIuMDQgOC4yNkMxMy4zMiA3LjkgMTQuNjggNy43MiAxNi4wNCA3LjcyQzE3LjQgNy43MiAxOC43NiA3LjkgMjAuMDQgOC4yNkMyMy4xIDYuMTggMjQuNDQgNi42MiAyNC40NCA2LjYyQzI1LjMyIDguODIgMjQuNzYgMTAuNDYgMjQuNiAxMC44NkMyNS42MiAxMS45OCAyNi4yNCAxMy40IDI2LjI0IDE1LjE2QzI2LjI0IDIxLjMgMjIuNSAyMi42NiAxOC45NCAyMy4wNkMxOS41MiAyMy41NiAyMC4wMiAyNC41MiAyMC4wMiAyNi4wMkMyMC4wMiAyOC4xNiAyMCAyOS44OCAyMCAzMC40MkMyMCAzMC44NCAyMC4zIDMxLjM0IDIxLjEgMzEuMThDMjcuNDIgMjkuMDYgMzIgMjMuMDYgMzIgMTZDMzIgNy4xNiAyNC44NCAwIDE2IDBWMFoiIGZpbGw9ImN1cnJlbnRDb2xvciIvPjwvc3ZnPg=="
                ),
            ),
        )
    }
}
