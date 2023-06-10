package playground.components.bookmark

import com.bkahlert.kommons.uri.Uri
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("type")
public sealed interface BookmarkTreeNode {

    @Serializable
    @SerialName("folder")
    public data class Folder(
        public val title: String?,
        public val children: List<BookmarkTreeNode>,
    ) : BookmarkTreeNode

    @Serializable
    @SerialName("bookmark")
    public data class Bookmark(
        public val title: String?,
        public val uri: Uri?,
        public val icon: Uri?,
        public val dateAdded: Instant?,
        public val lastModified: Instant?,
    ) : BookmarkTreeNode
}

public fun List<BookmarkTreeNode>.mapBookmarks(block: (BookmarkTreeNode.Bookmark) -> BookmarkTreeNode.Bookmark): List<BookmarkTreeNode> =
    map { it.mapBookmarks(block) }

public fun BookmarkTreeNode.mapBookmarks(block: (BookmarkTreeNode.Bookmark) -> BookmarkTreeNode.Bookmark): BookmarkTreeNode =
    when (this) {
        is BookmarkTreeNode.Folder -> copy(children = children.mapBookmarks(block))
        is BookmarkTreeNode.Bookmark -> block(this)
    }
