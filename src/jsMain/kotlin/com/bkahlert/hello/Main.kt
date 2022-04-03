package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.bkahlert.Brand
import com.bkahlert.hello.AppState.Loading
import com.bkahlert.hello.AppStylesheet.CUSTOM_BACKGROUND_COLOR
import com.bkahlert.hello.AppStylesheet.GRADIENT_HEIGHT
import com.bkahlert.hello.AppStylesheet.Grid.Custom
import com.bkahlert.hello.AppStylesheet.Grid.CustomGradient
import com.bkahlert.hello.AppStylesheet.Grid.Header
import com.bkahlert.hello.AppStylesheet.Grid.Links
import com.bkahlert.hello.AppStylesheet.Grid.Margin
import com.bkahlert.hello.AppStylesheet.Grid.Plugins
import com.bkahlert.hello.AppStylesheet.Grid.Search
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.custom.Custom
import com.bkahlert.hello.links.Header
import com.bkahlert.hello.plugins.clickup.ClickUpMenu
import com.bkahlert.hello.plugins.clickup.ClickUpModel
import com.bkahlert.hello.search.PasteHandlingMultiSearchInput
import com.bkahlert.hello.search.SearchEngine
import com.bkahlert.hello.search.SearchThing
import com.bkahlert.hello.ui.ViewportDimension
import com.bkahlert.hello.ui.center
import com.bkahlert.hello.ui.demo.DebugUI
import com.bkahlert.hello.ui.gridArea
import com.bkahlert.hello.ui.linearGradient
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.default
import com.semanticui.compose.element.AnkerButton
import com.semanticui.compose.element.ButtonGroupElementType.Icon
import com.semanticui.compose.element.Buttons
import com.semanticui.compose.element.Icon
import com.semanticui.compose.module.Content
import com.semanticui.compose.module.Modal
import com.semanticui.compose.module.autofocus
import com.semanticui.compose.module.blurring
import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.CSSBuilder
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitLength
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.and
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundSize
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
import org.jetbrains.compose.web.css.media
import org.jetbrains.compose.web.css.mediaMinHeight
import org.jetbrains.compose.web.css.mediaMinWidth
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.transform
import org.jetbrains.compose.web.css.unaryMinus
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.url.URL

// TODO start tasks
// TODO clock / timer of passed time
// TODO detect updates
// TODO loader animation on null Responses (not yet finished)
// TODO semantic UI progress https://semantic-ui.com/modules/progress.html#attached at top of page for Pomodoro timer

sealed interface AppState {
    object Loading : AppState
    object Ready : AppState
    object FullLoaded : AppState
}


class AppModel(storage: Storage = InMemoryStorage()) {
    private var recentlyUsedSearchEngine by storage default SearchEngine.Default

    private val logger = simpleLogger()

    private val _appState = MutableStateFlow<AppState>(Loading)
    val appState = _appState.asStateFlow()

    private val _engine = MutableStateFlow(recentlyUsedSearchEngine)
    val engine = _engine.asStateFlow()

    fun change(searchEngine: SearchEngine) {
        recentlyUsedSearchEngine = searchEngine
        _engine.update { searchEngine }
    }

    fun searchReady() {
        if (appState.value != Loading) return
        _appState.update { AppState.Ready }
    }
}

fun main() {

    // trigger creation to avoid flickering
    SearchEngine.values().forEach {
        it.grayscaleImage
        it.coloredImage
    }

    DebugMode(
        storage = localStorage.scoped("debug"),
    ) {

        Modal(Unit, {
            +Fullscreen
            +Long
            blurring = false // true will blur popups inside the debug mode, too
            autofocus = false
        }) {
            Content {
                DebugUI()
            }

        }
    }

    renderComposable("root") {
        Style(AppStylesheet)

        val appModel = remember { AppModel(localStorage.scoped("hello")) }
        val loadingState by appModel.appState.collectAsState()
        val engine by appModel.engine.collectAsState()

        @Suppress("SpellCheckingInspection")
        val clickupModel = remember { ClickUpModel(storage = localStorage.scoped("clickup")) }
        console.warn("START")
        when (loadingState) {
            is Loading -> {}
            else -> clickupModel.initialize()
        }

        Grid({
            style {
                backgroundSize("cover")
                backgroundImage("url(grayscale-gradient.svg)")
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
                Buttons(Icon, { +Basic }) {
                    AnkerButton("https://start.me/p/4K6MOy/dashboard") { Icon("globe") }
                    AnkerButton("https://home.bkahlert.com") { Icon("home") }
                    AnkerButton("https://github.com/bkahlert") { Icon("github") }
                }
            }
            Div({
                style {
                    gridArea(Search)
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    alignContent(AlignContent.Center)
                    justifyContent(JustifyContent.Center)
                    padding(2.em, 2.em, 2.em, 0.em)
                }
            }) {
                if (false) {
                    SearchThing(
                        engine,
                        onEngineChange = appModel::change,
                        onReady = appModel::searchReady,
                    )
                } else {
                    PasteHandlingMultiSearchInput()
                }
            }
            Div({
                style {
                    gridArea(Plugins)
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    alignContent(AlignContent.Center)
                    justifyContent(JustifyContent.Center)
                    padding(2.em, 2.em, 2.em, 0.em)
                }
            }) {
                // TODO delay until AppState.Ready
                when (loadingState) {
//                    is AppState.Loading -> {}
                    else -> ClickUpMenu(clickupModel)
                }
            }
            Div({
                style {
                    gridArea(CustomGradient)
                    property("z-index", "1")
                    height(GRADIENT_HEIGHT)
                    transform { translateY(-GRADIENT_HEIGHT / 2) }
                    backgroundColor(Color.transparent)
                    backgroundImage(linearGradient(CUSTOM_BACKGROUND_COLOR.transparentize(0),
                        CUSTOM_BACKGROUND_COLOR,
                        CUSTOM_BACKGROUND_COLOR.transparentize(0)))
                }
            })
            Div({
                style {
                    gridArea(Custom)
                    backgroundColor(CUSTOM_BACKGROUND_COLOR)
                }
            }) {
                when (loadingState) {
                    AppState.Ready -> Custom(URL("https://start.me/p/0PBMOo/dkb"))
                    AppState.FullLoaded -> Custom(URL("https://start.me/p/0PBMOo/dkb"))
                    else -> Custom(null)
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

object AppStylesheet : StyleSheet() {

    val HEADER_HEIGHT: CSSSizeValue<out CSSUnitLength> = 4.px
    val GRADIENT_HEIGHT: CSSSizeValue<out CSSUnitLength> = 0.3.cssRem
    val CUSTOM_BACKGROUND_COLOR = Brand.colors.white

    enum class Grid {
        Links, Header, Search, Plugins, Margin, CustomGradient, Custom
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

        gridTemplateColumns("1fr 2fr")
        gridTemplateRows("0 10fr 7fr 1fr 0 0")
        gridTemplateAreas(
            "$Header $Header",
            "$Search $Search",
            "$Links $Plugins",
            "$Margin $Margin",
            "$CustomGradient $CustomGradient",
            "$Custom $Custom",
        )

        // TODO check auf Handy
        media(mediaMinWidth(ViewportDimension.medium) and mediaMinHeight(250.px)) {
            self style {
                gridTemplateRows("$HEADER_HEIGHT 80px 45px 15px 0 1fr")
            }
        }

        media(mediaMinWidth(ViewportDimension.large) and mediaMinHeight(250.px)) {
            self style {
                // minmax enforces cell content to not consume more space
                // https://css-tricks.com/preventing-a-grid-blowout/
                gridTemplateColumns("1fr 1fr 1fr minmax(0, 2fr)")
                gridTemplateRows("$HEADER_HEIGHT 80px 0 0 1fr")
                gridTemplateAreas(
                    "$Header $Header $Header $Header",
                    "$Links $Search $Search $Plugins",
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
