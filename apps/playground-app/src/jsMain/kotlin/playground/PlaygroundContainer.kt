package playground

import com.bkahlert.hello.app.props.PropsStore
import com.bkahlert.hello.app.props.StoragePropsDataSource
import com.bkahlert.hello.app.props.mapByKeyOrDefault
import com.bkahlert.hello.app.props.propsView
import com.bkahlert.hello.bookmark.Bookmark
import com.bkahlert.hello.bookmark.BookmarkEditor
import com.bkahlert.hello.button.button
import com.bkahlert.hello.components.SimplePage
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.modal.modal
import com.bkahlert.hello.quicklink.QuickLinks
import dev.fritz2.core.Store
import dev.fritz2.core.storeOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive

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
    val bookmarks = QuickLinks(propsStore.mapByKeyOrDefault("quick-links", QuickLinks.DefaultLinks)).apply {
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
