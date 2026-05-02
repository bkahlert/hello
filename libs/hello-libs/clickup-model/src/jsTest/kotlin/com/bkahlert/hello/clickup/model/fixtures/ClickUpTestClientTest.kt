package com.bkahlert.hello.clickup.model.fixtures

import io.kotest.assertions.throwables.shouldNotThrowAny
import kotlin.test.Test

class ClickUpTestClientTest {

    @Test
    fun instantiation() {
        shouldNotThrowAny { ClickUpTestClient() }
    }
}
