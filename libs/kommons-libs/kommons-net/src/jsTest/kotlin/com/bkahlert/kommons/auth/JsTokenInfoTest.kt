package com.bkahlert.kommons.auth

import com.bkahlert.kommons.oauth.bearerTokens
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class JsTokenInfoTest {

    @Test
    fun bearer_tokens() {
        TokenInfo.TOKEN_INFO.bearerTokens should {
            it.shouldNotBeNull()
            it.accessToken shouldBe ENCODED_ACCESS_TOKEN
            it.refreshToken shouldBe ENCODED_REFRESH_TOKEN
        }
    }
}
