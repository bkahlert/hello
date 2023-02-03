package com.bkahlert.kommons.auth

import com.bkahlert.kommons.auth.JsonWebToken.Companion.usePart
import com.bkahlert.kommons.auth.JsonWebTokenPayload.AccessTokenPayload
import com.bkahlert.kommons.auth.JsonWebTokenPayload.IdTokenPayload
import com.bkahlert.kommons.json.LenientJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlin.jvm.JvmInline

public interface Token {

    /** Encoded token */
    public val token: String

    /** Truncated representation of this token. */
    public val truncated: String
        get() = when (token.length) {
            in 0..40 -> token.take(token.length / 2) + "…"
            else -> with(token) { take(15) + "…" + takeLast(5) }
        }
}

@JvmInline
@Serializable
public value class IdToken(public override val token: String) : JsonWebToken {
    /** Decoded ID token payload */
    public override val payload: IdTokenPayload
        get() = usePart(1) { LenientJson.decodeFromString(it) }

    override fun toString(): String = truncated
}

@JvmInline
@Serializable
public value class AccessToken(public override val token: String) : JsonWebToken {
    /** Decoded ID token payload */
    public override val payload: AccessTokenPayload
        get() = usePart(1) { LenientJson.decodeFromString(it) }

    override fun toString(): String = truncated
}

@JvmInline
@Serializable
public value class RefreshToken(public override val token: String) : Token {
    override fun toString(): String = truncated
}
