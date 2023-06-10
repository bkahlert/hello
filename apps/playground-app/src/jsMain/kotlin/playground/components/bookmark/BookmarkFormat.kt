package playground.components.bookmark

import com.bkahlert.kommons.uri.toUriOrNull
import kotlinx.browser.document
import kotlinx.datetime.Instant
import org.w3c.dom.DocumentFragment
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.asList

public enum class BookmarkFormat {

    NetscapeBookmarksFileFormat {

        override fun hasFormat(content: String): Boolean = content.startsWith("<!DOCTYPE NETSCAPE-Bookmark-file-1>")

        override fun parse(content: String): List<BookmarkTreeNode> {
            require(hasFormat(content)) { "Illegal format" }
            val fragment = DocumentFragment()
            val parent = document.createElement("div") as HTMLDivElement
            fragment.append(parent)
            parent.innerHTML = content
            return from(parent.children.asList())
        }

        private fun from(elements: List<Element>): List<BookmarkTreeNode> = elements
            .filter { element -> element.tagName == "DL" }
            .flatMap { element -> element.children.asList().filter { it.tagName == "DT" } }
            .mapNotNull { from(it) }

        private fun from(element: Element): BookmarkTreeNode? = element
            .also { check(it.tagName == "DT") { "DT element expected but was ${element.tagName}" } }
            .children
            .asList()
            .let {
                val curr = it.getOrNull(0)
                when (curr?.tagName) {
                    "H3" -> BookmarkTreeNode.Folder(
                        title = curr.textContent,
                        children = from(it.drop(1))
                    )

                    "A" -> BookmarkTreeNode.Bookmark(
                        title = curr.textContent,
                        uri = curr.getAttribute("href")?.toUriOrNull(),
                        icon = curr.getAttribute("icon")?.toUriOrNull(),
                        dateAdded = curr.getAttribute("add_date")?.toLongOrNull()?.let(Instant.Companion::fromEpochSeconds),
                        lastModified = curr.getAttribute("last_modified")?.toLongOrNull()?.let(Instant.Companion::fromEpochSeconds),
                    )

                    else -> null
                }
            }
    },
    ;

    public abstract fun hasFormat(content: String): Boolean
    public abstract fun parse(content: String): List<BookmarkTreeNode>

    public companion object {
        public fun of(content: String): BookmarkFormat? = values().firstOrNull { it.hasFormat(content) }
        public fun parse(content: String): List<BookmarkTreeNode> = requireNotNull(of(content)?.parse(content)) {
            "Unsupported format. Supported formats are: ${values().joinToString { it.name }}"
        }
    }
}
