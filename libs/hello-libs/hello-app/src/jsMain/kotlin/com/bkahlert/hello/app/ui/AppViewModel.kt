package com.bkahlert.hello.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.props.data.PropsRepository
import com.bkahlert.hello.props.data.SessionPropsDataSource
import com.bkahlert.hello.props.domain.GetPropUseCase
import com.bkahlert.hello.props.domain.GetPropsUseCase
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.hello.session.data.OpenIDConnectSessionDataSource
import com.bkahlert.hello.session.data.SessionDataSource
import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.hello.session.demo.FakeSessionDataSource
import com.bkahlert.hello.session.domain.AuthorizeUseCase
import com.bkahlert.hello.session.domain.ReauthorizeUseCase
import com.bkahlert.hello.session.domain.UnauthorizeUseCase
import com.bkahlert.hello.user.domain.GetUserUseCase
import com.bkahlert.hello.user.domain.User
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject

@Composable
public fun rememberAppViewModel(
    environment: Environment? = null,
    appScope: CoroutineScope = rememberReportingCoroutineScope(),
    sessionDataSource: SessionDataSource = if (environment != null) OpenIDConnectSessionDataSource(environment) else FakeSessionDataSource(),
    sessionRepository: SessionRepository = SessionRepository(sessionDataSource, appScope),
    propsRepository: PropsRepository = PropsRepository(sessionRepository.sessionFlow(), {
        SessionPropsDataSource(it, environment ?: Environment("PROPS_API" to "/mock/props"))
    }, appScope),
): AppViewModel = remember(
    environment,
    sessionDataSource,
    sessionRepository,
    propsRepository,
    appScope,
) {
    AppViewModel(
        authorizeUseCase = AuthorizeUseCase(sessionRepository),
        reauthorizeUseCase = ReauthorizeUseCase(sessionRepository),
        unauthorizeUseCase = UnauthorizeUseCase(sessionRepository),
        getUserUseCase = GetUserUseCase(sessionRepository),
        getPropsUseCase = GetPropsUseCase(propsRepository),
        getPropUseCase = GetPropUseCase(propsRepository),
        appScope = appScope,
    )
}

public class AppViewModel(
    private val authorizeUseCase: AuthorizeUseCase,
    private val reauthorizeUseCase: ReauthorizeUseCase,
    private val unauthorizeUseCase: UnauthorizeUseCase,
    getUserUseCase: GetUserUseCase,
    getPropsUseCase: GetPropsUseCase,
    private val getPropUseCase: GetPropUseCase,
    private val appScope: CoroutineScope,
) {

    public val user: StateFlow<Resource<User?>?> = getUserUseCase()
        .stateIn(appScope, SharingStarted.Eagerly, null)

    public val props: StateFlow<Resource<Props?>?> = getPropsUseCase()
        .stateIn(appScope, SharingStarted.Eagerly, null)

    public fun getProp(id: String): Flow<Resource<JsonObject?>> = getPropUseCase(id)

    public fun authorize() {
        appScope.launch { authorizeUseCase() }
    }

    public fun reauthorize(force: Boolean = false) {
        appScope.launch { reauthorizeUseCase(force = force) }
    }

    public fun unauthorize() {
        appScope.launch { unauthorizeUseCase() }
    }
}
