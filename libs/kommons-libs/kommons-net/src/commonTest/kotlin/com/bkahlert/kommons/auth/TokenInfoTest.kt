package com.bkahlert.kommons.auth

import com.bkahlert.kommons.auth.JsonWebTokenPayload.IdTokenPayload
import com.bkahlert.kommons.test.testAll
import com.bkahlert.kommons.time.InstantAsEpochSeconds
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.uri.Uri
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TokenInfoTest {

    @Test
    fun serialize() {
        Json.encodeToString(TokenInfo.TOKEN_INFO) shouldBe TOKEN_INFO_STRING
    }

    @Test
    fun deserialize() {
        Json.decodeFromString<TokenInfo>(TOKEN_INFO_STRING) shouldBe TokenInfo.TOKEN_INFO
    }

    @Test
    fun diagnostics() = testAll {
        IdTokenTestPayload().diagnostics should {
            it.keys.shouldContainExactlyInAnyOrder("Subject", "Issuer", "Audiences", "Validity")
            it["Subject"] shouldBe "d2f87456-ed02-49af-8de4-96b9e627d270"
            it["Issuer"] shouldBe "https://provider.example.com/test"
            it["Audiences"] shouldBe "made-up-client_id"
            it["Validity"] shouldBe "expires in 15m / issued 45m ago / authenticated 5d 1h ago"
        }
    }
}

val TokenInfo.Companion.TOKEN_INFO
    get() = TokenInfo(
        tokenType = "Bearer",
        scope = "openid",
        expiresIn = 3600.seconds,
        idToken = ID_TOKEN,
        accessToken = ACCESS_TOKEN,
        refreshToken = REFRESH_TOKEN,
    )

const val TOKEN_INFO_STRING =
    """{"token_type":"Bearer","scope":"openid","expires_in":3600,"id_token":"$ENCODED_ID_TOKEN","access_token":"$ENCODED_ACCESS_TOKEN","refresh_token":"$ENCODED_REFRESH_TOKEN"}"""

fun IdTokenTestPayload(
    subjectIdentifier: String = "d2f87456-ed02-49af-8de4-96b9e627d270",
    issuerIdentifier: Uri = Uri.parse("https://provider.example.com/test"),
    audiences: List<String> = listOf("made-up-client_id"),
    expiresAt: InstantAsEpochSeconds = Now + 15.minutes,
    issuedAt: InstantAsEpochSeconds = expiresAt - 60.minutes,
    authenticatedAt: InstantAsEpochSeconds = issuedAt - 5.days,
    id: String = "46ed56e8-6145-413c-9dd0-b1d89a825f41",
    origin_jti: String = "43c369dc-7c26-4ce1-afa9-012cdb4d98f2",
): IdTokenPayload = IdTokenPayload(buildJsonObject {
    put(OpenIDStandardClaims.SUB_CLAIM_NAME, JsonPrimitive(subjectIdentifier))
    put("iss", JsonPrimitive(issuerIdentifier.toString()))
    put("aud", JsonArray(audiences.map { JsonPrimitive(it) }))
    put("exp", JsonPrimitive(expiresAt.epochSeconds))
    put("iat", JsonPrimitive(issuedAt.epochSeconds))
    put("auth_time", JsonPrimitive(authenticatedAt.epochSeconds))
    put("jti", JsonPrimitive(id))
    put("origin_jti", JsonPrimitive(origin_jti))
    put("token_use", JsonPrimitive("id"))
})
