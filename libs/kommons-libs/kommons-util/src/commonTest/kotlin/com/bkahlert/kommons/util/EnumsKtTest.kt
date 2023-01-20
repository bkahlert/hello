package com.bkahlert.kommons.util

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EnumsKtTest {

    @Test
    fun predecessor() = testAll {
        TestEnum.Foo.predecessor shouldBe TestEnum.Baz
        TestEnum.Bar.predecessor shouldBe TestEnum.Foo
        TestEnum.Baz.predecessor shouldBe TestEnum.Bar
    }

    @Test
    fun successor() = testAll {
        TestEnum.Foo.successor shouldBe TestEnum.Bar
        TestEnum.Bar.successor shouldBe TestEnum.Baz
        TestEnum.Baz.successor shouldBe TestEnum.Foo
    }
}

enum class TestEnum {
    Foo, Bar, Baz
}
