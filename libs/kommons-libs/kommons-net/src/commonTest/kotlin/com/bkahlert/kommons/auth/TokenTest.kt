package com.bkahlert.kommons.auth

import com.bkahlert.kommons.auth.JsonWebTokenPayload.AccessTokenPayload
import com.bkahlert.kommons.auth.JsonWebTokenPayload.IdTokenPayload
import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.test.testAll
import com.bkahlert.kommons.uri.Uri
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.test.Test

class TokenTest {

    @Test
    fun serialization() = testAll {
        Json.encodeToString(IdToken.serializer(), ID_TOKEN) shouldBe ENCODED_ID_TOKEN.quoted
        Json.encodeToString(AccessToken.serializer(), ACCESS_TOKEN) shouldBe ENCODED_ACCESS_TOKEN.quoted
        Json.encodeToString(RefreshToken.serializer(), REFRESH_TOKEN) shouldBe ENCODED_REFRESH_TOKEN.quoted
    }

    @Test
    fun truncated() = testAll(
        ID_TOKEN to "eyJraWQiOiJQNko…2yCeg",
        ACCESS_TOKEN to "eyJraWQiOiJTdGw…Wv9eg",
        REFRESH_TOKEN to "eyJjdHkiOiJKV1Q…CrFWA",
    ) { (value, truncated) ->
        value.truncated shouldBe truncated
        value.toString() shouldBe truncated
    }

    @Test
    fun decoded_id_token() {
        ID_TOKEN.payload should {
            it.issuerIdentifier shouldBe Uri.parse("https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_2kcGMqneE")
            it.subjectIdentifier shouldBe "d2f87456-ed02-49af-8de4-96b9e627d270"
            it.audiences.shouldContainExactly("7lhdbv12q1ud9rgg7g779u8va7")
            it.expiresAt shouldBe Instant.fromEpochSeconds(1671507141)
            it.issuedAt shouldBe Instant.fromEpochSeconds(1671503541)
            it.authenticatedAt shouldBe Instant.fromEpochSeconds(1671503541)
            it.nonce shouldBe null
            it.authenticationContextClassReference shouldBe null
            it.authenticationMethodsReferences shouldBe null
            it.authorizedParty shouldBe null
            it.tokenUse shouldBe "id"
            it.cognitoGroups.shouldContainExactly("eu-central-1_2kcGMqneE_SignInWithApple")
            it.cognitoUsername shouldBe "SignInWithApple_000340.3f6e937a36b84caaaa2a817ea7d5e69c.0331"
            it.identities.shouldContainExactly(
                Identity(
                    userId = "000340.3f6e937a36b84caaaa2a817ea7d5e69c.0331",
                    providerName = "SignInWithApple",
                    providerType = "SignInWithApple",
                    issuer = null,
                    primary = true,
                    dateCreated = Instant.fromEpochSeconds(1671416345385),
                ),
            )
            it.id shouldBe "46ed56e8-6145-413c-9dd0-b1d89a825f41"
            it.origin_jti shouldBe "43c369dc-7c26-4ce1-afa9-012cdb4d98f2"

            it shouldBe LenientJson.decodeFromString(IdTokenPayload.serializer(), DECODED_ID_TOKEN)
        }
    }

    @Test
    fun decoded_access_token() {
        ACCESS_TOKEN.payload should {
            it.issuerIdentifier shouldBe Uri.parse("https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_2kcGMqneE")
            it.subjectIdentifier shouldBe "d2f87456-ed02-49af-8de4-96b9e627d270"
            it.expiresAt shouldBe Instant.fromEpochSeconds(1671507141)
            it.issuedAt shouldBe Instant.fromEpochSeconds(1671503541)
            it.authenticatedAt shouldBe Instant.fromEpochSeconds(1671503541)
            it.client_id shouldBe "7lhdbv12q1ud9rgg7g779u8va7"
            it.scope shouldBe "openid"
            it.tokenUse shouldBe "access"
            it.username shouldBe "SignInWithApple_000340.3f6e937a36b84caaaa2a817ea7d5e69c.0331"
            it.version shouldBe 2
            it.cognitoGroups.shouldContainExactly("eu-central-1_2kcGMqneE_SignInWithApple")
            it.id shouldBe "420799b1-0b66-414d-bc32-c2ed3091e89d"
            it.origin_jti shouldBe "43c369dc-7c26-4ce1-afa9-012cdb4d98f2"
            it shouldBe LenientJson.decodeFromString(AccessTokenPayload.serializer(), DECODED_ACCESS_TOKEN)
        }
    }
}


val ID_TOKEN: IdToken get() = IdToken(ENCODED_ID_TOKEN)
val ACCESS_TOKEN: AccessToken get() = AccessToken(ENCODED_ACCESS_TOKEN)
val REFRESH_TOKEN: RefreshToken get() = RefreshToken(ENCODED_REFRESH_TOKEN)

const val ENCODED_ID_TOKEN: String = "" +
    "eyJraWQiOiJQNkowM2dOeENBck9CSHNKTnZCZlc5eFpzTGpHOWtKMERVOGx4NFpNejBRPSIs" +
    "ImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoiREE3N0tjUFN3NU5PSjFjVldiTGs1QSIsInN1" +
    "YiI6ImQyZjg3NDU2LWVkMDItNDlhZi04ZGU0LTk2YjllNjI3ZDI3MCIsImNvZ25pdG86Z3Jv" +
    "dXBzIjpbImV1LWNlbnRyYWwtMV8ya2NHTXFuZUVfU2lnbkluV2l0aEFwcGxlIl0sImlzcyI6" +
    "Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5ldS1jZW50cmFsLTEuYW1hem9uYXdzLmNvbVwvZXUt" +
    "Y2VudHJhbC0xXzJrY0dNcW5lRSIsImNvZ25pdG86dXNlcm5hbWUiOiJTaWduSW5XaXRoQXBw" +
    "bGVfMDAwMzQwLjNmNmU5MzdhMzZiODRjYWFhYTJhODE3ZWE3ZDVlNjljLjAzMzEiLCJvcmln" +
    "aW5fanRpIjoiNDNjMzY5ZGMtN2MyNi00Y2UxLWFmYTktMDEyY2RiNGQ5OGYyIiwiYXVkIjoi" +
    "N2xoZGJ2MTJxMXVkOXJnZzdnNzc5dTh2YTciLCJpZGVudGl0aWVzIjpbeyJ1c2VySWQiOiIw" +
    "MDAzNDAuM2Y2ZTkzN2EzNmI4NGNhYWFhMmE4MTdlYTdkNWU2OWMuMDMzMSIsInByb3ZpZGVy" +
    "TmFtZSI6IlNpZ25JbldpdGhBcHBsZSIsInByb3ZpZGVyVHlwZSI6IlNpZ25JbldpdGhBcHBs" +
    "ZSIsImlzc3VlciI6bnVsbCwicHJpbWFyeSI6InRydWUiLCJkYXRlQ3JlYXRlZCI6IjE2NzE0" +
    "MTYzNDUzODUifV0sInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNjcxNTAzNTQxLCJl" +
    "eHAiOjE2NzE1MDcxNDEsImlhdCI6MTY3MTUwMzU0MSwianRpIjoiNDZlZDU2ZTgtNjE0NS00" +
    "MTNjLTlkZDAtYjFkODlhODI1ZjQxIn0.n6biATx3WMzaov_X07rc8mbuzgDLD7F3C-ZKQkRu" +
    "aWvRyKBoKeuOQHUTKTskZ67nJiuwzfd-zzgGZi5nSzu4tIibc4TdSJBltM6Uw4p_KZwi_EN9" +
    "-wNsLHYD6ogMHUXwiqBp7Oex9EkYRjZhpMFkY49sFmF6D86_0Bgb6Eouma8m2YTR_rGA2Ihk" +
    "D9sz9-6KSH8oDVTrt-C3i0nLIZ9lXF6XANAU5e06XwLbwWzobYlYbQINUalVQPdDxGzhyDvc" +
    "8H2yKKSvh8dX5Pcfk-Ha4IIFbtRXEbz_uholbgUBHJCHnD3hDQp2kNW7bpWI4jpZ7uio959V" +
    "hAIb1JQzg2yCeg"

// language=json
const val DECODED_ID_TOKEN = """{
    "at_hash": "DA77KcPSw5NOJ1cVWbLk5A",
    "sub": "d2f87456-ed02-49af-8de4-96b9e627d270",
    "cognito:groups": [
      "eu-central-1_2kcGMqneE_SignInWithApple"
    ],
    "iss": "https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_2kcGMqneE",
    "cognito:username": "SignInWithApple_000340.3f6e937a36b84caaaa2a817ea7d5e69c.0331",
    "origin_jti": "43c369dc-7c26-4ce1-afa9-012cdb4d98f2",
    "aud": "7lhdbv12q1ud9rgg7g779u8va7",
    "identities": [
      {
        "userId": "000340.3f6e937a36b84caaaa2a817ea7d5e69c.0331",
        "providerName": "SignInWithApple",
        "providerType": "SignInWithApple",
        "issuer": null,
        "primary": "true",
        "dateCreated": "1671416345385"
      }
    ],
    "token_use": "id",
    "auth_time": 1671503541,
    "exp": 1671507141,
    "iat": 1671503541,
    "jti": "46ed56e8-6145-413c-9dd0-b1d89a825f41"
}
"""

const val ENCODED_ACCESS_TOKEN = "" +
    "eyJraWQiOiJTdGw2ZmdCeG5OSGdXUFdzU0M5bTVQODdOZ0tGb0tPbXlcL1JDRlNkSElCOD0i" +
    "LCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkMmY4NzQ1Ni1lZDAyLTQ5YWYtOGRlNC05NmI5ZTY" +
    "yN2QyNzAiLCJjb2duaXRvOmdyb3VwcyI6WyJldS1jZW50cmFsLTFfMmtjR01xbmVFX1NpZ25" +
    "JbldpdGhBcHBsZSJdLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuZXUtY2VudHJhbC0" +
    "xLmFtYXpvbmF3cy5jb21cL2V1LWNlbnRyYWwtMV8ya2NHTXFuZUUiLCJ2ZXJzaW9uIjoyLCJ" +
    "jbGllbnRfaWQiOiI3bGhkYnYxMnExdWQ5cmdnN2c3Nzl1OHZhNyIsIm9yaWdpbl9qdGkiOiI" +
    "0M2MzNjlkYy03YzI2LTRjZTEtYWZhOS0wMTJjZGI0ZDk4ZjIiLCJ0b2tlbl91c2UiOiJhY2N" +
    "lc3MiLCJzY29wZSI6Im9wZW5pZCIsImF1dGhfdGltZSI6MTY3MTUwMzU0MSwiZXhwIjoxNjc" +
    "xNTA3MTQxLCJpYXQiOjE2NzE1MDM1NDEsImp0aSI6IjQyMDc5OWIxLTBiNjYtNDE0ZC1iYzM" +
    "yLWMyZWQzMDkxZTg5ZCIsInVzZXJuYW1lIjoiU2lnbkluV2l0aEFwcGxlXzAwMDM0MC4zZjZ" +
    "lOTM3YTM2Yjg0Y2FhYWEyYTgxN2VhN2Q1ZTY5Yy4wMzMxIn0.reLa3TfdP9D_Ie5d4Y8p8xV" +
    "dzcMMYJlrGQmDJ3CTjPmo-1RBdRf2ImVDlOQHTiOmuBV0FnoGt2GNi7qHNGbjcUb3K-rm6ym" +
    "WELGGwkEPTX3NkXgby8iAhYXnw1D-hb4pSWVU3zb5Pf5I-vMJQW5WO4N2Z-jVZB8khP0GiYY" +
    "ovY1Qg6vsKGXRqMxCi6nqPYCMvlJ7HPSgC79rKEgN6exrcLiuElVUrfXsigjlHLmu2Mqvjig" +
    "CgqMWMrevvZBp8JQ3qPIij-8N7xNydoDk2Hprv9C8IVdgE9_PesmwPEGJ8AiSjtDK9yWqLSv" +
    "FRP-qzUecH6XovA-6FipPbXJ2UWv9eg"

// language=json
const val DECODED_ACCESS_TOKEN = """{
    "sub": "d2f87456-ed02-49af-8de4-96b9e627d270",
    "cognito:groups": [
        "eu-central-1_2kcGMqneE_SignInWithApple"
    ],
    "iss": "https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_2kcGMqneE",
    "version": 2,
    "client_id": "7lhdbv12q1ud9rgg7g779u8va7",
    "origin_jti": "43c369dc-7c26-4ce1-afa9-012cdb4d98f2",
    "token_use": "access",
    "scope": "openid",
    "auth_time": 1671503541,
    "exp": 1671507141,
    "iat": 1671503541,
    "jti": "420799b1-0b66-414d-bc32-c2ed3091e89d",
    "username": "SignInWithApple_000340.3f6e937a36b84caaaa2a817ea7d5e69c.0331"
}    
"""

const val ENCODED_REFRESH_TOKEN = "" +
    "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.CcskL1dML" +
    "80XK3YAuNYSqAaib5AyBvfKaKqO5IJJr-98VS3LFmZxrAeZAyF13086FMCtqt5rVtqLE7JTg" +
    "53T791DMyPOY-xZC02ZMrZ30_wdNtz0OUR_Z4r5ZVfm9ocqyrZmbiNLamYMZgzhkHzbDYynF" +
    "JNrH6bMvtEA3Ikh6ZZuAWw15SPYdI_fQy6zq422Y8kUr10vUfCKkE4BM3n03sLPLU4A-sAVl" +
    "bK_0mHeC6jjL1jBv0ecnbanUzqnwv_E5KBXHjqGjzP2Dzz9PRKgjn85HkGTcEnGC-8XkBx8a" +
    "vraYbBkyPT31ckdj2Xl9Y6ERAnaOYi0fu5fo2zV3COhwg.bKzIXQle05GTOgZo.xQcyZQhPb" +
    "BCojXBoSn_ynNWLSb2Tv60UtzgLm4IdZM_A05rHOhWlQojqQohyCqjivLwiKbivqz5ZIWxue" +
    "6jInFHF7miwHA_bXJ8bflZUrxYU1B3ClAJs47D4-r_oQSwvJOskO4fMSHsvMjwiFs5exyDkD" +
    "_dmRbrhW_4bV35fS7AZDesb3uabK3-ZliaC-pFdMSEr6QX_eEwR0liFe0qUHG8p4lu_GDzBg" +
    "MnxlKYyCDlBP7MfKaF2vIx9GYDVU1gmy6XhAlvLEFYhC25c6dNwxwxvtcevAwrZFKrapOSFR" +
    "QfzGCxyrnXJlEwkMKwbMz0B_YN7JXnQ73sPDAWnFZZy6o_bZRc_R7XMdV0nMc090qySa73e_" +
    "19UbrQSJCxpDNOU6wtcP9LyBTrDTjDBEf9_l7eLIE_OIwWWtmBudWnKRigtvmcE7-JeF5SRV" +
    "TluNpSUKuLk-khSYFi7nQpjJWmzI1vqqubI0DpZA4XbxUpXlJyEyVyvmUwbIOQvMLTWHQJM0" +
    "4wDXrQ8jDLNPt0gUbJfz80s9tW9jt3z0j39exjosPSfqHKcagqYg4XTqt3XhRKX9PnJjU-lT" +
    "BL1KMcYjbhpoAfoWGZVtRDm1PWj1Hp_m3TTvg_UJBQfAaHX_XWJVkxFK9d4j8hTE4ViAg8ue" +
    "6T99xKd2BgNL1TCfRaNLdydsN1aBS76zVUF7nvjw0GEAIGwoR9vyPRCZEnu_nvlxbpNv2zir" +
    "yjvMlmKkJ-TAyodA8XxLP_EaTkC8TrXEHU9l5wQEOJLW_Xa03CD8cHXlO2nPpz0ytAbiN_0T" +
    "QxM-xEO57DpZj5yVrp_90o03eU-qYqAp0jjziWsAb5V2rJXffaG2Yh9XNxKZ0O-911Vi-2ih" +
    "ZWzMro6Gdl5Z3YmmquvVLLmEuM6msoLfOXBPsvAr2IeF7QYHmk_8x4h8JN7Ue3zX_ZE65zMW" +
    "rPosPsq_EO_L3J1kdN1ZYpQ3wfW_Pc196EHLIqF3-8gzRS2xZ2q9fJBVS0onetwG9heDWNrb" +
    "CbsY3wODxkWXJTsDAKOHaGn2JdOga1YlSpNi0B-Pz-rKguvTXUhBbE7iqgK111mEABkr2uZG" +
    "RgzGlVh-280Iv94SCjVmLo5HZx4tngWujHLbRHmcgpffYpuvdyKpBqaA9kFe6CfZQcgNJfd-" +
    "FIA5XUF_7cLXCdotUrUFmnEOeFGToFUe1u1JdyA1SMRtRChb4kmgJPpH3bR8mF90-ETp6SXH" +
    "GtDElyaETMs7fxaBkMrjXiYh_4SZCZETc7HUtypF9yT8SRNhg2y.IjX_f703NWo0MxEBsCrF" +
    "WA"
