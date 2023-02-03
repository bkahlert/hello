package com.bkahlert.hello

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.demo.ClickUpDemoProvider
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.demo.HelloDemoProviders
import com.bkahlert.hello.search.PasteHandlingMultiSearchInput
import com.bkahlert.hello.user.ui.UserMenu
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.uri.host
import com.bkahlert.kommons.uri.toUriOrNull
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Large
import com.bkahlert.semanticui.core.updateDebugSettings
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
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.icon
import com.bkahlert.semanticui.element.size
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.dom.clear
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.renderComposable

@Composable
fun App() {
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
            ClickUpMenu(rememberClickUpMenuViewModel(
                configurers = arrayOf(
                    ClickUpHttpClientConfigurer(),
                    ClickUpTestClientConfigurer(),
                ),
                initialState = Disabled,
                storage = localStorage.scoped("clickup")
            ).apply { enable() })
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
}


fun main() {

    updateDebugSettings { module, settings ->
        settings.debug = module !in listOf("transition")
        settings.verbose = true
        settings.performance = true
    }

    ReportingCoroutineScope().launch {
        DemoDevMode(*SemanticUiDemoProviders, *HelloDemoProviders, ClickUpDemoProvider)

        val root = document.getElementById("root")?.apply { clear() } ?: error("root element does not exist")
        renderComposable(root) {
            Style(ClickUpStyleSheet)
            App()
        }
    }
}
