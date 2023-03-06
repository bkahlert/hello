package com.bkahlert.kommons

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class Base64KtTest {

    @Test
    fun encode_base_64_url() = runTest {
        val s = "LCa0a2j_xo_5m0U8HTBBNBNCLXBkg7-g-YpeiGJm564"
        sha256("foo").encodeBase64Url() shouldBe s
    }
}
