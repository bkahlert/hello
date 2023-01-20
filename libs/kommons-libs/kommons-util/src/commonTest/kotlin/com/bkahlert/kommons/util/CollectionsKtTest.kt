package com.bkahlert.kommons.util

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test

class CollectionsKtTest {

    @Test
    fun predecessor() = testAll {
        list.predecessor { false }.shouldBeEmpty()
        list.predecessor { it == "foo" }.shouldContainExactly("baz")
        list.predecessor { it == "bar" }.shouldContainExactly("foo")
        list.predecessor { it == "baz" }.shouldContainExactly("bar")
        list.predecessor { it != "bar" }.shouldContainExactly("baz", "bar")
        list.predecessor { true }.shouldContainExactly("baz", "foo", "bar")
        listOf("foo", "bar").predecessor { true }.shouldContainExactly("bar", "foo")
        listOf("foo").predecessor { true }.shouldContainExactly("foo")
        emptyList<String>().predecessor { true }.shouldBeEmpty()
    }

    @Test
    fun successor() = testAll {
        list.successor { false }.shouldBeEmpty()
        list.successor { it == "foo" }.shouldContainExactly("bar")
        list.successor { it == "bar" }.shouldContainExactly("baz")
        list.successor { it == "baz" }.shouldContainExactly("foo")
        list.successor { it != "bar" }.shouldContainExactly("bar", "foo")
        list.successor { true }.shouldContainExactly("bar", "baz", "foo")
        listOf("foo", "bar").successor { true }.shouldContainExactly("bar", "foo")
        listOf("foo").successor { true }.shouldContainExactly("foo")
        emptyList<String>().successor { true }.shouldBeEmpty()
    }
}

val list = listOf("foo", "bar", "baz")
