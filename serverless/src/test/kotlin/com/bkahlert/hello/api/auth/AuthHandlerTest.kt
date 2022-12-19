package com.bkahlert.hello.api.auth

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.bkahlert.hello.aws.TestContext
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AuthHandlerTest {

    @Test
    fun `decode JWT`() {
        val authorization =
            "Bearer eyJraWQiOiJTdGw2ZmdCeG5OSGdXUFdzU0M5bTVQODdOZ0tGb0tPbXlcL1JDRlNkSElCOD0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkMmY4NzQ1Ni1lZDAyLTQ5YWYtOGRlNC05NmI5ZTYyN2QyNzAiLCJjb2duaXRvOmdyb3VwcyI6WyJldS1jZW50cmFsLTFfMmtjR01xbmVFX1NpZ25JbldpdGhBcHBsZSJdLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuZXUtY2VudHJhbC0xLmFtYXpvbmF3cy5jb21cL2V1LWNlbnRyYWwtMV8ya2NHTXFuZUUiLCJ2ZXJzaW9uIjoyLCJjbGllbnRfaWQiOiI3bGhkYnYxMnExdWQ5cmdnN2c3Nzl1OHZhNyIsIm9yaWdpbl9qdGkiOiJjZDZmNTk5MS0wN2M4LTRjNGQtOWRlMS0yMWU0YmE3YzIxMWYiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6Im9wZW5pZCIsImF1dGhfdGltZSI6MTY3MTQxNjM0NiwiZXhwIjoxNjcxNDE5OTQ2LCJpYXQiOjE2NzE0MTYzNDYsImp0aSI6IjhmYzMzMmU1LWFiNTItNDhiMC1iOWE0LTdhNDc4Mzk5ZDc1MyIsInVzZXJuYW1lIjoiU2lnbkluV2l0aEFwcGxlXzAwMDM0MC4zZjZlOTM3YTM2Yjg0Y2FhYWEyYTgxN2VhN2Q1ZTY5Yy4wMzMxIn0.ZES8WkuoRB7d7_19Y6eede2vZUTb-ACt76txvKkA6c0pOMNXlWJBzoGbxIZw3VGKfaA1BjhFJH6ow_rbzHYuyf6vWLZitHxIQfQtmvHB1XXGLwyhqseH5OgimSBdSuJMPVk2KT8RQXSN1LK3huc9yBNzDGqjCiquEha0MIzORiXZNgedqZvN-PWX-vM3_5lkXBYJGsCO18n7PBorvuOKXwutV3UYb0Lu0rkIQWOzxSk6TRtE6aAimUANqsGc8wKcwKCW3tXCvXrq7Kdx9zTdRlUgBaRLIXE-_Xk14HQWG7ZjoiyyQRC7-BwMTwjvzDluTvRVJ2D8v3HWZQwmI3pquw"

        val handler = AuthHandler()

        val response = handler.handleRequest(
            APIGatewayV2HTTPEvent.builder()
                .withHeaders(mapOf("authorization" to authorization))
                .withRouteKey("GET /user")
                .build(), TestContext
        )

        response.body shouldBe "{\"sub\":\"d2f87456-ed02-49af-8de4-96b9e627d270\",\"cognito:groups\":[\"eu-central-1_2kcGMqneE_SignInWithApple\"],\"iss\":\"https:\\/\\/cognito-idp.eu-central-1.amazonaws.com\\/eu-central-1_2kcGMqneE\",\"version\":2,\"client_id\":\"7lhdbv12q1ud9rgg7g779u8va7\",\"origin_jti\":\"cd6f5991-07c8-4c4d-9de1-21e4ba7c211f\",\"token_use\":\"access\",\"scope\":\"openid\",\"auth_time\":1671416346,\"exp\":1671419946,\"iat\":1671416346,\"jti\":\"8fc332e5-ab52-48b0-b9a4-7a478399d753\",\"username\":\"SignInWithApple_000340.3f6e937a36b84caaaa2a817ea7d5e69c.0331\"}"
    }
}
