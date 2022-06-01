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
import com.bkahlert.hello.AppStylesheet.Grid.Plugins
import com.bkahlert.hello.AppStylesheet.Grid.Search
import com.bkahlert.hello.clickup.api.Task
import com.bkahlert.hello.clickup.ui.ClickUpMenu
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.ui.ClickUpStyleSheet
import com.bkahlert.hello.clickup.ui.rememberClickUpMenuViewModel
import com.bkahlert.hello.custom.Custom
import com.bkahlert.hello.debug.clickup.ClickUpFixtures
import com.bkahlert.hello.debug.renderDebugMode
import com.bkahlert.hello.links.Header
import com.bkahlert.hello.semanticui.element.AnkerButton
import com.bkahlert.hello.semanticui.element.ButtonGroupElementType.Icon
import com.bkahlert.hello.semanticui.element.Buttons
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.ui.ViewportDimension
import com.bkahlert.hello.ui.center
import com.bkahlert.hello.ui.gridArea
import com.bkahlert.hello.ui.linearGradient
import com.bkahlert.hello.ui.search.SearchFeature
import com.bkahlert.kommons.compose.Length
import com.bkahlert.kommons.debug.Object
import com.bkahlert.kommons.debug.console
import com.bkahlert.kommons.debug.entries
import com.bkahlert.kommons.debug.grouping
import com.bkahlert.kommons.debug.inspect
import com.bkahlert.kommons.debug.inspectJs
import com.bkahlert.kommons.debug.jsonTable
import com.bkahlert.kommons.debug.toJson
import com.bkahlert.kommons.debug.trace
import com.bkahlert.kommons.debug.traceJs
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.url
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
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.js.Json
import kotlin.reflect.KClass

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
        @Suppress("SpellCheckingInspection")
        ClickUpMenu(rememberClickUpMenuViewModel(initialState = Disabled, storage = localStorage.scoped("clickup"))
            .also { it.enable() })
    }
}

object CustomFeature : Feature {
    override val name: String = "Custom"
    override val loaded: Boolean = true
    override val content: @Composable DOMScope<HTMLElement>.() -> Unit = {
        val url = if (window.location.url.host == "localhost") null else Url("https://start.me/p/0PBMOo/dkb")
        Custom(url)
    }
}

@Composable
fun App(state: AppState = rememberAppState()) {

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
                padding(2.em)
            }
        }) {
            state.load(SearchFeature())?.invoke(this)
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

    renderDebugMode {
        val task = ClickUpFixtures.task()
        val data = Object.keys(task)

        console.table(data)
        console.grouping("Test log") {
            console.info(ClickUpFixtures.User)
            console.jsonTable(*(0..100).map { ClickUpFixtures.task() }.toTypedArray())
        }
        console.table(arrayOf(task.toJson()))
        console.table(task.entries.toJson())
        console.table(task)
        console.table(listOf(task))
        console.table(task, arrayOf("id_1", "name_1"))
        console.table(arrayOf(task), arrayOf("id_1", "name_1"))
        val json: Json = JSON.parse<Json>(task.serialize())
        console.log(json)
        println(json)
//        println(json.to)
        val clazz: KClass<Task> = Task::class

    }
    renderComposable("root") {
        Style(AppStylesheet)
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
        classes(AppStylesheet.helloGridContainer)
        attrs?.invoke(this)
    }) {
        content()
    }
}

object AppStylesheet : StyleSheet() {

    val HEADER_HEIGHT: Length = 4.px
    val GRADIENT_HEIGHT: Length = 0.3.cssRem
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
