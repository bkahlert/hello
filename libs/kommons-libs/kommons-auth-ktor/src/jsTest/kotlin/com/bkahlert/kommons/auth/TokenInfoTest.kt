package com.bkahlert.kommons.auth

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class TokenInfoTest {

    @Test
    fun bearer_tokens() {
        tokenInfo.bearerTokens should {
            it.shouldNotBeNull()
            it.accessToken shouldBe "my-access-token"
            it.refreshToken shouldBe "my-refresh-token"
        }
    }
}

val tokenInfo = TokenInfo("my-access-token", "my-refresh-token", "my-id-token", "Bearer", 3600)
