package com.bkahlert.hello.clickup.client.http

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable

@Serializable
public value class AccessToken(
    public val token: String,
) {
    init {
        require(REGEX.matches(token)) { "token must match $REGEX" }
    }

    public fun configure(context: HttpRequestBuilder) {
        context.headers[HttpHeaders.Authorization] = token
    }

    public companion object {
        public val REGEX: Regex = Regex("pk_\\d+_\\w+")
    }
}
