package com.bkahlert.hello.clickup.rest

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.serialization.Serializable

class ClickUpException(
    error: ErrorInfo,
    cause: Throwable?,
) : IllegalStateException("[${error.ECODE}] ${error.err}", cause) {
    companion object {
        /**
         * Returns a business exception with this exception as its cause if applicable.
         */
        suspend fun Throwable.wrapOrNull(): ClickUpException? =
            (this as? ResponseException)?.let {
                kotlin.runCatching { ClickUpException(it.response.body(), it) }.getOrNull()
            }
    }
}

@Serializable
data class ErrorInfo(
    val err: String,
    val ECODE: String,
)
