package playground

import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.app.props.PropsStore
import com.bkahlert.hello.fritz2.app.props.StoragePropsDataSource
import com.bkahlert.hello.fritz2.app.props.propsView
import com.bkahlert.hello.fritz2.components.SimplePage
import com.bkahlert.hello.fritz2.components.bookmarks.Bookmark
import com.bkahlert.hello.fritz2.components.bookmarks.BookmarkEditor
import com.bkahlert.hello.fritz2.components.button
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.modal
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.Store
import dev.fritz2.core.storeOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer

val PlaygroundContainer = SimplePage(
    id = "playground",
    label = "Playground",
    description = "A place to play around with UI elements",
    heroIcon = HeroIcons::beaker,
) {

    val propsStore = PropsStore(
        mapOf(
            "foo" to JsonPrimitive("bar"),
            "baz" to JsonArray(listOf(JsonPrimitive("qux"), JsonPrimitive("pokkkkkkkkkkklöklölök"))),
        ),
        StoragePropsDataSource.InMemoryPropsDataSource(),
    )

    val activeEditor: Store<BookmarkEditor?> = storeOf(null)
    val bookmarks = Bookmarks.invoke(propsStore, Bookmarks.DEFAULT_VALUE, Bookmarks.DEFAULT_KEY).apply {
        edit handledBy { editor ->
            editor.addOrUpdate.map { null } handledBy activeEditor.update
            editor.delete.map { null } handledBy activeEditor.update
            editor.cancel.map { null } handledBy activeEditor.update
            activeEditor.update(editor)
        }
    }

    activeEditor.data.render { bookmarkEditor ->
        if (bookmarkEditor != null) {
            modal { labelledbyId ->
                with(bookmarkEditor) {
                    render(labelledbyId = labelledbyId)
                }
            }
        }
    }

    h1 { +"Bookmarks" }
    div {
        bookmarks.data.renderEach { bookmark ->
            div("flex items-center space-x-2") {
                div("w-8 h-8") { with(bookmark) { render() } }
                button(OutlineHeroIcons.pencil, "Edit").apply {
                    clicks.map { bookmark } handledBy bookmarks.edit
                }
            }
        }
        div("flex items-center space-x-2") {
            button(OutlineHeroIcons.plus, "New").apply {
                clicks.map { Bookmark() } handledBy bookmarks.edit
            }
        }

    }

    bookmarks.edit(bookmarks.current.first())
    propsView(propsStore)
}

class Bookmarks(
    val store: SyncStore<List<Bookmark>>,
) : SyncStore<List<Bookmark>> by store {

    val addOrUpdate = handle<Bookmark> { bookmarks, bookmark ->
        val existing = bookmarks.firstOrNull { it.id == bookmark.id }
        if (existing == null) {
            bookmarks + bookmark
        } else {
            bookmarks.map { if (it.id == bookmark.id) bookmark else it }
        }
    }

    val edit = handleAndEmit<Bookmark, BookmarkEditor> { bookmarks, bookmark ->
        emit(BookmarkEditor(
            bookmarks.none { it.id == bookmark.id },
            bookmark,
        ).also {
            it.addOrUpdate handledBy addOrUpdate
            it.delete handledBy delete
        })
        bookmarks
    }

    val delete = handle<Bookmark> { bookmarks, bookmark ->
        bookmarks.filter { it.id != bookmark.id }
    }

    companion object {
        val DEFAULT_KEY: String = "quick-links"
        val DEFAULT_VALUE: List<Bookmark> = listOf(
            Bookmark(
                title = "GitHub",
                href = Uri("https://github.com/bkahlert"),
                icon = Uri("https://github.githubassets.com/favicons/favicon.svg"),
            ),
        )

        fun invoke(propsStore: PropsStore, defaultValue: List<Bookmark>, id: String): Bookmarks =
            Bookmarks(propsStore.map(id, defaultValue, serializer()))
    }
}
