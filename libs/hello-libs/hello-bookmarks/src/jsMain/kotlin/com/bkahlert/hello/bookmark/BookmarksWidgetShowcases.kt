package com.bkahlert.hello.bookmark

import com.bkahlert.hello.icon.assets.Images
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.page.SimplePage
import com.bkahlert.hello.showcase.showcase
import com.bkahlert.hello.showcase.showcases
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.Tag
import org.w3c.dom.HTMLDivElement

public object BookmarksWidgetShowcases : SimplePage(
    "bookmarks-widget",
    "Bookmarks Widget",
    "Bookmarks widget showcases",
    heroIcon = HeroIcons::bookmark_square,
    content = {
        bookmarkShowcases(
            "Few bookmarks", SolidHeroIcons.bars_2, listOf(
                BookmarkTreeNode.Folder(
                    "Folder", listOf(
                        BookmarkTreeNode.Bookmark(
                            title = "Bookmark title",
                            url = Uri("https://example.com"),
                            icon = SolidHeroIcons.heart,
                            dateAdded = Now,
                            lastModified = Now,
                        ),
                        BookmarkTreeNode.Bookmark(
                            title = "L${"o".repeat(20)}ng bookmark title",
                            url = Uri("https://example.com#long"),
                            icon = SolidHeroIcons.ellipsis_horizontal,
                            dateAdded = Now,
                            lastModified = Now,
                        ),
                    )
                ),
                BookmarkTreeNode.Bookmark(
                    title = null,
                    url = Uri("https://example.com#no-title"),
                    icon = SolidHeroIcons.question_mark_circle,
                    dateAdded = Now,
                    lastModified = Now,
                ),
                BookmarkTreeNode.Bookmark(
                    title = "No URL",
                    url = null,
                    icon = SolidHeroIcons.bookmark_slash,
                    dateAdded = Now,
                    lastModified = Now,
                ),
                BookmarkTreeNode.Bookmark(
                    title = null,
                    url = null,
                    icon = SolidHeroIcons.stop,
                    dateAdded = Now,
                    lastModified = Now,
                ),
                BookmarkTreeNode.Bookmark(
                    title = "No icon",
                    url = Uri("https://example.com#no-icon"),
                    icon = null,
                    dateAdded = Now,
                    lastModified = Now,
                ),
                BookmarkTreeNode.Bookmark(
                    title = "No dates",
                    url = Uri("https://example.com#long"),
                    icon = SolidHeroIcons.calendar,
                    dateAdded = null,
                    lastModified = null,
                ),
                BookmarkTreeNode.Bookmark(
                    title = "Bookmarklet",
                    url = Uri("javascript:alert('Hello World!')"),
                    icon = SolidHeroIcons.code_bracket_square,
                    dateAdded = Now,
                    lastModified = Now,
                ),
            )
        )

        hr { }

        bookmarkShowcases("Many bookmarks", SolidHeroIcons.bars_4, bookmarkNodes(folders = 5, bookmarks = 10))
//
//            showcase("With user") {
//                BookmarksWidget(
//                    "bookmarks-tabs",
//                    bookmarks = bookmarkNodes(),
//                    style = BookmarksStyle.TabStyle,
//                ),
//                BookmarksWidget(
//                    "bookmarks-list",
//                    bookmarks = bookmarkNodes(),
//                    style = BookmarksStyle.ListStyle,
//                ),
//                BookmarksWidget(
//                    "bookmarks-1",
//                    bookmarks = BookmarksFormat.NetscapeBookmarksFileFormat.parse(BOOKMARKS_START_ME_HTML_BASE64.decodeBase64String()),
//                    style = BookmarksStyle.TabStyle,
//                ),
//                BookmarksWidget(
//                    "bookmarks-2",
//                    bookmarks = BookmarksFormat.NetscapeBookmarksFileFormat.parse(BOOKMARKS_FIREFOX_HTML_BASE64.decodeBase64String())
//                ),
//                BookmarksWidget(
//                    "bookmarks-3",
//                    bookmarks = BookmarksFormat.NetscapeBookmarksFileFormat.parse(BOOKMARKS_EDGE_HTML_BASE64.decodeBase64String())
//                ),

        hr { }
    },
)

private fun Tag<HTMLDivElement>.bookmarkShowcases(
    name: String,
    icon: DataUri,
    nodes: List<BookmarkTreeNode>
) {
    showcases(name, icon) {
        BookmarksStyle.values().forEach { style ->
            showcase(style.name, classes = "max-h-96") {
                with(style) { render(nodes) }
            }
        }
    }
}

private fun bookmarkNodes(
    folders: Int = 10,
    bookmarks: Int = 10,
    level: List<Int> = emptyList(),
    folderTitle: (List<Int>) -> String? = { "Folder ${it.joinToString("-")}" },
    bookmark: (List<Int>) -> BookmarkTreeNode.Bookmark = {
        BookmarkTreeNode.Bookmark(
            title = "Bookmark ${it.joinToString("-")}",
            url = Uri("https://example.com/#${it.joinToString("-")}"),
            icon = (Images + null).random(),
            dateAdded = Now,
            lastModified = null,
        )
    },
): List<BookmarkTreeNode> = buildList {
    repeat(folders) {
        add(
            BookmarkTreeNode.Folder(
                title = folderTitle(level + it),
                children = bookmarkNodes(
                    folders = folders.floorDiv(it + 2),
                    bookmarks = bookmarks,
                    level = level + it,
                    folderTitle = folderTitle,
                    bookmark = bookmark,
                )
            )
        )
    }
    repeat(bookmarks) {
        add(bookmark(level + it))
    }
}
