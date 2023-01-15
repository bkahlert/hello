package com.bkahlert.hello.client

import io.kotest.assertions.throwables.shouldNotThrowAny
import kotlin.test.Test

class HelloClientTest {

    @Test
    fun instantiation() {
        shouldNotThrowAny {
            HelloClient.Anonymous(emptyMap())
        }
    }
}
