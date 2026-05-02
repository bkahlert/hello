package com.bkahlert.hello.clickup

import com.bkahlert.kommons.auth.Token
import kotlinx.serialization.Serializable

@Serializable
public value class PersonalAccessToken(
    public override val token: String,
) : Token {

    init {
        require(REGEX.matches(token)) { "token must match $REGEX" }
    }

    public companion object {
        public val REGEX: Regex = Regex("pk_\\d+_\\w+")
    }
}
