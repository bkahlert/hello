package com.bkahlert.kommons.js

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotStartWith
import kotlin.test.Test

class StringKtTest {

    @Test
    fun to_string() = testAll {

        val aFunction: () -> Unit = {}
        aFunction.toString() shouldNotStartWith "custom implementation"

        aFunction.toString { "custom implementation" }
        aFunction.toString() shouldBe "custom implementation"
    }
}
