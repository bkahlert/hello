package com.bkahlert.aws.lambda

import com.amazonaws.services.lambda.runtime.LambdaLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class TestLogger(
    private val log: MutableList<String>,
    private val logger: Logger = LoggerFactory.getLogger(TestLogger::class.java),
) : LambdaLogger {

    override fun log(message: String) {
        logger.info(message.also { log.add(it) })
    }

    override fun log(message: ByteArray) {
        logger.info(String(message).also { log.add(it) })
    }
}
