package com.bkahlert.hello.bookmark

import com.bkahlert.hello.editor.UriLens
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.kommons.md5
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.Lens
import kotlinx.browser.window
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonNames

@Serializable
@JsonClassDiscriminator("type")
public sealed interface BookmarkTreeNode {

    @SerialName("id")
    public val id: String

    @SerialName("title")
    public val title: String?

    @SerialName("dateAdded")
    public val dateAdded: Instant?

    @SerialName("lastModified")
    public val lastModified: Instant?

    @Serializable
    @SerialName("folder")
    public data class Folder(
        override val title: String? = null,
        @SerialName("children") public val children: List<BookmarkTreeNode> = emptyList(),
        override val dateAdded: Instant? = null,
        override val lastModified: Instant? = null,
        @SerialName("personalToolbarFolder") public val personalToolbarFolder: Boolean? = null,
        override val id: String = title?.let { md5(it) } ?: randomString(),
    ) : BookmarkTreeNode

    @Serializable
    @SerialName("bookmark")
    public data class Bookmark(
        override val title: String? = null,
        @SerialName("url") @JsonNames("href") public val url: Uri? = null,
        @SerialName("icon") public val icon: Uri? = null,
        override val dateAdded: Instant? = null,
        override val lastModified: Instant? = null,
        override val id: String = title?.let { md5(it) } ?: icon?.let { md5(it.toString()) } ?: randomString(),
    ) : BookmarkTreeNode {

        public fun open() {
            url?.also { window.open(it.toString()) }
        }

        public companion object {
            public fun title(): Lens<Bookmark, String> =
                Bookmark::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

            public fun url(): Lens<Bookmark, String> =
                Bookmark::url.lens({ it.url }, { p, v -> p.copy(url = v?.toUriOrNull()) }) + UriLens

            public fun icon(): Lens<Bookmark, String> =
                Bookmark::icon.lens({ it.icon }, { p, v -> p.copy(icon = v?.toUriOrNull()) }) + UriLens
        }
    }
}

public fun List<BookmarkTreeNode>.mapBookmarks(block: (BookmarkTreeNode.Bookmark) -> BookmarkTreeNode.Bookmark): List<BookmarkTreeNode> =
    map { it.mapBookmarks(block) }

public fun BookmarkTreeNode.mapBookmarks(block: (BookmarkTreeNode.Bookmark) -> BookmarkTreeNode.Bookmark): BookmarkTreeNode =
    when (this) {
        is BookmarkTreeNode.Folder -> copy(children = children.mapBookmarks(block))
        is BookmarkTreeNode.Bookmark -> block(this)
    }
