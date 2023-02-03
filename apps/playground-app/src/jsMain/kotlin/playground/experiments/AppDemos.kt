package playground.experiments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClient
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.props.data.PropsRepository
import com.bkahlert.hello.props.data.SessionPropsDataSource
import com.bkahlert.hello.props.domain.GetPropsRepositoryUseCase
import com.bkahlert.hello.props.domain.GetPropsUseCase
import com.bkahlert.hello.props.ui.PropsView
import com.bkahlert.hello.session.data.OpenIDConnectSessionDataSource
import com.bkahlert.hello.session.data.SessionDataSource
import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.hello.session.demo.FakeSessionDataSource
import com.bkahlert.hello.session.domain.AuthorizeUseCase
import com.bkahlert.hello.session.domain.ReauthorizeUseCase
import com.bkahlert.hello.session.domain.UnauthorizeUseCase
import com.bkahlert.hello.user.domain.GetUserUseCase
import com.bkahlert.hello.user.domain.User
import com.bkahlert.hello.user.ui.UserMenu
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.takeUnlessEmpty
import com.bkahlert.kommons.text.capitalize
import com.bkahlert.kommons.text.simpleKebabCasedName
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.LinkItem
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.collection.Message
import com.bkahlert.semanticui.collection.attached
import com.bkahlert.semanticui.collection.borderless
import com.bkahlert.semanticui.collection.info
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached.Bottom
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.LineLength.Short
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import com.bkahlert.semanticui.demo.DEMO_BASE_DELAY
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Buttons
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Image
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.Placeholder
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.active
import com.bkahlert.semanticui.element.basic
import com.bkahlert.semanticui.element.centered
import com.bkahlert.semanticui.element.lineLength
import com.bkahlert.semanticui.element.loading
import com.bkahlert.semanticui.element.size
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.duration
import org.jetbrains.compose.web.css.s
import org.jetbrains.compose.web.css.transitions
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import playground.experiments.UiState.Failed
import playground.experiments.UiState.Loaded
import playground.experiments.UiState.Loading
import com.bkahlert.semanticui.element.List as SList

@Composable
fun LandingScreen(
    onTimeout: () -> Unit,
) {
    val currentOnTimeout by rememberUpdatedState(onTimeout)
    LaunchedEffect(Unit) {
        delay(DEMO_BASE_DELAY)
        currentOnTimeout()
    }
    Segment({ v.basic() }) {
        Image(ImageFixtures.HelloFavicon, attrs = { v.size(Size.Small).centered() })
    }
}

@Composable
fun App(
    viewModel: AppViewModel = rememberAppViewModel(),
) {

    val uiState: State<AppViewModelState> = viewModel.uiState.collectAsState()
    val propsState = viewModel.props.collectAsState()
    val clickUpPropsState = viewModel.clickUpProps.collectAsState()

    Menu({ v.borderless() }) {
        Item { Img(ImageFixtures.PearLogo.toString()) }
        when (val state = uiState.value) {
            is AppViewModelState.Loading -> Menu({ classes("right") }) {
                Item {
                    Loader(state.models.takeUnlessEmpty()?.let { "Loading $it" }) { s.active() }
                }
            }

            is AppViewModelState.Loaded -> UserMenu(
                user = state.user,
                onSignIn = viewModel::authorize,
                onSignOut = viewModel::unauthorize,
                {
                    LinkItem({ onClick { viewModel.reauthorize() } }) {
                        Icon("sync")
                        Text("Refresh")
                    }
                },
                attrs = { classes("right") }
            )

            is AppViewModelState.Failed -> Menu({ classes("right") }) {
                Item {
                    Buttons {
                        Button({
                            onClick { viewModel.reauthorize() }
                        }) {
                            Icon("eraser")
                            Text("Reset")
                        }
                    }
                }
            }
        }
    }

    Segment({
        if (uiState.value is AppViewModelState.Loading) s.loading()
        style {
            transitions {
                "all" {
                    duration(0.4.s)
                    timingFunction = AnimationTimingFunction.Ease
                }
            }
        }
    }) {
        when (val state = uiState.value) {
            is AppViewModelState.Loading -> SList {
                repeat(2) {
                    Item {
                        Placeholder {
                            Line({ v.lineLength(Short) })
                            Line()
                        }
                    }
                }
            }

            is AppViewModelState.Loaded -> {
                Header { Text("Props") }
                when (val props = propsState.value) {
                    null -> PropsView(loadingState = LoadingState.On)
                    else -> PropsView(props)
                }

                Header { Text("ClickUp") }
                when (val clickUpProps = clickUpPropsState.value) {
                    null -> ClickUpMenu(
                        viewModel = rememberClickUpMenuViewModel(),
                        state = ClickUpMenuState.Transitioned.Succeeded.Disabled,
                        loadingState = LoadingState.Indeterminate,
                    )

                    else -> ClickUpMenu(
                        viewModel = rememberClickUpMenuViewModel(
                            ClickUpHttpClientConfigurer(),
                            ClickUpTestClientConfigurer(),
                        ).apply {
                            if (clickUpProps.apiToken != null) {
                                val clickUpClient = ClickUpHttpClient(clickUpProps.apiToken, InMemoryStorage())
                                connect(clickUpClient)
                            }
                        },
                    )
                }
            }

            is AppViewModelState.Failed -> {
                Header { Text(state.operation.capitalize() + " failed") }
                ErrorMessage(state.cause, state.operation.capitalize() + " failed") {
                    val details = state.cause.message
                    if (details != null) {
                        P { Text(details) }
                    }
                }
            }
        }
    }

    Message({ v.attached(Bottom).info() }) {
        Icon("info")
        S("ui", "label") {
            Text("State")
            S("detail") {
                Text(uiState.value::class.simpleName ?: "?")
            }
        }
    }
}


/** The state of a UI element. */
public sealed class UiState<out T> {

    public data class Loading(
        public val name: String,
    ) : UiState<Nothing>() {
        public companion object {
            public inline operator fun <reified T : Any> invoke() =
                Loading(T::class.simpleKebabCasedName?.replace('-', ' ') ?: "unknown")
        }
    }

    public data class Loaded<out T>(
        /** The successfully loaded data. */
        public val model: T,
    ) : UiState<T>()

    public data class Failed(
        /** The cause of this failed state. */
        val cause: Throwable,
    ) : UiState<Nothing>()
}

public sealed interface AppViewModelState {
    public data class Loading(
        public val models: List<String>,
    ) : AppViewModelState {
        public constructor(vararg models: String) : this(models.asList())
    }

    public data class Loaded(
        public val user: User?,
    ) : AppViewModelState

    public data class Failed(
        /** The name of the failed operation. */
        val operation: String,
        /** The cause of this failed state. */
        val cause: Throwable,
    ) : AppViewModelState
}

class AppViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val reauthorizeUseCase: ReauthorizeUseCase,
    private val authorizeUseCase: AuthorizeUseCase,
    private val unauthorizeUseCase: UnauthorizeUseCase,
    internal val getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
    private val appScope: CoroutineScope,
) {

    init {
        appScope.launch {
            reauthorize()
        }
    }

    private val userFlow = getUserUseCase()
    public val userStateFlow: Flow<UiState<User?>> = userFlow
        .map<_, UiState<User?>>(::Loaded)
        .catch { ex -> emit(Failed(ex)) }

    val uiState: StateFlow<AppViewModelState> = userStateFlow
        .map { userState ->
            val combine: () -> AppViewModelState = {
                when (userState) {
                    is Loading -> AppViewModelState.Loading("User")
                    is Loaded -> AppViewModelState.Loaded(userState.model)
                    is Failed -> AppViewModelState.Failed("loading user", userState.cause)
                }
            }
            console.grouping("AppState: Mapping ${userState::class.simpleName}", block = combine)
        }
        .catch { ex -> emit(AppViewModelState.Failed("combining", ex)) }
        .stateIn(appScope, SharingStarted.Eagerly, AppViewModelState.Loading())

    public val props = GetPropsUseCase(getPropsRepositoryUseCase)
        .invoke()
        .stateIn(appScope, SharingStarted.Eagerly, null)

    public val clickUpProps = GetClickUpPropsUseCase(getPropsRepositoryUseCase)
        .invoke()
        .stateIn(appScope, SharingStarted.Eagerly, null)

    fun authorize() {
        appScope.launch { authorizeUseCase() }
    }

    fun reauthorize() {
        appScope.launch { reauthorizeUseCase() }
    }

    fun unauthorize() {
        appScope.launch { unauthorizeUseCase() }
    }
}


@Composable
fun rememberAppViewModel(
    environment: Environment? = null,
    ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
    sessionDataSource: SessionDataSource = if (environment != null) OpenIDConnectSessionDataSource(environment) else FakeSessionDataSource(),
    sessionRepository: SessionRepository = SessionRepository(sessionDataSource, ioDispatcher),
    appScope: CoroutineScope = rememberReportingCoroutineScope(),
) = remember(
    sessionDataSource,
    ioDispatcher,
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
