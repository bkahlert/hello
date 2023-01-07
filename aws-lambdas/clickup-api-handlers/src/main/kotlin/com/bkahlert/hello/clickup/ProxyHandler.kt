package com.bkahlert.hello.clickup

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.caseInsensitiveHeaders
import com.bkahlert.aws.lambda.requiredUserId
import com.bkahlert.aws.lambda.response
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ProxyHandler : APIGatewayProxyRequestEventHandler() {
    private val clickupUrl by lazy { System.getenv("CLICKUP_URL") }
    private val clickupApiToken by lazy { System.getenv("CLICKUP_API_TOKEN") }
    private val clickupClientId by lazy { System.getenv("CLICKUP_CLIENT_ID") }
    private val clickupClientSecret by lazy { System.getenv("CLICKUP_CLIENT_SECRET") }

    private val apiEndpoint = "https://hello.aws-dev.choam.de/api"
    private val userPropsApiEndpoint = "$apiEndpoint/user-props"
    private val clickupReturnUrlPath by lazy { "/oauth2/idpresponse" }
    private val clickupReturnUrl by lazy { "$apiEndpoint/clickup$clickupReturnUrlPath" }

//    {resource: /,path: /,httpMethod: GET,headers: {Accept-Encoding=gzip, Authorization=Bearer eyJraWQiOiJpZEpNQkQ1S0dpUDMzVU02M0paXC9IS1J3VDU1ZVVSNHZxczdubmM0UnFQbz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJiZTdhNWQzZi01OGIxLTRjYTktYTQ0NS1jODI4MDA2ZmM3NDEiLCJjb2duaXRvOmdyb3VwcyI6WyJ1cy1lYXN0LTFfbzNyQXR3cFg1X1NpZ25JbldpdGhBcHBsZSJdLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9vM3JBdHdwWDUiLCJ2ZXJzaW9uIjoyLCJjbGllbnRfaWQiOiI2ODc0ZHBuaWd0cGI5dWh0N29zZjBpam42YiIsIm9yaWdpbl9qdGkiOiJkMzVkYTgxZi1mYmI5LTQxZTktYmE3ZC05ZGU3YTljNzQ5NzUiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6Im9wZW5pZCBlbWFpbCIsImF1dGhfdGltZSI6MTY3MjYzMzIyNiwiZXhwIjoxNjcyODgwNDA1LCJpYXQiOjE2NzI4NzY4MDUsImp0aSI6Ijk1OGJjNTE3LTFiODMtNGVlNC04YmQ2LWZhZjYwMmZhMjVmMiIsInVzZXJuYW1lIjoiU2lnbkluV2l0aEFwcGxlXzAwMDM0MC4zZjZlOTM3YTM2Yjg0Y2FhYWEyYTgxN2VhN2Q1ZTY5Yy4wMzMxIn0.ki2OnuUlqZ2vQPTr8Cx2NbH5JptGksgS8DXK7RHMdZgnPCQlH6t0pL1k9-x9Ahr629hyBhFSTWeS3IMVcAta0vWTdP69m25SUyaHAN6kHV8Kf2LCfCJ8ZCD5alFz_pAuA8RmBPWoajlZsTMTS5wIjzx30Nz1zTjTopOvpQta7uT9gX4zbPQr2_ryuTZFC-OW02d_xi-_5q2n_x0j8zR2SDWhP4U9RqVlOSleZqQjoE45vlH8zX7UsBE4eXUyFbkWhMWWpPHz2NS5ri0wdGA8VX9H21Czvkt1ipLWucLd1KPjep9cwI-FJkzOiSwnGC8wjl1zE2reWSK6FUy0oF5Dkg, Cache-Control=no-cache, CloudFront-Forwarded-Proto=https, CloudFront-Is-Desktop-Viewer=true, CloudFront-Is-Mobile-Viewer=false, CloudFront-Is-SmartTV-Viewer=false, CloudFront-Is-Tablet-Viewer=false, CloudFront-Viewer-ASN=16509, CloudFront-Viewer-Country=US, Dnt=1, Host=doiot27raj.execute-api.us-east-1.amazonaws.com, Pragma=no-cache, Sec-Fetch-Dest=empty, Sec-Fetch-Mode=cors, Sec-Fetch-Site=same-origin, User-Agent=Amazon CloudFront, Via=2.0 e026b2802d48048e9935caadbecf124e.cloudfront.net (CloudFront), 1.1 f7aba4a0337c5f98c4703e2b10f1940a.cloudfront.net (CloudFront), X-Amz-Cf-Id=aWDC0_g0S9xDc4SocCYLDdxkBIoKdUn-QyuMV4CqgGuLJcQVNjyP9w==, X-Amzn-Trace-Id=Root=1-63b61a17-6d9fdcfb44d443cc73cbc5f6, X-Forwarded-For=87.123.214.22, 130.176.161.211, 130.176.208.239, X-Forwarded-Port=443, X-Forwarded-Proto=https},multiValueHeaders: {Accept-Encoding=[gzip], Authorization=[Bearer eyJraWQiOiJpZEpNQkQ1S0dpUDMzVU02M0paXC9IS1J3VDU1ZVVSNHZxczdubmM0UnFQbz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJiZTdhNWQzZi01OGIxLTRjYTktYTQ0NS1jODI4MDA2ZmM3NDEiLCJjb2duaXRvOmdyb3VwcyI6WyJ1cy1lYXN0LTFfbzNyQXR3cFg1X1NpZ25JbldpdGhBcHBsZSJdLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9vM3JBdHdwWDUiLCJ2ZXJzaW9uIjoyLCJjbGllbnRfaWQiOiI2ODc0ZHBuaWd0cGI5dWh0N29zZjBpam42YiIsIm9yaWdpbl9qdGkiOiJkMzVkYTgxZi1mYmI5LTQxZTktYmE3ZC05ZGU3YTljNzQ5NzUiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6Im9wZW5pZCBlbWFpbCIsImF1dGhfdGltZSI6MTY3MjYzMzIyNiwiZXhwIjoxNjcyODgwNDA1LCJpYXQiOjE2NzI4NzY4MDUsImp0aSI6Ijk1OGJjNTE3LTFiODMtNGVlNC04YmQ2LWZhZjYwMmZhMjVmMiIsInVzZXJuYW1lIjoiU2lnbkluV2l0aEFwcGxlXzAwMDM0MC4zZjZlOTM3YTM2Yjg0Y2FhYWEyYTgxN2VhN2Q1ZTY5Yy4wMzMxIn0.ki2OnuUlqZ2vQPTr8Cx2NbH5JptGksgS8DXK7RHMdZgnPCQlH6t0pL1k9-x9Ahr629hyBhFSTWeS3IMVcAta0vWTdP69m25SUyaHAN6kHV8Kf2LCfCJ8ZCD5alFz_pAuA8RmBPWoajlZsTMTS5wIjzx30Nz1zTjTopOvpQta7uT9gX4zbPQr2_ryuTZFC-OW02d_xi-_5q2n_x0j8zR2SDWhP4U9RqVlOSleZqQjoE45vlH8zX7UsBE4eXUyFbkWhMWWpPHz2NS5ri0wdGA8VX9H21Czvkt1ipLWucLd1KPjep9cwI-FJkzOiSwnGC8wjl1zE2reWSK6FUy0oF5Dkg], Cache-Control=[no-cache], CloudFront-Forwarded-Proto=[https], CloudFront-Is-Desktop-Viewer=[true], CloudFront-Is-Mobile-Viewer=[false], CloudFront-Is-SmartTV-Viewer=[false], CloudFront-Is-Tablet-Viewer=[false], CloudFront-Viewer-ASN=[16509], CloudFront-Viewer-Country=[US], Dnt=[1], Host=[doiot27raj.execute-api.us-east-1.amazonaws.com], Pragma=[no-cache], Sec-Fetch-Dest=[empty], Sec-Fetch-Mode=[cors], Sec-Fetch-Site=[same-origin], User-Agent=[Amazon CloudFront], Via=[2.0 e026b2802d48048e9935caadbecf124e.cloudfront.net (CloudFront), 1.1 f7aba4a0337c5f98c4703e2b10f1940a.cloudfront.net (CloudFront)], X-Amz-Cf-Id=[aWDC0_g0S9xDc4SocCYLDdxkBIoKdUn-QyuMV4CqgGuLJcQVNjyP9w==], X-Amzn-Trace-Id=[Root=1-63b61a17-6d9fdcfb44d443cc73cbc5f6], X-Forwarded-For=[87.123.214.22, 130.176.161.211, 130.176.208.239], X-Forwarded-Port=[443], X-Forwarded-Proto=[https]},requestContext: {accountId: 382728805609,resourceId: ijs5dt4tvh,stage: prod,requestId: 3006f709-0b97-4360-a3c3-7b2aacc46057,identity: {sourceIp: 130.176.161.211,userAgent: Amazon CloudFront,},resourcePath: /,httpMethod: GET,apiId: doiot27raj,path: /prod/,authorizer: {claims={sub=be7a5d3f-58b1-4ca9-a445-c828006fc741, cognito:groups=us-east-1_o3rAtwpX5_SignInWithApple, iss=https://cognito-idp.us-east-1.amazonaws.com/us-east-1_o3rAtwpX5, version=2, client_id=6874dpnigtpb9uht7osf0ijn6b, origin_jti=d35da81f-fbb9-41e9-ba7d-9de7a9c74975, token_use=access, scope=openid email, auth_time=1672633226, exp=Thu Jan 05 01:00:05 UTC 2023, iat=Thu Jan 05 00:00:05 UTC 2023, jti=958bc517-1b83-4ee4-8bd6-faf602fa25f2, username=SignInWithApple_000340.3f6e937a36b84caaaa2a817ea7d5e69c.0331}}},isBase64Encoded: false}

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.info("EVENT:\n$event")
        logger.info("CONTEXT:\n$context")

        val httpClient = HttpClient(OkHttp) {
            // Accept: application/json
            install(ContentNegotiation) { json(Json) }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(event.caseInsensitiveHeaders["Authorization"].firstOrNull()?.removePrefix("Bearer ") ?: "", "")
                    }
                }
            }
            expectSuccess = true
        }

        val clickupSessionPropsUrl = "$userPropsApiEndpoint/clickup-session"
        val clickUpSession: ClickSession = kotlin.runCatching {
            httpClient.get(clickupSessionPropsUrl).body<ClickSession>()
        }.getOrElse {
            if (it !is ClientRequestException) logger.error("Failed to get ClickUp session", it)
            ClickSession()
        }

        httpClient.post(clickupSessionPropsUrl) {
            contentType(ContentType.Application.Json)
            setBody(clickUpSession)
        }

//        "https://hello-dev-bkahlert-com.auth.us-east-1.amazoncognito.com/oauth2/idpresponse"
        val userId = event.requiredUserId
        logger.info("Successfully authenticated with as $userId")


        val httpClient2 = HttpClient(OkHttp) {
            install(ContentNegotiation) { json(Json) }
            install("ClickUp-PersonalToken-Authorization") {
                plugin(HttpSend).intercept { context ->
                    context.headers[HttpHeaders.Authorization] = clickupApiToken
                    execute(context)
                }
            }
            expectSuccess = true
        }
        val host = clickupUrl
        val pathname = "/user"

        val responseAsString = httpClient2.get("$host$pathname").bodyAsText()

        logger.info("ClickUp response: $responseAsString")

        return response(responseAsString)
    }
}

@Serializable
data class ClickSession(
    @SerialName("code") val code: String? = null,
    @SerialName("access_token") val accessToken: String? = null,
)
