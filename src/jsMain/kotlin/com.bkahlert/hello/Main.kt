package com.bkahlert.hello

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.bkahlert.hello.clickup.User
import com.bkahlert.hello.custom.Custom
import com.bkahlert.hello.integration.ClickUp
import com.bkahlert.hello.links.Header
import com.bkahlert.hello.links.Link
import com.bkahlert.hello.links.Links
import com.bkahlert.hello.search.Engine
import com.bkahlert.hello.search.Engine.Google
import com.bkahlert.hello.search.Search
import com.bkahlert.kommons.browser.delayed
import com.bkahlert.kommons.runtime.LocalStorage
import com.bkahlert.kommons.time.seconds
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.BearerTokens
import io.ktor.client.features.auth.providers.bearer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.formUrlEncode
import kotlinx.browser.window
import kotlinx.serialization.Serializable
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

val token = "pk_4687596_P3N26P81XHQLEJD3DBDLO3SWU25L38E0"


@Serializable
data class TokenInfo(
    val access_token: String,
)

@Serializable
data class Error(
    val err: String,
    val ECODE: String,
)

@OptIn(ExperimentalComposeWebApi::class)
suspend fun main() {

    // trigger creation to avoid flickering
    Engine.values().forEach {
        it.grayscaleImage
        it.coloredImage
    }

    val tokenClient = HttpClient(Js) {
        install(JsonFeature) {
            serializer = Serializer
        }
    }

//    val clickUpUrl = "https://api.clickup.com/api"
    val clickUpUrl = "http://localhost:8080/api"

    val client = HttpClient(Js) {
        install(JsonFeature) {
            serializer = Serializer
        }
        install(Auth) {
            val code = Url(window.location.href).parameters.get("code")
            val error = Url(window.location.href).parameters.get("error")
            console.info("code", code)
            console.info("code", error)
            if (code == null) {
                if (error != null) {
                    console.error("failed", error)
                } else {
                    val authorizationUrlQuery = Parameters.build {
                        append("client_id", "GN6516W1PG9IHB9E9O4ITESONC9SP8V7")
                        append("redirect_uri", "http://localhost:8080")
                    }.formUrlEncode()
                    val url = "https://app.clickup.com/api?$authorizationUrlQuery"
                    console.info("redirect", url)
                    window.location.href = url
                }
            } else {
//                http://localhost:8080/?code=K6OFL2CRA88IKENUIMBIARR27O7QWP7J
                lateinit var tokenInfo: TokenInfo

                bearer {
                    loadTokens {
                        window.alert("load tokens: $clickUpUrl")
                        val url = Url("$clickUpUrl/v2/oauth/token?" + Parameters.build {
                            append("client_id", "GN6516W1PG9IHB9E9O4ITESONC9SP8V7")
                            append("client_secret", "VQ19OLVF41A01U7S8LBJWMS8NUZQGYRDYR3L2PK3FG826JHCXFSB99KATRE8DKZJ")
                            append("code", code)
                        }.formUrlEncode())
                        console.info("load tokens", url)
                        // TODO check if code was already used
                        tokenInfo = kotlin.runCatching {
                            window.alert("load tokens: $clickUpUrl -- $url")
                            tokenClient.post<TokenInfo>(url)
                        }.getOrElse {
                            window.alert(it.stackTraceToString())
                            console.error(it.message, it.cause)
                            window.location.href = "http://localhost:8080?" + Parameters.build {
                                append("error", it.stackTraceToString())
                            }.formUrlEncode()
                            throw it
                        }
                        BearerTokens(tokenInfo.access_token, tokenInfo.access_token)
                    }
                }
            }
        }
//            val response: HttpResponse = client.get("$clickUpUrl/v2/team") {
//
//            }
//            response.receive<Any?>().also { console.warn(it) }
//            val location: List<Team> = response.receive()
//            console.info(location)
    }

    val user: User = client.get("$clickUpUrl/v2/user")
    window.alert(user.toString())

    renderComposable("root") {
        Style(AppStylesheet)

        val (canSearch, setCanSearch) = remember { mutableStateOf(false) }

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
                Search(LocalStorage["engine", { Engine.valueOf(it) }] ?: Google,
                    onEngineChange = { LocalStorage["engine"] = it },
                    onFocusChange = { hasFocus ->
                        if (hasFocus && !canSearch) {
                            delayed(.5.seconds) { setCanSearch(true) }
                        }
                    })
            }
            Div({
                style {
                    gridArea(Options)
                }
            }) {
                ClickUp()
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
            }) { Custom(if (canSearch) URL("https://start.me/p/0PBMOo/dkb") else null) }
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
        attrs?.also { apply(it) }
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
