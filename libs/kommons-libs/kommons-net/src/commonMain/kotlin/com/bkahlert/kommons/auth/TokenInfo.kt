package com.bkahlert.kommons.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null, // only if grant_type was authorization_code
    @SerialName("id_token") val idToken: String? = null,
    @SerialName("token_type") val tokenType: String, // e.g. "Bearer"
    @SerialName("expires_in") val expiresIn: Int, // e.g. 3600
)
