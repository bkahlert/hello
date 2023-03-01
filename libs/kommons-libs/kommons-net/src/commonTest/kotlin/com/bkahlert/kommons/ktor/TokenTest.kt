package com.bkahlert.kommons.ktor

import com.bkahlert.kommons.auth.Token
import io.kotest.assertions.throwables.shouldNotThrowAny
import kotlin.test.Test

class TokenTest {

    @Test
    fun install() {
        shouldNotThrowAny {
            JsonHttpClient {
                install(TokenAuthPlugin) { token = authorizationToken }
            }
        }
    }
}

val authorizationToken = object : Token {
    override val token: String get() = "foo-bar"
}
