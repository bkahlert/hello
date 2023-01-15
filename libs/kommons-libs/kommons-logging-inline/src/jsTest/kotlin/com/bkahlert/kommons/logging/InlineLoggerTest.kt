package com.bkahlert.kommons.logging

import io.kotest.assertions.throwables.shouldNotThrowAny
import kotlin.test.Test

class InlineLoggerTest {

    @Test
    fun instantiation() {
        shouldNotThrowAny {
            val logger = InlineLogger("TestLogger")
            logger.info("info message")
        }
    }
}
