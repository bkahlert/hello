package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.Brand
import com.bkahlert.hello.AppStylesheet.CUSTOM_BACKGROUND_COLOR
import com.bkahlert.hello.AppStylesheet.GRADIENT_HEIGHT
import com.bkahlert.hello.AppStylesheet.Grid.Custom
import com.bkahlert.hello.AppStylesheet.Grid.CustomGradient
import com.bkahlert.hello.AppStylesheet.Grid.Header
import com.bkahlert.hello.AppStylesheet.Grid.Links
import com.bkahlert.hello.AppStylesheet.Grid.Options
import com.bkahlert.hello.AppStylesheet.Grid.Search
import com.bkahlert.hello.AppStylesheet.Grid.Space
import com.bkahlert.hello.LoadingState.PartiallyLoaded
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.clickup.ClickUpApiClient
import com.bkahlert.hello.clickup.Team
import com.bkahlert.hello.clickup.User
import com.bkahlert.hello.custom.Custom
import com.bkahlert.hello.integration.ClickUp
import com.bkahlert.hello.links.Header
import com.bkahlert.hello.links.Link
import com.bkahlert.hello.links.Links
import com.bkahlert.hello.search.Engine
import com.bkahlert.hello.search.Engine.Google
import com.bkahlert.hello.search.Search
import com.bkahlert.kommons.Either
import com.bkahlert.kommons.fix.map
import com.bkahlert.kommons.fix.or
import com.bkahlert.kommons.runtime.LocalStorage
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.web.ExperimentalComposeWebApi
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
import org.jetbrains.compose.web.css.boxSizing
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.div
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.gap
import org.jetbrains.compose.web.css.gridTemplateAreas
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.css.gridTemplateRows
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.media
import org.jetbrains.compose.web.css.mediaMinHeight
import org.jetbrains.compose.web.css.mediaMinWidth
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.selectors.CSSSelector
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

enum class LoadingState { Loading, PartiallyLoaded, FullLoaded }

sealed interface ProfileState {
    object Loading : ProfileState

    data class Data(
        val user: User,
        val teams: List<Team>,
    ) : ProfileState

    data class Error(
        val exceptions: List<Throwable>,
    ) : ProfileState {
        constructor(vararg exceptions: Throwable) : this(exceptions.toList())

        val message: String
            get() = exceptions.firstNotNullOfOrNull { it.message } ?: "message missing"
    }
}

class AppModel(private val backgroundScope: CoroutineScope) {

    init {
        console.warn("New AppModel")
    }

    private val logger = simpleLogger()

    private val _loading = MutableStateFlow(LoadingState.Loading) // private mutable state flow
    val loading = _loading.asStateFlow() // publicly exposed as read-only state flow

    private val _engine = MutableStateFlow(LocalStorage["engine", { Engine.valueOf(it) }] ?: Google)
    val engine = _engine.asStateFlow()

    fun change(engine: Engine) {
        LocalStorage["engine"] = engine
        _engine.update { engine }
    }

    fun partiallyLoaded() {
        when (loading.value) {
            LoadingState.Loading -> {
                logger.log("${loading.value} -> $PartiallyLoaded")
                _loading.update { PartiallyLoaded }
            }
            else -> {
                logger.log("${loading.value} >= $PartiallyLoaded")
            }
        }
    }

    private val _clickUpAccessToken = MutableStateFlow(LocalStorage[ACCESS_TOKEN_STORAGE_KEY])
    private val _clickUpApiClient = MutableStateFlow(ClickUpApiClient(_clickUpAccessToken.asStateFlow()))
    val clickUpApiClient = _clickUpApiClient.asStateFlow()

    private val _clickUpUser: Flow<Either<User, Throwable>> =
        _clickUpApiClient.mapLatest { it.getUser() }
    private val _clickUpTeams: Flow<Either<List<Team>, Throwable>> =
        _clickUpApiClient.mapLatest { it.getTeams() }
    val profile: Flow<ProfileState?> = _clickUpUser.combine(_clickUpTeams) { user: Either<User, Throwable>, teams: Either<List<Team>, Throwable> ->
        console.warn(user, "A")
        console.warn(teams, "B")
        console.warn(teams, "!: " + listOf(user, teams).filterIsInstance<Throwable>().size)
        console.warn(teams, "!: " + listOf(user, teams).filterIsInstance<Throwable>().size)

        val x = user.map { theUser ->
            teams.map { ProfileState.Data(theUser, it) } or { ProfileState.Error(it) }
        } or { ex ->
            user.map { ProfileState.Error(ex) } or { ProfileState.Error(ex, it) }
        }

        console.warn(x)

        x
    }

    fun configureClickUp(accessToken: String) {
        LocalStorage[ACCESS_TOKEN_STORAGE_KEY] = accessToken
        _clickUpAccessToken.update { accessToken }
//        backgroundScope.launch {
//            clickUpApiClient.value.run {
//                getUser { user -> _clickUpUser.update { user } }
//                getTeams { teams -> _clickUpTeams.update { teams } }
//            }
//        }
    }

    companion object {
        private const val ACCESS_TOKEN_STORAGE_KEY = "clickup.access-token"
    }
}

@OptIn(ExperimentalComposeWebApi::class)
fun main() {


    // trigger creation to avoid flickering
    Engine.values().forEach {
        it.grayscaleImage
        it.coloredImage
    }

    renderComposable("root") {
        Style(AppStylesheet)

        val backgroundScope = rememberCoroutineScope()
        val appState = remember { AppModel(backgroundScope) }
        val loadingState by appState.loading.collectAsState()
        val engine by appState.engine.collectAsState()
        val profile by appState.profile.collectAsState(null)


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
                Links {
                    Link("https://start.me/p/4K6MOy/dashboard", "start.me", "web.svg")
                    Link("https://home.bkahlert.com", "home", "home.svg")
                    Link("https://clickup.com/", "clickup", "clickup.svg")
                }
            }
            Div({
                style {
                    gridArea(Search)
                }
            }) {
//                Search(value = "all engines (focussed)", allEngines = true)
//                Search(value = "all engines", allEngines = true)
//                Search(value = "single engine")
//                Search(allEngines = true)
                Search(engine,
                    onEngineChange = { appState.change(it) },
                    onReady = { appState.partiallyLoaded() })
            }
            Div({
                style {
                    gridArea(Options)
                }
            }) {
                ClickUp(
                    profile = profile,
                    onConfig = {
                        window.prompt("""
                            Currently, OAuth2 is not supported yet.
                            
                            To use this feature at its current state,
                            please enter your personal ClickUp API token.
                            
                            More information can be found on https://clickup.com/api
                            """.trimIndent(), "pk_4687596_XQ7VFO3V06T6TE6FJI3A6UY6EY3LBKYI")
                            ?.also {
                                appState.configureClickUp(it)
                            }
                    },
                )
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
            }) { }
            Div({
                style {
                    gridArea(Custom)
                    backgroundColor(CUSTOM_BACKGROUND_COLOR)
                }
            }) {
                when (loadingState) {
                    LoadingState.FullLoaded -> Custom(URL("https://start.me/p/0PBMOo/dkb"))
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
        Links, Header, Search, Options, Space, CustomGradient, Custom
    }

    init {
        "html, body, #root" style {
            overflow("hidden")
            height(100.percent)
            margin(0.px)
            padding(0.px)
            backgroundColor(Color.transparent)
            backgroundImage("none")
            fontFamily(Brand.fonts)
        }

        // `universal` can be used instead of "*": `universal style {}`
        "*" style {
//            fontSize(35.px)
            padding(0.px)
        }

        "*, ::after, ::before" style {
            boxSizing("border-box")
        }

        // raw selector
        "h1, h2, h3, h4, h5, h6" style {
            property("font-family", "Arial, Helvetica, sans-serif")

        }

        // combined selector
        type("A") + attr( // selects all tags <a> with href containing 'jetbrains'
            name = "href",
            value = "jetbrains",
            operator = CSSSelector.Attribute.Operator.Equals
        ) style {
            fontSize(25.px)
        }
    }

    val helloGridContainer by style {
        display(DisplayStyle.Grid)
        alignContent(AlignContent.Center)
        justifyContent(JustifyContent.Center)
        width(100.vw)
        height(100.vh)
        gap(0.px, 0.px)

        gridTemplateColumns("1fr 1fr")
        gridTemplateRows("0 10fr 7fr 1fr 0 0")
        gridTemplateAreas(
            "$Header $Header",
            "$Search $Search",
            "$Links $Options",
            "$Space $Space",
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
                gridTemplateColumns("1fr 1fr 1fr 1fr")
                gridTemplateRows("$HEADER_HEIGHT 80px 0 0 1fr")
                gridTemplateAreas(
                    "$Header $Header $Header $Header",
                    "$Links $Search $Search $Options",
                    "$Space $Space $Space $Space",
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
