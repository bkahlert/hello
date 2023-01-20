package com.bkahlert.kommons.ktor

import io.kotest.assertions.throwables.shouldNotThrowAny
import kotlin.test.Test

class TokenTest {

    @Test
    fun install() {
        shouldNotThrowAny {
            JsonHttpClient {
                installTokenAuth(com.bkahlert.kommons.ktor.authorizationToken)
            }
        }
    }
}

val authorizationToken = AuthorizationToken("foo-bar")
