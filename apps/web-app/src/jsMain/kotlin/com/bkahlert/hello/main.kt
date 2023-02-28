package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.ClickUpProps.Companion.mapClickUpProps
import com.bkahlert.hello.app.ui.AppViewModel
import com.bkahlert.hello.app.ui.AppViewModelState
import com.bkahlert.hello.app.ui.LandingScreen
import com.bkahlert.hello.app.ui.rememberAppViewModel
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClient
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.demo.ClickUpDemoProvider
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.data.DataRetrieval
import com.bkahlert.hello.demo.HelloDemoProviders
import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.hello.environment.ui.EnvironmentView
import com.bkahlert.hello.search.PasteHandlingMultiSearchInput
import com.bkahlert.hello.user.ui.UserMenu
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.uri.host
import com.bkahlert.kommons.uri.toUriOrNull
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.LinkItem
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Large
import com.bkahlert.semanticui.core.updateDebugSettings
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.custom.IFrame
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.custom.ReportingCoroutineScope
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_POPUPS
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_SAME_ORIGIN
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_SCRIPTS
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_TOP_NAVIGATION
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION
import com.bkahlert.semanticui.custom.apply
import com.bkahlert.semanticui.custom.sandbox
import com.bkahlert.semanticui.custom.src
import com.bkahlert.semanticui.demo.SemanticUiDemoProviders
import com.bkahlert.semanticui.devmode.DemoDevMode
import com.bkahlert.semanticui.element.AnchorButton
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Buttons
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.icon
import com.bkahlert.semanticui.element.size
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.dom.clear
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

@Composable
fun WebApp(
    viewModel: AppViewModel = rememberAppViewModel(),
) {
    val uiState: State<AppViewModelState> = viewModel.uiState.collectAsState()

    S("toolbar") {
        Buttons({
            classes("links")
            v.icon()
        }) {
            AnchorButton("https://start.me/p/4K6MOy/dashboard") { Icon("globe") }
            AnchorButton("https://home.bkahlert.com") { Icon("home") }
            AnchorButton("https://github.com/bkahlert") { Icon("github") }
            AnchorButton("https://console.aws.amazon.com/") { Icon("aws") }
        }

        S("tasks") {
            val clickUpPropsState = viewModel.getProp("clickup").mapClickUpProps().collectAsState(null)
            when (val clickUpProps = clickUpPropsState.value) {
                null -> ClickUpMenu(
                    viewModel = rememberClickUpMenuViewModel(),
                    state = Disabled,
                    loadingState = LoadingState.Indeterminate,
                )

                else -> ClickUpMenu(rememberClickUpMenuViewModel(
                    configurers = arrayOf(
                        ClickUpHttpClientConfigurer(),
                        ClickUpTestClientConfigurer(),
                    ),
                    initialState = Disabled,
                    storage = localStorage.scoped("clickup")
                ).apply {
                    if (clickUpProps.apiToken != null) {
                        val clickUpClient = ClickUpHttpClient(clickUpProps.apiToken, InMemoryStorage())
                        connect(clickUpClient)
                    }
                    enable()
                })
            }
        }

        S("search") {
            PasteHandlingMultiSearchInput()
        }
    }

    S("bookmarks") {

        val url: CharSequence? = when (window.location.href.toUriOrNull()?.host) {
            "localhost" -> "placeholder.html"
            else -> "https://start.me/p/0PBMOo/dkb"
        }

        if (url != null) {
            IFrame("Loading bookmarks …", {
                v.size(Large)
            }) {
                style { border(0.px) }
                src(url)
                sandbox(
                    ALLOW_POPUPS,
                    ALLOW_SCRIPTS,
                    ALLOW_SAME_ORIGIN,
                    ALLOW_TOP_NAVIGATION,
                    ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION,
                )
            }
        }
    }

    S("session", attrs = {
        style { marginLeft(5.em) }
        if (uiState.value is AppViewModelState.Loaded) apply(LoadingState.On)
    }) {
        when (val state = uiState.value) {
            is AppViewModelState.Loading -> Menu({ classes("mini", "compact", "secondary") }) {
                apply(LoadingState.On, loaderText = "...")
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
                attrs = { classes("mini", "compact", "secondary") }
            )

            is AppViewModelState.Failed -> Menu({ classes("mini", "compact", "secondary") }) {
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
}


fun main() {

    updateDebugSettings { module, settings ->
        settings.debug = module !in listOf("transition")
        settings.verbose = true
        settings.performance = true
    }

    DemoDevMode(*SemanticUiDemoProviders, *HelloDemoProviders, ClickUpDemoProvider)

    val appScope = ReportingCoroutineScope()
    val appRoot = document.getElementById("root")?.apply { clear() } ?: error("root element does not exist")

    renderComposable(appRoot) {
        Style(ClickUpStyleSheet)
        var showLandingScreen by remember { mutableStateOf(true) }
        val environmentRepository = remember { EnvironmentRepository(DynamicEnvironmentDataSource(), appScope) }
        val environmentRetrieval by environmentRepository.environmentFlow().collectAsState(DataRetrieval.Ongoing)
        if (showLandingScreen) {
            LandingScreen(onTimeout = { showLandingScreen = false })
        } else {
            when (val retrieval = environmentRetrieval) {
                is DataRetrieval.Ongoing -> showLandingScreen = true
                is DataRetrieval.Succeeded -> {
                    WebApp(rememberAppViewModel(environment = retrieval.data))
                    EnvironmentView(retrieval.data)
                }

                is DataRetrieval.Failed -> ErrorMessage(retrieval.cause, retrieval.message)
            }
        }
    }
}
