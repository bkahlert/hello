package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.bkahlert.hello.BookmarkProps.Companion.mapBookmarkProps
import com.bkahlert.hello.ClickUpProps.Companion.mapClickUpProps
import com.bkahlert.hello.app.ui.AppViewModel
import com.bkahlert.hello.app.ui.rememberAppViewModel
import com.bkahlert.hello.clickup.demo.ClickUpDemoProvider
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.hello.demo.HelloDemoProviders
import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.hello.search.PasteHandlingMultiSearchInput
import com.bkahlert.hello.user.ui.UserMenu
import com.bkahlert.kommons.dom.uri
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Huge
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Large
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Massive
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.custom.IFrame
import com.bkahlert.semanticui.custom.ReportingCoroutineScope
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_POPUPS
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_SAME_ORIGIN
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_SCRIPTS
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_TOP_NAVIGATION
import com.bkahlert.semanticui.custom.Sandbox.ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION
import com.bkahlert.semanticui.custom.sandbox
import com.bkahlert.semanticui.custom.src
import com.bkahlert.semanticui.demo.SemanticUiDemoProviders
import com.bkahlert.semanticui.devmode.DemoDevMode
import com.bkahlert.semanticui.element.AnchorButton
import com.bkahlert.semanticui.element.Buttons
import com.bkahlert.semanticui.element.Container
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.active
import com.bkahlert.semanticui.element.icon
import com.bkahlert.semanticui.element.inline
import com.bkahlert.semanticui.element.size
import com.bkahlert.semanticui.module.updateDebugSettings
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.clear
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.renderComposable

@Composable
fun WebApp(
    viewModel: AppViewModel = rememberAppViewModel(),
) {

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
            val clickUpPropsResource by viewModel.getProp("clickup").mapClickUpProps().collectAsState(null)
            ClickUpMenuElement(clickUpPropsResource)
        }

        S("search") {
            PasteHandlingMultiSearchInput()
        }
    }

    S("content") {

        S("bookmarks") {
            val bookmarkPropsResource by viewModel.getProp("bookmark").mapBookmarkProps().collectAsState(null)
            when (val resource = bookmarkPropsResource) {
                null -> Loader({ v.size(Huge); s.active() })

                is Success -> {
                    val defaultUri = resource.data?.default
                    IFrame("Loading bookmarks …", { v.size(Large) }) {
                        style { border(0.px) }
                        src(defaultUri?.takeIf { it.scheme == window.location.uri.scheme } ?: "about:blank")
                        sandbox(
                            ALLOW_POPUPS,
                            ALLOW_SCRIPTS,
                            ALLOW_SAME_ORIGIN,
                            ALLOW_TOP_NAVIGATION,
                            ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION,
                        )
                    }
                }

                is Failure -> {
                    ErrorMessage(resource.message, resource.cause)
                }
            }
        }
    }

    S("footer") {
        val userResource by viewModel.user.collectAsState(null)
        when (val resource = userResource) {
            null -> Menu({ classes("tiny", "compact", "secondary") }) { Item { Loader({ v.size(Mini).inline(); s.active() }) } }

            is Success -> UserMenu(
                user = resource.data,
                onSignIn = viewModel::authorize,
                onReauthorize = { viewModel.reauthorize(force = false) }.takeUnless { resource.data == null },
                onSignOut = viewModel::unauthorize,
                attrs = { classes("tiny", "compact", "secondary") }
            )

            is Failure -> UserMenu(
                userResource = resource,
                onReauthorize = { viewModel.reauthorize(force = false) },
                onForceReauthorize = { viewModel.reauthorize(force = true) },
                attrs = { classes("tiny", "compact", "secondary") }
            )
        }
    }
}

@JsModule("./semantic/semantic.less")
private external val SemanticStyles: dynamic

@JsModule("./styles/web-app.scss")
private external val AppStyles: dynamic

fun main() {
    SemanticStyles
    AppStyles

    updateDebugSettings { module, settings ->
        settings.debug = module !in listOf("transition")
        settings.verbose = true
        settings.performance = false
    }

    DemoDevMode(*SemanticUiDemoProviders, *HelloDemoProviders, ClickUpDemoProvider)

    val appScope = ReportingCoroutineScope()
    val appRoot = document.getElementById("root")?.apply { clear() } ?: error("root element does not exist")

    renderComposable(appRoot) {
        val environmentRepository = remember { EnvironmentRepository(DynamicEnvironmentDataSource(), appScope) }
        val environmentResource by environmentRepository.environmentFlow().collectAsState(null)
        when (val resource = environmentResource) {
            null -> {
                Loader({ v.size(Massive); s.active() })
            }

            is Success -> {
                WebApp(rememberAppViewModel(environment = resource.data))
            }

            is Failure -> S("progress") {
                Container { ErrorMessage(resource.message, resource.cause) }
            }
        }
    }
}
