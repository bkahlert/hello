package com.bkahlert.kommons.ktor

import io.kotest.assertions.throwables.shouldNotThrowAny
import kotlin.test.Test

class JsonHttpClientTest {

    @Test
    fun instantiation() {
        shouldNotThrowAny {
            JsonHttpClient()
        }
    }
}
