package playground

import com.bkahlert.hello.app.widgets.DefaultWidgetRegistration
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.syncStoreOf
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.page.SimplePage
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.Widgets
import com.bkahlert.hello.widget.preview.FeaturePreview
import com.bkahlert.hello.widget.preview.FeaturePreviewWidget
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.storeOf
import io.ktor.util.decodeBase64String
import playground.components.bookmark.BOOKMARKS_EDGE_HTML_BASE64
import playground.components.bookmark.BOOKMARKS_FIREFOX_HTML_BASE64
import playground.components.bookmark.BOOKMARKS_START_ME_HTML_BASE64
import playground.components.bookmark.BookmarkFormat
import playground.components.bookmark.BookmarkTreeNode
import playground.components.bookmark.BookmarksStyle
import playground.components.bookmark.BookmarksWidget

val PlaygroundContainer = SimplePage(
    id = "playground",
    label = "Playground",
    description = "A place to play around with UI elements",
    heroIcon = HeroIcons::beaker,
) {
    val store: SyncStore<List<Widget>> = syncStoreOf(
        storeOf(
            listOf(
                BookmarksWidget(
                    "bookmarks-tabs",
                    bookmarks = bookmarkNodes(),
                    style = BookmarksStyle.TabStyle,
                ),
                BookmarksWidget(
                    "bookmarks-list",
                    bookmarks = bookmarkNodes(),
                    style = BookmarksStyle.ListStyle,
                ),
                BookmarksWidget(
                    "bookmarks-1",
                    bookmarks = BookmarkFormat.NetscapeBookmarksFileFormat.parse(BOOKMARKS_START_ME_HTML_BASE64.decodeBase64String()),
                    style = BookmarksStyle.TabStyle,
                ),
                BookmarksWidget(
                    "bookmarks-2",
                    bookmarks = BookmarkFormat.NetscapeBookmarksFileFormat.parse(BOOKMARKS_FIREFOX_HTML_BASE64.decodeBase64String())
                ),
                BookmarksWidget(
                    "bookmarks-3",
                    bookmarks = BookmarkFormat.NetscapeBookmarksFileFormat.parse(BOOKMARKS_EDGE_HTML_BASE64.decodeBase64String())
                ),
                FeaturePreviewWidget("xxx", feature = FeaturePreview.chatbot, AspectRatio.stretch),
            )
        )
    )
    Widgets(store, DefaultWidgetRegistration.apply {
        register<BookmarksWidget>(
            "bookmarks",
            title = "Bookmarks",
            description = "Manage, import and export bookmarks",
            icon = SolidHeroIcons.bookmark_square,
        )
    }).render(this)
}

// TODO new Page "Bookmarks Widget"
private fun bookmarkNodes(
    folders: Int = 10,
    bookmarks: Int = 10,
    level: List<Int> = emptyList(),
    folderTitle: (List<Int>) -> String? = { "Folder ${it.joinToString("-")}" },
    bookmarkTitle: (List<Int>) -> String? = { "Bookmark ${it.joinToString("-")}" },
    bookmarkUri: (List<Int>) -> Uri? = { Uri("https://example.com/#${it.joinToString("-")}") },
): List<BookmarkTreeNode> = buildList {
    repeat(folders) {
        add(
            BookmarkTreeNode.Folder(
                title = folderTitle(level + it),
                children = bookmarkNodes(
                    folders = folders.floorDiv(3),
                    bookmarks = bookmarks,
                    level = level + it,
                    folderTitle = folderTitle,
                    bookmarkTitle = bookmarkTitle,
                    bookmarkUri = bookmarkUri,
                )
            )
        )
    }
    repeat(bookmarks) {
        add(
            BookmarkTreeNode.Bookmark(
                title = bookmarkTitle(level + it),
                uri = bookmarkUri(level + it),
                icon = null,
                dateAdded = Now,
                lastModified = null,
            )
        )
    }
}
