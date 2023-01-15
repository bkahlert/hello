package com.bkahlert.kommons.logging

import io.kotest.assertions.throwables.shouldNotThrowAny
import kotlin.test.Test

class InlineLoggingTest {

    @Test
    fun instantiation() {
        shouldNotThrowAny {
            val logger by InlineLogging
            logger.info("info message")
        }
    }
}
