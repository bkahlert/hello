package com.bkahlert.hello.clickup.client.http

import com.bkahlert.kommons.ktor.AuthorizationToken
import com.bkahlert.kommons.ktor.Token

public class PersonalAccessToken(
    public val token: String,
) : Token by AuthorizationToken(token) {

    init {
        require(REGEX.matches(token)) { "token must match $REGEX" }
    }

    public companion object {
        public val REGEX: Regex = Regex("pk_\\d+_\\w+")
    }
}
