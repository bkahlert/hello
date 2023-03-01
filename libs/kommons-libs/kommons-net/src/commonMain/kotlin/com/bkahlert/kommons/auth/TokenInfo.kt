package com.bkahlert.kommons.auth

import com.bkahlert.kommons.auth.JsonWebTokenPayload.IdTokenPayload
import com.bkahlert.kommons.time.DurationAsSeconds
import com.bkahlert.kommons.time.DurationAsSecondsSerializer
import com.bkahlert.kommons.time.toMomentString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TokenInfo(
    @SerialName("token_type") val tokenType: String, // e.g. "Bearer"
    @SerialName("scope") val scope: String? = null,
    @SerialName("expires_in") @Serializable(DurationAsSecondsSerializer::class) val expiresIn: DurationAsSeconds, // e.g. 3600
    @SerialName("id_token") val idToken: IdToken? = null,
    @SerialName("access_token") val accessToken: AccessToken,
    @SerialName("refresh_token") val refreshToken: RefreshToken? = null, // only if grant_type was authorization_code
) {
    public companion object
}

public val TokenInfo.diagnostics: Map<String, String?>
    get() = buildMap {
        put("Token type", tokenType)
        put("Scope", scope)
        put("Validity", expiresIn.toMomentString(descriptive = false))
        put("ID token", idToken?.run { "$truncated (${payload.expiresInDescription})" })
        put("Access token", accessToken.run { "$truncated (${payload.expiresInDescription})" })
        put("Refresh token", refreshToken?.run { "$truncated (encrypted)" })
    }

public val IdToken.diagnostics: Map<String, String?>
    get() = buildMap {
        put("ID token", truncated)
        putAll(payload.diagnostics)
    }

public val IdTokenPayload.diagnostics: Map<String, String?>
    get() = buildMap {
        put("Subject", subjectIdentifier)
        put("Issuer", issuerIdentifier.toString())
        put("Audiences", audiences.joinToString())
        put("Validity", "$expiresInDescription / $issuedAgoDescription / $authenticatedAgoDescription")
    }
