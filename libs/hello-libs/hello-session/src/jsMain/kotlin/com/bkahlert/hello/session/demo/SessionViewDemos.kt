package com.bkahlert.hello.session.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.hello.session.demo.FakeSessionDataSource.Companion.FakeAuthorizedSession
import com.bkahlert.hello.session.demo.FakeSessionDataSource.Companion.FakeUnauthorizedSession
import com.bkahlert.hello.session.domain.AuthorizeUseCase
import com.bkahlert.hello.session.domain.GetSessionUseCase
import com.bkahlert.hello.session.domain.ReauthorizeUseCase
import com.bkahlert.hello.session.domain.UnauthorizeUseCase
import com.bkahlert.hello.session.ui.SessionView
import com.bkahlert.kommons.InstantAsEpochSeconds
import com.bkahlert.kommons.Now
import com.bkahlert.kommons.auth.JsonWebTokenPayload.IdTokenPayload
import com.bkahlert.kommons.auth.OpenIDStandardClaims
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.UserInfo
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.custom.LoadingState.Off
import com.bkahlert.semanticui.custom.LoadingState.On
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.LabeledIconButton
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.size
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@Composable
public fun SessionViewDemos() {
    Demos("SessionView") {
        Demo("Unauthorized") {
            SessionView(FakeUnauthorizedSession())
        }
        Demo("Authorized") {
            SessionView(FakeAuthorizedSession())
        }
        Demo("Dynamically filled") {
            val repository = remember { SessionRepository(FakeSessionDataSource()) }
            val getSession = remember { GetSessionUseCase(repository) }
            val authorize = remember { AuthorizeUseCase(repository) }
            val reauthorize = remember { ReauthorizeUseCase(repository) }
            val unauthorize = remember { UnauthorizeUseCase(repository) }

            val session: Session? by getSession().collectAsState(null)
            var refreshing by remember(session) { mutableStateOf(false) }

            val scope = rememberReportingCoroutineScope()
            LabeledIconButton({
                if (refreshing) s.disabled()
                v.size(Mini)
                onClick {
                    scope.launch {
                        refreshing = true
                        reauthorize()
                    }
                }
            }) {
                Icon("sync", loading = refreshing)
                Text("Refresh")
            }

            when (val current = session) {
                null -> SessionView()
                else -> SessionView(
                    loadingState = if (refreshing) On else Off,
                    session = current,
                    onAuthorize = {
                        refreshing = true
                        scope.launch { authorize() }
                    },
                    onReauthorize = {
                        refreshing = true
                        scope.launch { reauthorize() }
                    },
                    onUnauthorize = {
                        refreshing = true
                        scope.launch { unauthorize() }
                    },
                )
            }
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
