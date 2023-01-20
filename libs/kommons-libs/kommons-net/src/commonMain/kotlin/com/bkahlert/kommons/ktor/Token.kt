package com.bkahlert.kommons.ktor

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * A token that can the used for authorization
 * in combination with [installTokenAuth].
 */
public interface Token {
    public fun configure(context: HttpRequestBuilder)
}

@JvmInline
@Serializable
public value class AuthorizationToken(
    private val authorization: String,
) : Token {
    override fun configure(context: HttpRequestBuilder) {
        context.headers[HttpHeaders.Authorization] = authorization
    }
}

public fun HttpClientConfig<HttpClientEngineConfig>.installTokenAuth(token: Token) {
    install("Token-Authorization") {
        plugin(HttpSend).intercept { context ->
            token.configure(context)
            execute(context)
        }
    }
}
