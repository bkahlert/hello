package com.bkahlert.kommons.dom

import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class StorageTest {

    @Test
    fun scoped() {
        val storage = InMemoryStorage()
        val scopedStorage = storage.scoped("foo")

        scopedStorage["bar"] = "baz"

        storage should {
            it.keys shouldContainExactly listOf("foo.bar")
            it["foo.bar"] shouldBe "baz"
        }
    }
}
