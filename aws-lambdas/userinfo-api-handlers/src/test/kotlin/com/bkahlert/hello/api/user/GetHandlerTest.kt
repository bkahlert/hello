package com.bkahlert.hello.api.user

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.tests.annotations.Event
import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.UrlJwkProvider
import com.bkahlert.aws.lambda.TestContext
import com.bkahlert.hello.user.info.GetHandler
import com.bkahlert.hello.user.info.JsonWebTokenValidator
import com.bkahlert.kommons.fixed
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.params.ParameterizedTest

class GetHandlerTest {

    @ParameterizedTest
    @Event(value = "events/get/id-token.json", type = APIGatewayProxyRequestEvent::class)
    fun `should validate ID token`(event: APIGatewayProxyRequestEvent, context: TestContext) {
        val handler = GetHandler(TestJsonWebTokenValidator())
        val response = handler.handleRequest(event, context)
        response.statusCode shouldBe 200
        response.body shouldBe "{\"userId\":\"d2f87456-ed02-49af-8de4-96b9e627d270\"}"
    }

    @ParameterizedTest
    @Event(value = "events/get/access-token.json", type = APIGatewayProxyRequestEvent::class)
    fun `should validate access token`(event: APIGatewayProxyRequestEvent, context: TestContext) {
        val handler = GetHandler(TestJsonWebTokenValidator())
        val response = handler.handleRequest(event, context)
        response.statusCode shouldBe 200
        response.body shouldBe "{\"userId\":\"d2f87456-ed02-49af-8de4-96b9e627d270\"}"
    }

    @ParameterizedTest
    @Event(value = "events/get/refresh-token.json", type = APIGatewayProxyRequestEvent::class)
    fun `should reject refresh token`(event: APIGatewayProxyRequestEvent, context: TestContext) {
        val handler = GetHandler(TestJsonWebTokenValidator())
        val response = handler.handleRequest(event, context)
        response.statusCode shouldBe 401
        response.body shouldBe "{\"message\":\"The token was expected to have 3 parts, but got > 3.\"}"
    }
}

fun Jwk(vararg values: Pair<String, Any?>): Jwk = Jwk.fromValues(values.toMap())
fun JwkProvider(vararg jwks: Jwk): JwkProvider = object : UrlJwkProvider("example.com") {
    override fun getAll(): List<Jwk> = jwks.asList()
}

fun TestJsonWebTokenValidator(
    jwkProvider: JwkProvider = TestTokens.JwkProvider,
    issuer: String = TestTokens.IdentityProviderUri,
    clientId: String = TestTokens.ClientId,
    now: Instant = TestTokens.ExpiresAt,
): JsonWebTokenValidator = JsonWebTokenValidator(
    jwkProvider = jwkProvider,
    issuer = issuer,
    clientId = clientId,
    clock = Clock.fixed(now),
)

@Suppress("LongLine", "SpellCheckingInspection")
object TestTokens {
    val JwkProvider = JwkProvider(
        Jwk(
            "alg" to "RS256",
            "e" to "AQAB",
            "kid" to "P6J03gNxCArOBHsJNvBfW9xZsLjG9kJ0DU8lx4ZMz0Q=",
            "kty" to "RSA",
            "n" to "vW29P2Ho3PcjwZW9xdX9FIJ-8kC7V8DWMD5Wn_On18BlY92XwMXac3F-g-VJjkVWg3l2KSkHynl1NsKFBGzLA4TDAKMvevScIRCSTb4qPo3DXTyZCFRgKTEN0IDsLsVd4-FtqpIsRYwfe_oWqhTeVdgpVvkLS6j5LoZJCgquX6h8VNc4F2S92P8oFX57ndGj4myZgxMUaaqRm3zie-zv4dKM2hG0Qbowm_W0u7ASYqyc75lJpUMXAQyWjOqJUWKYT9_uoQtHyleL-GTjmJQ7FrnKrvz-_Tje24HQn6zCO8fIVwO1hRv-zKVx0Rg766NJjryLIX_c1JPiWjcqFt9rVQ",
            "use" to "sig"
        ),
        Jwk(
            "alg" to "RS256",
            "e" to "AQAB",
            "kid" to "Stl6fgBxnNHgWPWsSC9m5P87NgKFoKOmy/RCFSdHIB8=",
            "kty" to "RSA",
            "n" to "uCrjwk6YdzoWNaX-yeFTFQOrqUio6NgQo551Mk_XoRl890BAGjVjf4HhmnKEPsddQEVPcKRsAdmUmXiQzuAQs3RNvd925z_jhZcwxCXAJ3UmgitDzFyJOQJE3N9f3rEkU-aJ352R_rXVbdsYy6yvqeonFFFtVh9O_RKXOJfLTFBkheFG9jd-J4IiDxHkO-wJMA8LkRAG-3RvWrAxBEHiOQMOoDO8qTo8a-8R17ISWyvEURe-XZ5JZT7VMkHr0_wTUZrptkpbmqMJo7ClrXMAajReKSwh9_rz-b5Fs9qEavACu9tE-snN2mcoiip-ZK97dJE4ETSGjVALJfdsC205jQ",
            "use" to "sig"
        ),
    )
    const val IdentityProviderUri = "https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_2kcGMqneE"
    val ExpiresAt = Instant.fromEpochSeconds(1671507141)
    const val ClientId = "7lhdbv12q1ud9rgg7g779u8va7"
    const val IdToken =
        "eyJraWQiOiJQNkowM2dOeENBck9CSHNKTnZCZlc5eFpzTGpHOWtKMERVOGx4NFpNejBRPSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoiREE3N0tjUFN3NU5PSjFjVldiTGs1QSIsInN1YiI6ImQyZjg3NDU2LWVkMDItNDlhZi04ZGU0LTk2YjllNjI3ZDI3MCIsImNvZ25pdG86Z3JvdXBzIjpbImV1LWNlbnRyYWwtMV8ya2NHTXFuZUVfU2lnbkluV2l0aEFwcGxlIl0sImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5ldS1jZW50cmFsLTEuYW1hem9uYXdzLmNvbVwvZXUtY2VudHJhbC0xXzJrY0dNcW5lRSIsImNvZ25pdG86dXNlcm5hbWUiOiJTaWduSW5XaXRoQXBwbGVfMDAwMzQwLjNmNmU5MzdhMzZiODRjYWFhYTJhODE3ZWE3ZDVlNjljLjAzMzEiLCJvcmlnaW5fanRpIjoiNDNjMzY5ZGMtN2MyNi00Y2UxLWFmYTktMDEyY2RiNGQ5OGYyIiwiYXVkIjoiN2xoZGJ2MTJxMXVkOXJnZzdnNzc5dTh2YTciLCJpZGVudGl0aWVzIjpbeyJ1c2VySWQiOiIwMDAzNDAuM2Y2ZTkzN2EzNmI4NGNhYWFhMmE4MTdlYTdkNWU2OWMuMDMzMSIsInByb3ZpZGVyTmFtZSI6IlNpZ25JbldpdGhBcHBsZSIsInByb3ZpZGVyVHlwZSI6IlNpZ25JbldpdGhBcHBsZSIsImlzc3VlciI6bnVsbCwicHJpbWFyeSI6InRydWUiLCJkYXRlQ3JlYXRlZCI6IjE2NzE0MTYzNDUzODUifV0sInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNjcxNTAzNTQxLCJleHAiOjE2NzE1MDcxNDEsImlhdCI6MTY3MTUwMzU0MSwianRpIjoiNDZlZDU2ZTgtNjE0NS00MTNjLTlkZDAtYjFkODlhODI1ZjQxIn0.n6biATx3WMzaov_X07rc8mbuzgDLD7F3C-ZKQkRuaWvRyKBoKeuOQHUTKTskZ67nJiuwzfd-zzgGZi5nSzu4tIibc4TdSJBltM6Uw4p_KZwi_EN9-wNsLHYD6ogMHUXwiqBp7Oex9EkYRjZhpMFkY49sFmF6D86_0Bgb6Eouma8m2YTR_rGA2IhkD9sz9-6KSH8oDVTrt-C3i0nLIZ9lXF6XANAU5e06XwLbwWzobYlYbQINUalVQPdDxGzhyDvc8H2yKKSvh8dX5Pcfk-Ha4IIFbtRXEbz_uholbgUBHJCHnD3hDQp2kNW7bpWI4jpZ7uio959VhAIb1JQzg2yCeg"
    const val AccessToken =
        "eyJraWQiOiJTdGw2ZmdCeG5OSGdXUFdzU0M5bTVQODdOZ0tGb0tPbXlcL1JDRlNkSElCOD0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkMmY4NzQ1Ni1lZDAyLTQ5YWYtOGRlNC05NmI5ZTYyN2QyNzAiLCJjb2duaXRvOmdyb3VwcyI6WyJldS1jZW50cmFsLTFfMmtjR01xbmVFX1NpZ25JbldpdGhBcHBsZSJdLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuZXUtY2VudHJhbC0xLmFtYXpvbmF3cy5jb21cL2V1LWNlbnRyYWwtMV8ya2NHTXFuZUUiLCJ2ZXJzaW9uIjoyLCJjbGllbnRfaWQiOiI3bGhkYnYxMnExdWQ5cmdnN2c3Nzl1OHZhNyIsIm9yaWdpbl9qdGkiOiI0M2MzNjlkYy03YzI2LTRjZTEtYWZhOS0wMTJjZGI0ZDk4ZjIiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6Im9wZW5pZCIsImF1dGhfdGltZSI6MTY3MTUwMzU0MSwiZXhwIjoxNjcxNTA3MTQxLCJpYXQiOjE2NzE1MDM1NDEsImp0aSI6IjQyMDc5OWIxLTBiNjYtNDE0ZC1iYzMyLWMyZWQzMDkxZTg5ZCIsInVzZXJuYW1lIjoiU2lnbkluV2l0aEFwcGxlXzAwMDM0MC4zZjZlOTM3YTM2Yjg0Y2FhYWEyYTgxN2VhN2Q1ZTY5Yy4wMzMxIn0.reLa3TfdP9D_Ie5d4Y8p8xVdzcMMYJlrGQmDJ3CTjPmo-1RBdRf2ImVDlOQHTiOmuBV0FnoGt2GNi7qHNGbjcUb3K-rm6ymWELGGwkEPTX3NkXgby8iAhYXnw1D-hb4pSWVU3zb5Pf5I-vMJQW5WO4N2Z-jVZB8khP0GiYYovY1Qg6vsKGXRqMxCi6nqPYCMvlJ7HPSgC79rKEgN6exrcLiuElVUrfXsigjlHLmu2MqvjigCgqMWMrevvZBp8JQ3qPIij-8N7xNydoDk2Hprv9C8IVdgE9_PesmwPEGJ8AiSjtDK9yWqLSvFRP-qzUecH6XovA-6FipPbXJ2UWv9eg"
    const val RefreshToken =
        "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.CcskL1dML80XK3YAuNYSqAaib5AyBvfKaKqO5IJJr-98VS3LFmZxrAeZAyF13086FMCtqt5rVtqLE7JTg53T791DMyPOY-xZC02ZMrZ30_wdNtz0OUR_Z4r5ZVfm9ocqyrZmbiNLamYMZgzhkHzbDYynFJNrH6bMvtEA3Ikh6ZZuAWw15SPYdI_fQy6zq422Y8kUr10vUfCKkE4BM3n03sLPLU4A-sAVlbK_0mHeC6jjL1jBv0ecnbanUzqnwv_E5KBXHjqGjzP2Dzz9PRKgjn85HkGTcEnGC-8XkBx8avraYbBkyPT31ckdj2Xl9Y6ERAnaOYi0fu5fo2zV3COhwg.bKzIXQle05GTOgZo.xQcyZQhPbBCojXBoSn_ynNWLSb2Tv60UtzgLm4IdZM_A05rHOhWlQojqQohyCqjivLwiKbivqz5ZIWxue6jInFHF7miwHA_bXJ8bflZUrxYU1B3ClAJs47D4-r_oQSwvJOskO4fMSHsvMjwiFs5exyDkD_dmRbrhW_4bV35fS7AZDesb3uabK3-ZliaC-pFdMSEr6QX_eEwR0liFe0qUHG8p4lu_GDzBgMnxlKYyCDlBP7MfKaF2vIx9GYDVU1gmy6XhAlvLEFYhC25c6dNwxwxvtcevAwrZFKrapOSFRQfzGCxyrnXJlEwkMKwbMz0B_YN7JXnQ73sPDAWnFZZy6o_bZRc_R7XMdV0nMc090qySa73e_19UbrQSJCxpDNOU6wtcP9LyBTrDTjDBEf9_l7eLIE_OIwWWtmBudWnKRigtvmcE7-JeF5SRVTluNpSUKuLk-khSYFi7nQpjJWmzI1vqqubI0DpZA4XbxUpXlJyEyVyvmUwbIOQvMLTWHQJM04wDXrQ8jDLNPt0gUbJfz80s9tW9jt3z0j39exjosPSfqHKcagqYg4XTqt3XhRKX9PnJjU-lTBL1KMcYjbhpoAfoWGZVtRDm1PWj1Hp_m3TTvg_UJBQfAaHX_XWJVkxFK9d4j8hTE4ViAg8ue6T99xKd2BgNL1TCfRaNLdydsN1aBS76zVUF7nvjw0GEAIGwoR9vyPRCZEnu_nvlxbpNv2ziryjvMlmKkJ-TAyodA8XxLP_EaTkC8TrXEHU9l5wQEOJLW_Xa03CD8cHXlO2nPpz0ytAbiN_0TQxM-xEO57DpZj5yVrp_90o03eU-qYqAp0jjziWsAb5V2rJXffaG2Yh9XNxKZ0O-911Vi-2ihZWzMro6Gdl5Z3YmmquvVLLmEuM6msoLfOXBPsvAr2IeF7QYHmk_8x4h8JN7Ue3zX_ZE65zMWrPosPsq_EO_L3J1kdN1ZYpQ3wfW_Pc196EHLIqF3-8gzRS2xZ2q9fJBVS0onetwG9heDWNrbCbsY3wODxkWXJTsDAKOHaGn2JdOga1YlSpNi0B-Pz-rKguvTXUhBbE7iqgK111mEABkr2uZGRgzGlVh-280Iv94SCjVmLo5HZx4tngWujHLbRHmcgpffYpuvdyKpBqaA9kFe6CfZQcgNJfd-FIA5XUF_7cLXCdotUrUFmnEOeFGToFUe1u1JdyA1SMRtRChb4kmgJPpH3bR8mF90-ETp6SXHGtDElyaETMs7fxaBkMrjXiYh_4SZCZETc7HUtypF9yT8SRNhg2y.IjX_f703NWo0MxEBsCrFWA"
}
