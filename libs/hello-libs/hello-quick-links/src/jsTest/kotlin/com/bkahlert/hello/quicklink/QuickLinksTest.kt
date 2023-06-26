package com.bkahlert.hello.quicklink

import com.bkahlert.hello.bookmark.BookmarkTreeNode
import com.bkahlert.hello.fritz2.TestSyncStore
import com.bkahlert.hello.fritz2.runTest
import com.bkahlert.kommons.uri.Uri
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.delay
import kotlin.test.Test

class QuickLinksTest {

    @Test
    fun instantiate_empty() = runTest {
        val propsStore = TestSyncStore(Bookmarks(0))
        val quickLinks = QuickLinks(propsStore)
        quickLinks.current.shouldBeEmpty()
    }

    @Test
    fun instantiate_non_empty() = runTest {
        val propsStore = TestSyncStore(Bookmarks(1))
        val quickLinks = QuickLinks(propsStore)
        quickLinks.current shouldContainExactly listOf(Bookmark(0))
    }


    @Test
    fun add_to_empty() = runTest {
        val propsStore = TestSyncStore(Bookmarks(0))
        val quickLinks = QuickLinks(propsStore)

        quickLinks.addOrUpdate(Bookmark(1))
        delay(50)

        quickLinks.current shouldContainExactly listOf(Bookmark(1))
    }

    @Test
    fun add_to_non_empty() = runTest {
        val propsStore = TestSyncStore(Bookmarks(1))
        val quickLinks = QuickLinks(propsStore)

        quickLinks.addOrUpdate(Bookmark(1))
        delay(50)

        quickLinks.current shouldContainExactly listOf(Bookmark(0), Bookmark(1))
    }
}

fun Bookmarks(index: Int) = (0..index).map { Bookmark(index) }

fun Bookmark(index: Int) = BookmarkTreeNode.Bookmark(
    id = "quick-link-$index",
    title = "Quick Link $index",
    url = Uri("https://example.com/quick-link/$index"),
)
