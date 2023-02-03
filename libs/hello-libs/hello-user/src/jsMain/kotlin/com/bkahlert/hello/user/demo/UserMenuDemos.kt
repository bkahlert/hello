package com.bkahlert.hello.user.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.hello.session.demo.FakeSessionDataSource
import com.bkahlert.hello.session.demo.TestUserInfo
import com.bkahlert.hello.session.domain.AuthorizeUseCase
import com.bkahlert.hello.session.domain.ReauthorizeUseCase
import com.bkahlert.hello.session.domain.UnauthorizeUseCase
import com.bkahlert.hello.user.domain.GetUserUseCase
import com.bkahlert.hello.user.domain.User
import com.bkahlert.hello.user.ui.UserMenu
import com.bkahlert.kommons.InstantAsEpochSeconds
import com.bkahlert.kommons.Now
import com.bkahlert.kommons.auth.OpenIDStandardClaims
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.demo.SemanticUiImageFixtures.JohnDoeWithBackground
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.LabeledIconButton
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.size
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@Composable
public fun UserMenuDemos() {
    Demos("UserMenu") {
        Demo("Empty") {
            UserMenu()
        }
        Demo("Filled") {
            UserMenu(User.JohnDoe)
        }
        Demo("Dynamically filled") {
            val repository = remember { SessionRepository(FakeSessionDataSource()) }
            val getUserInfo = remember { GetUserUseCase(repository) }
            val authorizeUseCase = remember { AuthorizeUseCase(repository) }
            val reauthorizeUseCase = remember { ReauthorizeUseCase(repository) }
            val unauthorizeUseCase = remember { UnauthorizeUseCase(repository) }

            val user: User? by getUserInfo().collectAsState(null)
            var refreshing by remember(user) { mutableStateOf(false) }

            val scope = rememberReportingCoroutineScope()
            LabeledIconButton({
                if (refreshing) s.disabled()
                v.size(Mini)
                onClick {
                    scope.launch {
                        refreshing = true
                        reauthorizeUseCase()
                    }
                }
            }) {
                Icon("sync", loading = refreshing)
                Text("Refresh")
            }

            when (val current = user) {
                null -> {
                    UserMenu(
                        loadingState = if (refreshing) LoadingState.On else LoadingState.Off,
                        onSignIn = {
                            refreshing = true
                            scope.launch { authorizeUseCase() }
                        },
                    )
                }

                else -> UserMenu(
                    loadingState = if (refreshing) LoadingState.On else LoadingState.Off,
                    user = current,
                    onSignOut = {
                        refreshing = true
                        scope.launch { unauthorizeUseCase() }
                    },
                )
            }
        }
    }
}

public fun TestUser(
    username: String? = null,
    picture: Uri? = null,
    subjectIdentifier: String = "d2f87456-ed02-49af-8de4-96b9e627d270",
    issuerIdentifier: Uri = Uri.parse("https://provider.example.com/test"),
    audiences: List<String> = listOf("made-up-client_id"),
    expiresAt: InstantAsEpochSeconds = Now + 15.minutes,
    issuedAt: InstantAsEpochSeconds = expiresAt - 60.minutes,
    authenticatedAt: InstantAsEpochSeconds = issuedAt - 5.days,
    id: String = "46ed56e8-6145-413c-9dd0-b1d89a825f41",
    origin_jti: String = "43c369dc-7c26-4ce1-afa9-012cdb4d98f2",
): User {
    val testUserInfo = TestUserInfo(
        subjectIdentifier = subjectIdentifier,
        issuerIdentifier = issuerIdentifier,
        audiences = audiences,
        expiresAt = expiresAt,
        issuedAt = issuedAt,
        authenticatedAt = authenticatedAt,
        id = id,
        origin_jti = origin_jti,
    ) {
        username?.also { put(User.USERNAME_CLAIM_NAME, JsonPrimitive(it)) }
        picture?.also { put(OpenIDStandardClaims.PICTURE_CLAIM_NAME, JsonPrimitive(it.toString())) }
    }
    return User(FakeSessionDataSource.FakeAuthorizedSession(testUserInfo))
}

public val User.Companion.JohnDoe: User
    get() = TestUser(
        username = "john.doe",
        picture = JohnDoeWithBackground,
        subjectIdentifier = randomString(),
    )
