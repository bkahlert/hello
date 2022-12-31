package com.bkahlert.hello.aws

import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.bkahlert.kommons.logging.SLF4J

object TestLogger : LambdaLogger {

    private val logger by SLF4J

    override fun log(message: String) {
        logger.info(message)
    }

    override fun log(message: ByteArray) {
        logger.info(message.decodeToString())
    }
}
