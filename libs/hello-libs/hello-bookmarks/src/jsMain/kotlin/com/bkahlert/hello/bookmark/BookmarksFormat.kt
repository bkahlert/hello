package com.bkahlert.hello.bookmark

import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import kotlinx.browser.document
import kotlinx.datetime.Instant
import org.w3c.dom.DocumentFragment
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.asList

public enum class BookmarksFormat {

    NetscapeBookmarksFileFormat {

        private val HEADER_LINES = listOf(
            """<!DOCTYPE NETSCAPE-Bookmark-file-1>""",
            """<!-- This is an automatically generated file.""",
            """     It will be read and overwritten.""",
            """     DO NOT EDIT! -->""",
            """<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">""",
            """<TITLE>Bookmarks</TITLE>""",
            """<H1>Bookmarks</H1>""",
        )

        override fun hasFormat(content: String): Boolean = content.startsWith(HEADER_LINES.first())

        override fun read(content: String): List<BookmarkTreeNode> {
            require(hasFormat(content)) { "Illegal format" }
            val fragment = DocumentFragment()
            val parent = document.createElement("div") as HTMLDivElement
            fragment.append(parent)
            parent.innerHTML = content
            return from(parent.children.asList())
        }

        override fun write(nodes: List<BookmarkTreeNode>): String = buildString {
            HEADER_LINES.forEach { appendLine(it) }
            appendLine(writeNodes(nodes))
        }.replace("\n", "\r\n")

        private fun writeNodes(nodes: List<BookmarkTreeNode>): String = buildString {
            appendLine("<DL><p>")
            nodes.forEach { node ->
                appendLine(writeNode(node).prependIndent("    "))
            }
            append("</DL><p>")
        }

        private fun writeNode(node: BookmarkTreeNode) = buildString {
            when (node) {
                is BookmarkTreeNode.Folder -> {
                    append("<DT><H3")
                    node.dateAdded?.also { append(""" ADD_DATE="${it.epochSeconds}"""") }
                    node.lastModified?.also { append(""" LAST_MODIFIED="${it.epochSeconds}"""") }
                    node.personalToolbarFolder?.also { append(""" PERSONAL_TOOLBAR_FOLDER="$it"""") }
                    append(">")
                    append(node.title.orEmpty())
                    appendLine("</H3>")
                    append(writeNodes(node.children))
                }

                is BookmarkTreeNode.Bookmark -> {
                    append("<DT><A")
                    node.url?.also { append(""" HREF="$it"""") }
                    node.dateAdded?.also { append(""" ADD_DATE="${it.epochSeconds}"""") }
                    node.lastModified?.also { append(""" LAST_MODIFIED="${it.epochSeconds}"""") }
                    node.icon?.also { append(""" ICON="${it.toPaddedString()}"""") }
                    append(">")
                    append(node.title.orEmpty())
                    append("</A>")
                }
            }
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
                        children = from(it.drop(1)),
                        dateAdded = curr.getAttribute("add_date")?.toLongOrNull()?.let(Instant.Companion::fromEpochSeconds),
                        lastModified = curr.getAttribute("last_modified")?.toLongOrNull()?.let(Instant.Companion::fromEpochSeconds),
                        personalToolbarFolder = curr.getAttribute("personal_toolbar_folder")?.toBooleanStrictOrNull(),
                    )

                    "A" -> BookmarkTreeNode.Bookmark(
                        title = curr.textContent,
                        url = curr.getAttribute("href")?.toUriOrNull(),
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
    public abstract fun read(content: String): List<BookmarkTreeNode>
    public abstract fun write(nodes: List<BookmarkTreeNode>): String

    public companion object {
        public fun of(content: String): BookmarksFormat? = values().firstOrNull { it.hasFormat(content) }
        public fun read(content: String): List<BookmarkTreeNode> = requireNotNull(of(content)?.read(content)) {
            "Unsupported format. Supported formats are: ${values().joinToString { it.name }}"
        }
    }
}

private fun Uri.toPaddedString(): String = when (this) {
    is DataUri -> toString().let {
        "$it${
            when (it.substringAfterLast("base64,", "").length.mod(4)) {
                2 -> "=="
                3 -> "="
                else -> ""
            }
        }"
    }

    else -> toString()
}
