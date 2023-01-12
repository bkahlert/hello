package com.bkahlert.hello.clickup.client

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException

public class ClickUpException(
    error: ErrorInfo,
    cause: Throwable?,
) : IllegalStateException("[${error.ECODE}] ${error.err}", cause) {
    public companion object {
        /**
         * Returns a business exception with this exception as its cause if applicable.
         */
        public suspend fun Throwable.wrapOrNull(): ClickUpException? =
            (this as? ResponseException)?.let {
                kotlin.runCatching { ClickUpException(it.response.body(), it) }.getOrNull()
            }
    }
}
