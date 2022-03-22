package com.clickup.api.rest

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable

@Serializable
value class AccessToken(
    val token: String,
) {
    init {
        require(token.isNotBlank()) { "token must not be empty / blank" }
    }

    fun configue(context: HttpRequestBuilder) {
        context.headers[HttpHeaders.Authorization] = token
    }
}
