package com.bkahlert.hello

import androidx.compose.runtime.Composable
import com.bkahlert.Brand
import com.bkahlert.hello.Dimensions.Screen
import com.bkahlert.hello.links.Custom
import com.bkahlert.hello.links.Header
import com.bkahlert.hello.links.Links
import com.bkahlert.hello.search.Engine
import com.bkahlert.hello.search.Engine.Google
import com.bkahlert.hello.search.Search
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.CSSBuilder
import org.jetbrains.compose.web.css.CSSUnitValue
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.Color.transparent
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.boxSizing
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.gap
import org.jetbrains.compose.web.css.gridArea
import org.jetbrains.compose.web.css.gridTemplateAreas
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.css.gridTemplateRows
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.media
import org.jetbrains.compose.web.css.mediaMaxHeight
import org.jetbrains.compose.web.css.mediaMinWidth
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.selectors.CSSSelector
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.url.URL

fun main() {

    // trigger creation to avoid flickering
    Engine.values().forEach {
        it.greyscaleImage
        it.coloredImage
    }

    renderComposable("root") {
        Style(AppStylesheet)

        Grid {
            Div({
                style {
                    gridArea(AppStylesheet.Grid.Header.name)
                    backgroundColor(Brand.colors.primary)
                }
            }) { Header("Hello") }
            Div({
                style {
                    gridArea(AppStylesheet.Grid.Links.name)
                    backgroundColor(Brand.colors.primary)
                }
            }) { Links() }
            Div({
                style {
                    gridArea(AppStylesheet.Grid.Search.name)
                    backgroundColor(Brand.colors.primary)
                }
            }) {
//                Search(value = "all engines (focussed)", allEngines = true)
//                Search(value = "all engines", allEngines = true)
//                Search(value = "single engine")
//                Search(allEngines = true)
                Search(Google)
            }
            Div({
                style {
                    gridArea(AppStylesheet.Grid.Options.name)
                    backgroundColor(Brand.colors.primary)
                }
            })
            Div({
                style {
                    gridArea(AppStylesheet.Grid.CustomGradient.name)
                    property("z-index", "1")
                    height(0.5.cssRem)
                    backgroundColor(transparent)
                    backgroundImage(linearGradient(Brand.colors.primary, Brand.colors.primary.transparentize(0)))
                }
            }) { }
            Div({
                style {
                    gridArea(AppStylesheet.Grid.Custom.name)
                    backgroundColor(Engine.StartMe.color)
                }
            }) { Custom(URL("https://start.me/p/jj2pPl/technology")) }
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

    enum class Grid {
        Links, Header, Search, Options, CustomGradient, Custom
    }

    init {
        "html, body, #root" style {
            overflow("hidden")
            height(100.percent)
            margin(0.px)
            padding(0.px)
            backgroundColor(transparent)
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

    // A convenient way to create a class selector
    // com.bkahlert.hello.AppStylesheet.container can be used as a class in component attrs
    val container by style {
        color(Color.red)

        // hover selector for a class
        self + hover style { // self is a selector for `container`
            color(Color.green)
        }

//        media(maxWidth(640.px)) {
//            self style {
//                padding(12.px)
//            }
//        }
    }

    val helloGridContainer by style {
        display(DisplayStyle.Grid)
        alignContent(AlignContent.Center)
        justifyContent(JustifyContent.Center)
        width(100.vw)
        height(100.vh)
        gap(0.px, 0.px)

        media(mediaMinWidth(Screen.lg)) {
            self style {
                gridTemplateColumns("1fr 1fr 1fr 1fr")
                gridTemplateRows("1fr 10fr 0 60fr")
                gridTemplateAreas(
                    "${Grid.Links} ${Grid.Links} ${Grid.Links} ${Grid.Links}",
                    "${Grid.Header} ${Grid.Search} ${Grid.Search} ${Grid.Options}",
                    "${Grid.CustomGradient} ${Grid.CustomGradient} ${Grid.CustomGradient} ${Grid.CustomGradient}",
                    "${Grid.Custom} ${Grid.Custom} ${Grid.Custom} ${Grid.Custom}",
                )
            }
        }

        gridTemplateColumns("1fr 1fr")
        gridTemplateRows("1fr 1fr 1fr 0 3fr")
        gridTemplateAreas(
            "${Grid.Links} ${Grid.Links}",
            "${Grid.Search} ${Grid.Search}",
            "${Grid.Header} ${Grid.Options}",
            "${Grid.CustomGradient} ${Grid.CustomGradient}",
            "${Grid.Custom} ${Grid.Custom}",
        )

        media(mediaMaxHeight(150.px)) {
            gridTemplateRows("0 1fr 0 0 0")
            style(className(container)) { padding(0.px) }
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

object Dimensions {
    object Screen {

        /**
         * Small tablets and large smartphones (landscape view)
         */
        val sm: CSSUnitValue = 576.px

        /**
         * Small tablets (portrait view)
         */
        val md: CSSUnitValue = 768.px

        /**
         * Tablets and small desktops
         */
        val lg: CSSUnitValue = 992.px

        /**
         * Large tablets and desktops
         */
        val xl: CSSUnitValue = 1200.px
    }
}
