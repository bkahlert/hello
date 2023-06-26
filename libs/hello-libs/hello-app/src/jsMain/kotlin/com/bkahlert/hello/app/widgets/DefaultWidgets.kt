package com.bkahlert.hello.app.widgets

import com.bkahlert.hello.bookmark.BookmarkTreeNode
import com.bkahlert.hello.bookmark.BookmarksWidget
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.image.ImageWidget
import com.bkahlert.hello.widget.preview.FeaturePreview
import com.bkahlert.hello.widget.preview.FeaturePreviewWidget
import com.bkahlert.hello.widget.video.VideoWidget
import com.bkahlert.hello.widget.website.WebsiteWidget
import com.bkahlert.kommons.uri.Uri

public val DefaultWidgets: List<Widget> by lazy {
    buildList {
        add(
            ImageWidget(
                id = "nyan-cat",
                title = Bookmarks.nyanCat.title,
                src = Bookmarks.nyanCat.url,
                aspectRatio = AspectRatio.video
            )
        )
        add(
            VideoWidget(
                id = "rick-astley",
                title = "Rick Astley",
                src = Uri("https://www.youtube.com/embed/dQw4w9WgXcQ"),
            )
        )
        add(
            WebsiteWidget(
                id = "impossible-color",
                title = Bookmarks.impossibleColor.title,
                src = Bookmarks.impossibleColor.url,
                aspectRatio = AspectRatio.stretch,
            )
        )
        add(
            BookmarksWidget(
                id = "bookmarks",
                bookmarks = listOf(
                    BookmarkTreeNode.Folder(
                        title = "Favorites",
                        children = listOf(Bookmarks.nyanCat, Bookmarks.rickAstley),
                    ),
                    Bookmarks.impossibleColor,
                ),
            )
        )
        FeaturePreview.values().mapTo(this) {
            FeaturePreviewWidget(
                id = "feature-preview-${it.name}",
                feature = it,
                aspectRatio = AspectRatio.stretch,
            )
        }
    }
}

private object Bookmarks {
    val nyanCat = BookmarkTreeNode.Bookmark(
        url = Uri("https://raw.githubusercontent.com/bkahlert/-/master/nyancat.svg"),
    )
    val rickAstley = BookmarkTreeNode.Bookmark(
        title = "Rick Astley",
        url = Uri("https://www.youtube.com/watch?v=dQw4w9WgXcQ"),
    )
    val impossibleColor = BookmarkTreeNode.Bookmark(
        title = "Impossible color",
        url = Uri("https://en.wikipedia.org/wiki/Impossible_color"),
        icon = Uri("https://en.wikipedia.org/favicon.ico"),
    )
}
