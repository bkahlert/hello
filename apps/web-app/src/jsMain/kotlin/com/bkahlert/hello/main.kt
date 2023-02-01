package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.demo.ClickUpDemos
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.custom.Custom
import com.bkahlert.hello.custom.linearGradient
import com.bkahlert.hello.grid.GridLayoutStylesheet
import com.bkahlert.hello.grid.GridLayoutStylesheet.CUSTOM_BACKGROUND_COLOR
import com.bkahlert.hello.grid.GridLayoutStylesheet.GRADIENT_HEIGHT
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Custom
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.CustomGradient
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Header
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Links
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Search
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Tasks
import com.bkahlert.hello.header.Header
import com.bkahlert.hello.header.center
import com.bkahlert.hello.search.SearchFeature
import com.bkahlert.hello.search.demos.SearchDemos
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.net.host
import com.bkahlert.kommons.net.toUriOrNull
import com.bkahlert.semanticui.custom.backgroundColor
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
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.div
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.transform
import org.jetbrains.compose.web.css.unaryMinus
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

interface Feature {
    val name: String
    val loaded: Boolean
    val content: ContentBuilder<HTMLElement>
}

@Stable
interface AppState {
    val features: Set<Feature>
    fun load(feature: Feature): ContentBuilder<HTMLElement>?
}

class ProgressivelyLoadingAppState(
    initialFeatures: Set<Feature>,
) : AppState {
    override var features by mutableStateOf(initialFeatures)
        private set

    override fun load(feature: Feature): ContentBuilder<HTMLElement>? {
        if (features.all { it.loaded }) {
            if (!features.contains(feature)) {
                features = features + feature
            }
        }
        return if (features.contains(feature)) feature.content
        else null
    }
}

@Composable
fun rememberAppState(
    vararg features: Feature,
) = remember(features) { ProgressivelyLoadingAppState(features.toSet()) }


object ClickUpFeature : Feature {
    override val name: String = "ClickUp"
    override val loaded: Boolean = true
    override val content: ContentBuilder<HTMLElement> = {
        (ClickUpMenu(rememberClickUpMenuViewModel(
            configurers = arrayOf(
                ClickUpHttpClientConfigurer(),
                ClickUpTestClientConfigurer(),
            ),
            initialState = Disabled,
            storage = localStorage.scoped("clickup")
        ).also { it.enable() }))
    }
}

object CustomFeature : Feature {
    override val name: String = "Custom"
    override val loaded: Boolean = true
    override val content: ContentBuilder<HTMLElement> = {
        val url = when (window.location.href.toUriOrNull()?.host) {
            "localhost" -> null
            else -> Url("https://start.me/p/0PBMOo/dkb")
        }
        Custom(url)
    }
}

@Composable
fun App(state: AppState = rememberAppState()) {

    Grid({
        style {
//            backgroundSize("cover")
//            backgroundImage("url(grayscale-gradient.svg)")
        }
    }) {
        Div({
            style {
                gridArea(Header)
                backgroundSize("cover")
                backgroundImage("url(steel-gradient.svg)")
            }
        }) { Header() }
        Div({
            style {
                gridArea(Links)
                center()
            }
        }) {
            Buttons({ v.icon().basic() }) {
                AnkerButton("https://start.me/p/4K6MOy/dashboard") { Icon("globe") }
                AnkerButton("https://home.bkahlert.com") { Icon("home") }
                AnkerButton("https://github.com/bkahlert") { Icon("github") }
                AnkerButton("https://console.aws.amazon.com/") { Icon("aws") }
            }
        }
        Div({
            style {
                gridArea(Search)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                alignContent(AlignContent.Center)
                justifyContent(JustifyContent.Center)
                padding(2.em)
            }
        }) {
            state.load(SearchFeature())?.invoke(this)
        }
        Div({
            style {
                gridArea(Tasks)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                alignContent(AlignContent.Center)
                justifyContent(JustifyContent.Center)
                padding(2.em, 2.em, 2.em, 0.em)
            }
        }) {
            state.load(ClickUpFeature)?.invoke(this)
        }
        Div({
            style {
                gridArea(CustomGradient)
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
        Div({
            style {
                gridArea(Custom)
                backgroundColor(CUSTOM_BACKGROUND_COLOR)
            }
        }) {
            state.load(CustomFeature)?.invoke(this)
        }
    }
}


fun main() {
    setupDemoDevMode(ClickUpDemos, SearchDemos, *SemanticUiDemoProviders)
    renderComposable("root") {
        Style(GridLayoutStylesheet)
        Style(ClickUpStyleSheet)
        App()
    }
}

@Composable
fun Grid(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable () -> Unit = {},
) {
    Div({
        classes(GridLayoutStylesheet.container)
        attrs?.invoke(this)
    }) {
        content()
    }
}


// https://developer.mozilla.org/en-US/docs/Web/CSS/grid-area
private fun <T : Enum<T>> StyleScope.gridArea(rowStart: T) {
    property("grid-area", rowStart.name)
}
