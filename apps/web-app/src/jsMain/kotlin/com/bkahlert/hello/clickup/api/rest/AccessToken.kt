package com.bkahlert.hello.clickup.api.rest

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable

@Serializable
value class AccessToken(
    val token: String,
) {
    init {
        require(REGEX.matches(token)) { "token must match $REGEX" }
    }

    fun configure(context: HttpRequestBuilder) {
        context.headers[HttpHeaders.Authorization] = token
    }

    companion object {
        val REGEX = Regex("pk_\\d+_\\w+")
    }
}
