package com.bkahlert.hello.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.bkahlert.hello.app.ui.UiState.Failed
import com.bkahlert.hello.app.ui.UiState.Loaded
import com.bkahlert.hello.app.ui.UiState.Loading
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.props.data.PropsRepository
import com.bkahlert.hello.props.data.SessionPropsDataSource
import com.bkahlert.hello.props.domain.GetPropUseCase
import com.bkahlert.hello.props.domain.GetPropsRepositoryUseCase
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
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement


@Composable
public fun rememberAppViewModel(
    environment: Environment? = null,
    ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
    sessionDataSource: SessionDataSource = if (environment != null) OpenIDConnectSessionDataSource(environment) else FakeSessionDataSource(),
    sessionRepository: SessionRepository = SessionRepository(sessionDataSource, ioDispatcher),
    appScope: CoroutineScope = rememberReportingCoroutineScope(),
): AppViewModel = remember(
    environment,
    ioDispatcher,
    sessionDataSource,
    sessionRepository,
    appScope,
) {
    AppViewModel(
        getUserUseCase = GetUserUseCase(sessionRepository),
        reauthorizeUseCase = ReauthorizeUseCase(sessionRepository),
        authorizeUseCase = AuthorizeUseCase(sessionRepository),
        unauthorizeUseCase = UnauthorizeUseCase(sessionRepository),
        getPropsRepositoryUseCase = GetPropsRepositoryUseCase(sessionRepository.sessionFlow()) {
            PropsRepository(SessionPropsDataSource(it, environment ?: Environment("PROPS_API" to "/mock/props")), ioDispatcher)
        },
        appScope = appScope,
    )
}

public class AppViewModel(
    getUserUseCase: GetUserUseCase,
    private val reauthorizeUseCase: ReauthorizeUseCase,
    private val authorizeUseCase: AuthorizeUseCase,
    private val unauthorizeUseCase: UnauthorizeUseCase,
    internal val getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
    private val appScope: CoroutineScope,
) {
    private val logger by ConsoleLogging

    init {
        appScope.launch {
            reauthorize()
        }
    }

    private val userFlow = getUserUseCase()
    public val userStateFlow: Flow<UiState<User?>> = userFlow
        .map<_, UiState<User?>>(::Loaded)
        .catch { ex -> emit(Failed(ex)) }

    public val uiState: StateFlow<AppViewModelState> = userStateFlow
        .map { userState ->
            logger.grouping("Mapping ${userState::class.simpleName}") {
                when (userState) {
                    is Loading -> AppViewModelState.Loading("User")
                    is Loaded -> AppViewModelState.Loaded(userState.model)
                    is Failed -> AppViewModelState.Failed("loading user", userState.cause)
                }
            }
        }
        .catch { ex -> emit(AppViewModelState.Failed("combining", ex)) }
        .stateIn(appScope, SharingStarted.Eagerly, AppViewModelState.Loading())

    public val props: StateFlow<Props?> = GetPropsUseCase(getPropsRepositoryUseCase)
        .invoke()
        .stateIn(appScope, SharingStarted.Eagerly, null)

    public fun getProp(id: String): Flow<JsonElement?> =
        GetPropUseCase(getPropsRepositoryUseCase).invoke(id)

    public fun authorize() {
        appScope.launch { authorizeUseCase() }
    }

    public fun reauthorize() {
        appScope.launch { reauthorizeUseCase() }
    }

    public fun unauthorize() {
        appScope.launch { unauthorizeUseCase() }
    }
}
