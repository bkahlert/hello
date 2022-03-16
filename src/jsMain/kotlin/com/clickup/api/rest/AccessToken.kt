package com.clickup.api.rest

import com.bkahlert.kommons.runtime.LocalStorage
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable

@Serializable
value class AccessToken(
    val token: String,
) {
//    init {
//        require(token.isNotBlank()) { "token must not be empty / blank" }
//    }

    fun configue(context: HttpRequestBuilder) {
        context.headers[HttpHeaders.Authorization] = token
    }

    fun save() {
        LocalStorage[ACCESS_TOKEN_STORAGE_KEY] = token
    }

    companion object {
        private const val ACCESS_TOKEN_STORAGE_KEY = "clickup.access-token"
        fun load() = LocalStorage[ACCESS_TOKEN_STORAGE_KEY]?.let { AccessToken(it) }
    }
}
