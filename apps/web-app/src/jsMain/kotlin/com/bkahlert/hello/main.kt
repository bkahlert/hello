package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bkahlert.Brand
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.demo.ClickUpDemos
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.custom.Custom
import com.bkahlert.hello.custom.linearGradient
import com.bkahlert.hello.search.PasteHandlingMultiSearchInput
import com.bkahlert.hello.search.demos.SearchDemos
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.net.host
import com.bkahlert.kommons.net.toUriOrNull
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.jQuery
import com.bkahlert.semanticui.custom.Length
import com.bkahlert.semanticui.demo.SemanticUiDemoProviders
import com.bkahlert.semanticui.devmode.setupDemoDevMode
import com.bkahlert.semanticui.element.AnkerButton
import com.bkahlert.semanticui.element.Buttons
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.basic
import com.bkahlert.semanticui.element.icon
import io.ktor.http.Url
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.div
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.transform
import org.jetbrains.compose.web.css.unaryMinus
import org.jetbrains.compose.web.renderComposable

@Composable
fun App() {
    S("toolbar") {
        Buttons({
            classes("links")
            v.icon().basic()
        }) {
            AnkerButton("https://start.me/p/4K6MOy/dashboard") { Icon("globe") }
            AnkerButton("https://home.bkahlert.com") { Icon("home") }
            AnkerButton("https://github.com/bkahlert") { Icon("github") }
            AnkerButton("https://console.aws.amazon.com/") { Icon("aws") }
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
            DisposableEffect(Unit) {
                jQuery(scopeElement).find("[type=search]").focus()
                onDispose { }
            }
        }
    }

    S("bookmarks") {
        S(attrs = {
            style {
                property("z-index", "1")
                height(GRADIENT_HEIGHT)
                transform { translateY(-GRADIENT_HEIGHT / 2) }
                backgroundColor(Color.transparent)
                backgroundImage(
                    linearGradient(
                        CUSTOM_BACKGROUND_COLOR.fade(0.0),
                        CUSTOM_BACKGROUND_COLOR,
                        CUSTOM_BACKGROUND_COLOR.fade(0.0)
                    )
                )
            }
        })
        val url = when (window.location.href.toUriOrNull()?.host) {
            "localhost" -> null
            else -> Url("https://start.me/p/0PBMOo/dkb")
        }
        Custom(url)
    }
}

fun main() {
    setupDemoDevMode(ClickUpDemos, SearchDemos, *SemanticUiDemoProviders)
    renderComposable("root") {
        Style(ClickUpStyleSheet)
        App()
    }
}

val GRADIENT_HEIGHT: Length = 0.3.cssRem
val CUSTOM_BACKGROUND_COLOR = Brand.colors.white
