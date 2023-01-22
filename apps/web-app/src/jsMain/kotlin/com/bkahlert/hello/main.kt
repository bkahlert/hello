package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.Brand
import com.bkahlert.hello.AppStylesheet.CUSTOM_BACKGROUND_COLOR
import com.bkahlert.hello.AppStylesheet.GRADIENT_HEIGHT
import com.bkahlert.hello.AppStylesheet.Grid.Custom
import com.bkahlert.hello.AppStylesheet.Grid.CustomGradient
import com.bkahlert.hello.AppStylesheet.Grid.Header
import com.bkahlert.hello.AppStylesheet.Grid.Links
import com.bkahlert.hello.AppStylesheet.Grid.Margin
import com.bkahlert.hello.AppStylesheet.Grid.Search
import com.bkahlert.hello.AppStylesheet.Grid.Tasks
import com.bkahlert.hello.clickup.client.http.ClickUpHttpClientConfigurer
import com.bkahlert.hello.clickup.demo.ClickUpDemos
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.hello.custom.Custom
import com.bkahlert.hello.custom.linearGradient
import com.bkahlert.hello.debug.search.SearchDemos
import com.bkahlert.hello.ui.ViewportDimension
import com.bkahlert.hello.ui.header.Header
import com.bkahlert.hello.ui.header.center
import com.bkahlert.hello.ui.search.SearchFeature
import com.bkahlert.kommons.binding.Binding
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.net.host
import com.bkahlert.kommons.net.toUriOrNull
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.custom.Length
import com.bkahlert.semanticui.custom.backgroundColor
import com.bkahlert.semanticui.custom.zIndex
import com.bkahlert.semanticui.demo.SemanticUiDemoProviders
import com.bkahlert.semanticui.devmode.asMutableState
import com.bkahlert.semanticui.devmode.setupDemoDevMode
import com.bkahlert.semanticui.element.AnkerButton
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Buttons
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.basic
import com.bkahlert.semanticui.element.icon
import io.ktor.http.Url
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.CSSBuilder
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.css.bottom
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.div
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.gap
import org.jetbrains.compose.web.css.gridTemplateAreas
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.css.gridTemplateRows
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.left
import org.jetbrains.compose.web.css.media
import org.jetbrains.compose.web.css.mediaMinWidth
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.times
import org.jetbrains.compose.web.css.transform
import org.jetbrains.compose.web.css.unaryMinus
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

// TODO create tasks
// TODO rename tasks
// TODO semantic UI progress https://semantic-ui.com/modules/progress.html#attached at top of page for Pomodoro timer

interface Feature {
    val name: String
    val loaded: Boolean
    val content: @Composable DOMScope<HTMLElement>.() -> Unit
}

@Stable
interface AppState {
    val features: Set<Feature>
    fun load(feature: Feature): (@Composable DOMScope<HTMLElement>.() -> Unit)?
}

class ProgressivelyLoadingAppState(
    initialFeatures: Set<Feature>,
) : AppState {
    override var features by mutableStateOf(initialFeatures)
        private set

    override fun load(feature: Feature): (@Composable DOMScope<HTMLElement>.() -> Unit)? {
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
    override val content: @Composable DOMScope<HTMLElement>.() -> Unit = {
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
    override val content: @Composable DOMScope<HTMLElement>.() -> Unit = {
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
    val devModeBinding = setupDemoDevMode(*SemanticUiDemoProviders, ClickUpDemos, SearchDemos)
    renderComposable("root") {
        DevModeToggle(devModeBinding)
        Style(AppStylesheet)
        Style(ClickUpStyleSheet)
        App()
    }
}

@Composable
fun DevModeToggle(
    binding: Binding<Boolean>,
) {
    var active by binding.asMutableState()
    Menu({
        classes("compact", "tiny", "secondary")
        style {
            position(Position.Fixed)
            bottom(1.em)
            left(0.5.em)
            zIndex(2000)
        }
    }) {
        Item {
            Button({
                classes("animated", "fade", "teal", "basic")
                tabIndex(0)
                onClick { active = !active }
            }) {
                if (active) {
                    S("visible", "content") { Icon("close") }
                    S("hidden", "content") { Text("Close") }
                } else {
                    S("visible", "content") { Icon("wrench"); Text("F4") }
                    S("hidden", "content") { Text("Debug") }
                }
            }
        }
    }
}

@Composable
fun Grid(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable () -> Unit = {},
) {
    Div({
        classes(AppStylesheet.helloGridContainer)
        attrs?.invoke(this)
    }) {
        content()
    }
}


// https://developer.mozilla.org/en-US/docs/Web/CSS/grid-area
private fun <T : Enum<T>> StyleScope.gridArea(rowStart: T) {
    property("grid-area", rowStart.name)
}

object AppStylesheet : StyleSheet() {

    val HEADER_HEIGHT: Length = 4.px
    val GRADIENT_HEIGHT: Length = 0.3.cssRem
    val MIN_HEIGHT: Length = 2.5.cssRem
    val CUSTOM_BACKGROUND_COLOR = Brand.colors.white

    enum class Grid {
        Links, Header, Search, Tasks, Margin, CustomGradient, Custom
    }

    init {
        id("root") style {
            overflow("hidden")
        }
    }


    val helloGridContainer by style {
        display(DisplayStyle.Grid)
        alignContent(AlignContent.Center)
        justifyContent(JustifyContent.Center)
        width(100.vw)
        height(100.vh)
        gap(0.px, 0.px)

        // Links, Tasks and Search in separate rows
        gridTemplateColumns("1fr 2fr")
        gridTemplateRows("$HEADER_HEIGHT ${MIN_HEIGHT * 2} ${MIN_HEIGHT * 1.5} ${MIN_HEIGHT * 2} ${MIN_HEIGHT * 0.5} 0 1fr")
        gridTemplateAreas(
            "$Header $Header",
            "$Links $Links",
            "$Tasks $Tasks",
            "$Search $Search",
            "$Margin $Margin",
            "$CustomGradient $CustomGradient",
            "$Custom $Custom",
        )

        // Links, Tasks and Search in two rows at top
        media(mediaMinWidth(ViewportDimension.medium)) {
            self style {
                gridTemplateRows("$HEADER_HEIGHT ${MIN_HEIGHT * 1.5} ${MIN_HEIGHT * 1.5} ${MIN_HEIGHT * 0.5} 0 1fr")
                gridTemplateAreas(
                    "$Header $Header",
                    "$Links $Tasks",
                    "$Search $Search",
                    "$Margin $Margin",
                    "$CustomGradient $CustomGradient",
                    "$Custom $Custom",
                )
            }
        }

        // Links, Tasks and Search in one row at top
        media(mediaMinWidth(ViewportDimension.xLarge)) {
            self style {
                // minmax enforces cell content to not consume more space
                // https://css-tricks.com/preventing-a-grid-blowout/
                gridTemplateColumns("1fr 1fr 1fr minmax(0, 3fr)")
                gridTemplateRows("$HEADER_HEIGHT ${MIN_HEIGHT * 2} $GRADIENT_HEIGHT 0 1fr")
                gridTemplateAreas(
                    "$Header $Header $Header $Header",
                    "$Links $Search $Search $Tasks",
                    "$Margin $Margin $Margin $Margin",
                    "$CustomGradient $CustomGradient $CustomGradient $CustomGradient",
                    "$Custom $Custom $Custom $Custom",
                )
            }
        }
    }
}

object Mixins {
    val CSSBuilder.center: Unit
        get() {
            display(DisplayStyle.Flex)
            alignContent(AlignContent.Center)
            alignItems(AlignItems.Center)
            flexDirection(FlexDirection.Row)
            flexWrap(FlexWrap.Nowrap)
            justifyContent(JustifyContent.SpaceAround)
        }
}
