package com.bkahlert.hello.app.session

import com.bkahlert.hello.app.session.MockPluginConfig.Companion.findResponse
import com.bkahlert.hello.showcase.DEMO_BASE_DELAY
import com.bkahlert.kommons.auth.JsonWebTokenPayload.IdTokenPayload
import com.bkahlert.kommons.auth.OpenIDStandardClaims
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.UserInfo
import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.oauth.OAuth2ResourceServer
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.time.InstantAsEpochSeconds
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.uri.Uri
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.HttpClientCall
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.InternalAPI
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

public class FakeSession(
    userInfo: UserInfo = IdTokenPayload.JohnDoeInfo,
    initiallyAuthorized: Boolean = false,
) {

    internal var session: Session
        private set
    private var authorizedSession: Session.AuthorizedSession
    private lateinit var unauthorizedSession: Session.UnauthorizedSession

    init {
        authorizedSession = FakeAuthorizedSession(userInfo) {
            delay(DEMO_BASE_DELAY)
            unauthorizedSession.also { session = it }
        }
        unauthorizedSession = FakeUnauthorizedSession {
            delay(DEMO_BASE_DELAY)
            authorizedSession.also { session = it }
        }
        session = if (initiallyAuthorized) authorizedSession else unauthorizedSession
    }

    public companion object {

        public fun Unauthorized(
            userInfo: UserInfo = IdTokenPayload.JohnDoeInfo,
        ): Session.UnauthorizedSession = FakeSession(
            userInfo = userInfo,
            initiallyAuthorized = false
        ).unauthorizedSession

        public fun Authorized(
            userInfo: UserInfo = IdTokenPayload.JohnDoeInfo,
        ): Session.AuthorizedSession = FakeSession(
            userInfo = userInfo,
            initiallyAuthorized = true
        ).authorizedSession
    }
}

private class FakeUnauthorizedSession(
    private val authorize: suspend () -> Session.AuthorizedSession,
) : Session.UnauthorizedSession {
    override suspend fun authorize(): Session = authorize.invoke()
    override fun toString(): String = "FakeSigned>OUT<State"
}

private class FakeAuthorizedSession(
    override val userInfo: UserInfo,
    private val unauthorize: suspend () -> Session.UnauthorizedSession,
) : Session.AuthorizedSession {
    override val diagnostics: Map<String, String?> get() = userInfo.mapValues { it.toString() }
    override suspend fun reauthorize(httpClient: HttpClient?): Session = this
    override suspend fun unauthorize(httpClient: HttpClient?): Session.UnauthorizedSession = unauthorize.invoke()
    override fun installAuth(config: HttpClientConfig<HttpClientEngineConfig>, vararg resources: OAuth2ResourceServer) {
        config.install(MockResponsePlugin) {
            onRequestMatching { true }.respond {
                put("foo", buildJsonObject { put("bar", JsonPrimitive(42)) })
                put("baz", JsonNull)
            }
        }
    }

    override fun toString(): String = "FakeSigned>IN<State"
}

@OptIn(InternalAPI::class)
private val MockResponsePlugin = createClientPlugin("MockResponsePlugin", ::MockPluginConfig) {
    on(Send) { request ->
        val requestData = request.build()
        val responseData = pluginConfig.findResponse(requestData) ?: error("No matching rule found for $requestData")
        HttpClientCall(client, requestData, responseData)
    }
}

public class MockPluginConfig {
    private val rules: MutableList<Pair<(HttpRequestData) -> Boolean, HttpResponseDataBuilder>> = mutableListOf()

    public fun onRequestMatching(predicate: (HttpRequestData) -> Boolean): HttpResponseDataBuilderScope {
        val responseBuilder = HttpResponseDataBuilder()
        rules.add(predicate to responseBuilder)
        return responseBuilder
    }

    public companion object {
        internal fun MockPluginConfig.findResponse(requestData: HttpRequestData): HttpResponseData? =
            rules.firstOrNull { (matcher, _) -> matcher.invoke(requestData) }?.second?.responder?.invoke(requestData)
    }
}

public interface HttpResponseDataBuilderScope {
    public fun respond(
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        json: JsonObjectBuilder.(HttpRequestData) -> Unit,
    )
}

private typealias Responder = (HttpRequestData) -> HttpResponseData

private class HttpResponseDataBuilder(
    var responder: Responder? = null,
) : HttpResponseDataBuilderScope {
    override fun respond(statusCode: HttpStatusCode, json: JsonObjectBuilder.(HttpRequestData) -> Unit) {
        responder = { requestData ->
            HttpResponseData(
                statusCode = statusCode,
                requestTime = GMTDate(),
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
                version = HttpProtocolVersion.HTTP_1_1,
                body = ByteReadChannel(LenientJson.encodeToString(buildJsonObject { json(requestData) })),
                callContext = requestData.executionContext,
            )
        }
    }
}


public fun TestUserInfo(
    subjectIdentifier: String = "d2f87456-ed02-49af-8de4-96b9e627d270",
    issuerIdentifier: Uri = Uri.parse("https://provider.example.com/test"),
    audiences: List<String> = listOf("made-up-client_id"),
    expiresAt: InstantAsEpochSeconds = Now + 15.minutes,
    issuedAt: InstantAsEpochSeconds = expiresAt - 60.minutes,
    authenticatedAt: InstantAsEpochSeconds = issuedAt - 5.days,
    id: String = "46ed56e8-6145-413c-9dd0-b1d89a825f41",
    origin_jti: String = "43c369dc-7c26-4ce1-afa9-012cdb4d98f2",
    init: JsonObjectBuilder.() -> Unit = {},
): UserInfo = UserInfo(buildJsonObject {
    put(OpenIDStandardClaims.SUB_CLAIM_NAME, JsonPrimitive(subjectIdentifier))
    put("iss", JsonPrimitive(issuerIdentifier.toString()))
    put("aud", JsonArray(audiences.map { JsonPrimitive(it) }))
    put("exp", JsonPrimitive(expiresAt.epochSeconds))
    put("iat", JsonPrimitive(issuedAt.epochSeconds))
    put("auth_time", JsonPrimitive(authenticatedAt.epochSeconds))
    put("jti", JsonPrimitive(id))
    put("origin_jti", JsonPrimitive(origin_jti))
    put("token_use", JsonPrimitive("id"))
    init()
})

public val IdTokenPayload.Companion.JohnDoeInfo: UserInfo
    get() = TestUserInfo(
        subjectIdentifier = randomString(),
    )
