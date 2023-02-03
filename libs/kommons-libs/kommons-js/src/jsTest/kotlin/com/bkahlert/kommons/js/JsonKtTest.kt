package com.bkahlert.kommons.js

import io.kotest.matchers.shouldBe
import kotlin.js.json
import kotlin.test.Test

class JsonKtTest {

    @Test
    fun json() {
        val jsonFromMap = json(mapOf("foo" to "bar", "baz" to null))
        val jsonFromPairs = json("foo" to "bar", "baz" to null)
        JSON.stringify(jsonFromMap) shouldBe JSON.stringify(jsonFromPairs)
    }
}
