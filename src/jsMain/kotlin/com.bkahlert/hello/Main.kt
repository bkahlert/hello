package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.bkahlert.Brand
import com.bkahlert.hello.AppStylesheet.CUSTOM_BACKGROUND_COLOR
import com.bkahlert.hello.AppStylesheet.GRADIENT_HEIGHT
import com.bkahlert.hello.AppStylesheet.Grid.Custom
import com.bkahlert.hello.AppStylesheet.Grid.CustomGradient
import com.bkahlert.hello.AppStylesheet.Grid.Header
import com.bkahlert.hello.AppStylesheet.Grid.Links
import com.bkahlert.hello.AppStylesheet.Grid.Plugins
import com.bkahlert.hello.AppStylesheet.Grid.Search
import com.bkahlert.hello.AppStylesheet.Grid.Space
import com.bkahlert.hello.ProfileState.Loaded.Activating
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.clickup.Tag
import com.bkahlert.hello.clickup.Task
import com.bkahlert.hello.clickup.Team
import com.bkahlert.hello.clickup.TimeEntry
import com.bkahlert.hello.clickup.User
import com.bkahlert.hello.clickup.rest.AccessToken
import com.bkahlert.hello.clickup.rest.ClickupClient
import com.bkahlert.hello.custom.Custom
import com.bkahlert.hello.links.Header
import com.bkahlert.hello.links.Link
import com.bkahlert.hello.links.Links
import com.bkahlert.hello.plugins.ClickUp
import com.bkahlert.hello.plugins.errorMessage
import com.bkahlert.hello.search.Engine
import com.bkahlert.hello.search.Engine.Google
import com.bkahlert.hello.search.Search
import com.bkahlert.hello.test.mainTest
import com.bkahlert.kommons.Color.RGB
import com.bkahlert.kommons.Either
import com.bkahlert.kommons.fix.map
import com.bkahlert.kommons.fix.or
import com.bkahlert.kommons.fix.value
import com.bkahlert.kommons.runtime.LocalStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
import com.bkahlert.hello.clickup.Space as ClickupSpace

sealed interface AppState {
    object Loading : AppState
    object Ready : AppState
    object FullLoaded : AppState
}

sealed interface ProfileState {
    object Disconnected : ProfileState

    object Loading : ProfileState

    sealed class Loaded(
        protected val client: ClickupClient,
        val user: User,
        val teams: List<Team>,
        protected val update: (suspend (ProfileState) -> ProfileState) -> Unit,
    ) : ProfileState {

        protected val logger = simpleLogger()

        fun activate(team: Team) {
            logger.info("Activating ${user.username}@${team.name}")
            update { Activated(client, user, teams, team, client.getRunningTimeEntry(team, user), update) }
        }

        class Activating(
            client: ClickupClient,
            user: User,
            teams: List<Team>,
            update: (suspend (ProfileState) -> ProfileState) -> Unit,
        ) : Loaded(client, user, teams, update)

        open class Activated(
            client: ClickupClient,
            user: User,
            teams: List<Team>,
            val activeTeam: Team,
            val runningTimeEntry: Response<TimeEntry?>,
            update: (suspend (ProfileState) -> ProfileState) -> Unit,
        ) : Loaded(client, user, teams, update) {

            fun prepare() {
                update { Searchable(client, user, teams, activeTeam, runningTimeEntry, client.getTasks(activeTeam), update) }
            }

            fun startTimeEntry() {
                val tomato = RGB(0xff6347)
                val tomatoSauce = RGB(0xb21807)
                update {
                    logger.log("starting time entry")
                    val xxx: Either<TimeEntry, Throwable> = client.startTimeEntry(
                        activeTeam,
                        Task.ID("20jg1er"),
                        "50m pomodoro",
                        false,
                        Tag("pomodoro", tomato, tomato, null),
                    )
                    Activated(client, user, teams, activeTeam, xxx, update)
                }
            }

            fun stopTimeEntry() {
                update {
                    logger.info("stopping time entry")
                    when (val response = client.stopTimeEntry(activeTeam)) {
                        is Success -> console.log("stopped", response.value)
                        is Failure -> console.error("failed to stop", response.value.errorMessage)
                    }
                    this
                }
            }
        }

        open class Searchable(
            client: ClickupClient,
            user: User,
            teams: List<Team>,
            activeTeam: Team,
            runningTimeEntry: Response<TimeEntry?>,
            val tasks: Response<List<Task>>,
            update: (suspend (ProfileState) -> ProfileState) -> Unit,
        ) : Activated(client, user, teams, activeTeam, runningTimeEntry, update)
    }

    data class Failed(
        val exceptions: List<Throwable>,
    ) : ProfileState {
        constructor(vararg exceptions: Throwable) : this(exceptions.toList())

        val message: String
            get() = exceptions.firstNotNullOfOrNull { it.message } ?: "message missing"
    }

    companion object {
        fun of(
            client: ClickupClient,
            userResponse: Response<User>,
            teamsResponse: Response<List<Team>>,
            update: (suspend (ProfileState) -> ProfileState) -> Unit,
        ): ProfileState {
            if (userResponse == null || teamsResponse == null) return Loading
            return userResponse.map { user ->
                teamsResponse.map { Activating(client, user, it, update) } or { Failed(it) }
            } or { ex ->
                userResponse.map { Failed(ex) } or { Failed(ex, it) }
            }
        }
    }
}

/** A response that did not arrive yet or either succeeded or failed. */
typealias Response<T> = Either<out T, Throwable>?
typealias Success<A, B> = Either.Left<A, B>
typealias Failure<A, B> = Either.Right<A, B>


class AppModel(private val config: Config) {

    private val logger = simpleLogger()

    private val _appState = MutableStateFlow<AppState>(AppState.Loading)
    val appState = _appState.asStateFlow()

    private val _engine = MutableStateFlow(LocalStorage["engine", { Engine.valueOf(it) }] ?: Google)
    val engine = _engine.asStateFlow()

    fun change(engine: Engine) {
        LocalStorage["engine"] = engine
        _engine.update { engine }
    }

    fun searchReady() {
        if (appState.value != AppState.Loading) return
        _appState.update { AppState.Ready }
    }

    // flow starts with access token
    private val _clickupTokenState = MutableStateFlow(AccessToken.load())
    fun configureClickUp(accessToken: AccessToken) {
        logger.debug("setting access token")
        _clickupTokenState.update { accessToken }
    }

    // save access token in locally
    // if access token is missing use fallback if set
    // result is saved
    private val _savedClickupTokenState = _clickupTokenState
        .onEach { it?.save() }
        .map { it ?: config.clickup.fallbackAccessToken }

    // mutable state to store a lambda that can update an existing profile state
    private val _updateState = MutableStateFlow<(suspend (ProfileState) -> ProfileState)> { it }
    private fun update(update: suspend (ProfileState) -> ProfileState) {
        _updateState.update { update }
    }

    // actual profile state deducted from an initial one
    // and update state applied to it
    val profileState: Flow<ProfileState> = _savedClickupTokenState
        .combine(appState) { token, appState ->
            if (appState == AppState.Loading) {
                ProfileState.Loading
            } else {
                token?.let(::ClickupClient)?.let {
                    ProfileState.of(it, it.getUser(), it.getTeams(), ::update)
                } ?: ProfileState.Disconnected
            }
        }
        .combine(_updateState) { profile, update -> update(profile) }
}


fun main() {

    // trigger creation to avoid flickering
    Engine.values().forEach {
        it.grayscaleImage
        it.coloredImage
    }

    if (AppConfig.uiOnly) {
        mainTest()
        return
    }

    renderComposable("root") {
        Style(AppStylesheet)
        val appState = remember { AppModel(AppConfig) }
        val loadingState by appState.appState.collectAsState()
        val engine by appState.engine.collectAsState()
        val profileState by appState.profileState.collectAsState(null)

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
                Search(
                    engine,
                    onEngineChange = appState::change,
                    onReady = appState::searchReady,
                )
            }
            Div({
                style {
                    gridArea(Plugins)
                }
            }) {
                profileState?.also {
                    ClickUp(
                        profileState = it,
                        onConnect = { details ->
                            details(AppConfig.clickup.fallbackAccessToken, appState::configureClickUp)
                        },
                    )
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
            }) { }
            Div({
                style {
                    gridArea(Custom)
                    backgroundColor(CUSTOM_BACKGROUND_COLOR)
                }
            }) {
                when (loadingState) {
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
        Links, Header, Search, Plugins, Space, CustomGradient, Custom
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
//            padding(0.px)
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
            "$Links $Plugins",
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
//                gridTemplateColumns("1fr 1fr 1fr 1fr") TODO restore
//                gridTemplateRows("$HEADER_HEIGHT 80px 0 0 1fr")  TODO restore
                gridTemplateColumns("1fr 1fr 1fr 3fr")
                gridTemplateRows("$HEADER_HEIGHT 640px 0 0 1fr")
                gridTemplateAreas(
                    "$Header $Header $Header $Header",
                    "$Links $Search $Search $Plugins",
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
