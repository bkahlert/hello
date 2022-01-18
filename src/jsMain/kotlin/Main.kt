import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bkahlert.Brand
import com.bkahlert.hello.Engine
import com.bkahlert.hello.Search
import com.bkahlert.hello.linearGradient
import org.jetbrains.compose.web.ExperimentalComposeWebApi
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
import org.jetbrains.compose.web.css.background
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
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
import org.jetbrains.compose.web.dom.Iframe
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement

fun main() {
    var count: Int by mutableStateOf(0)

    // trigger creation to avoid flickering
    Engine.values().forEach {
        it.greyscaleImage
        it.coloredImage
    }

    renderComposable("root") {
        Style(AppStylesheet)

        Grid {
            Header {
                Text(AppStylesheet.helloHeader)
            }
            Links {
                Text(AppStylesheet.helloLinks)
            }
            Div({
                classes(AppStylesheet.helloSearch)
            }) {
//                Search(value = "all engines (focussed)", allEngines = true)
//                Search(value = "all engines", allEngines = true)
//                Search(value = "single engine")
//                Search(allEngines = true)
                Search()
            }
            Options {
                Text(AppStylesheet.helloOptions)
            }
            CustomGradient()
            Custom({
                classes("loading", "loaded")
            }) {
                Iframe({
                    attr("src", "https://start.me/p/jj2pPl/technology")
                    attr("sandbox", "allow-scripts allow-same-origin allow-top-navigation-by-user-activation")
                })
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
        attrs?.also { apply(it) }
    }) {
        content()
    }
}


@Composable
fun Header(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable () -> Unit = {},
) {
    Div({
        classes(AppStylesheet.helloHeader)
        attrs?.also { apply(it) }
    }) {
        content()
    }
}


@Composable
fun Links(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable () -> Unit = {},
) {
    Div({
        classes(AppStylesheet.helloLinks)
        attrs?.also { apply(it) }
    }) {
        content()
    }
}


@Composable
fun Options(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable () -> Unit = {},
) {
    Div({
        classes(AppStylesheet.helloOptions)
        attrs?.also { apply(it) }
    }) {
        content()
    }
}


@Composable
fun CustomGradient(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable () -> Unit = {},
) {
    Div({
        classes(AppStylesheet.helloCustomGradient)
        attrs?.also { apply(it) }
    }) {
        content()
    }
}


@Composable
fun Custom(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable () -> Unit = {},
) {
    Div({
        classes(AppStylesheet.helloCustom)
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
        "html, body" style {
            overflow("hidden")
            height(100.percent)
            margin(0.px)
            padding(0.px)
            backgroundColor(transparent)
            backgroundImage("none")
        }

        // `universal` can be used instead of "*": `universal style {}`
        "*" style {
            fontSize(35.px)
            padding(0.px)
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
    // AppStylesheet.container can be used as a class in component attrs
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

    val helloHeader by style {
        gridArea(Grid.Header.name)
        backgroundColor(Brand.colors.primary)
    }

    val helloLinks by style {
        gridArea(Grid.Links.name)
        backgroundColor(Brand.colors.primary)
    }

    val helloSearch by style {
        gridArea(Grid.Search.name)
        backgroundColor(Brand.colors.primary)

        display(DisplayStyle.Flex)
        alignContent(AlignContent.Center)
        alignItems(AlignItems.Stretch)
        flexDirection(FlexDirection.Column)
        flexWrap(FlexWrap.Nowrap)
        justifyContent(JustifyContent.SpaceAround)
    }

    val helloOptions by style {
        gridArea(Grid.Options.name)
        backgroundColor(Brand.colors.primary)
    }

    val helloCustomGradient by style {
        gridArea(Grid.CustomGradient.name)
        property("z-index", "1")
        height(0.5.cssRem)
        backgroundColor(transparent)
        backgroundImage(linearGradient(Brand.colors.primary, Brand.colors.primary.transparentize(0)))
    }

    @OptIn(ExperimentalComposeWebApi::class)
    val helloCustom by style {
        gridArea(Grid.Custom.name)
        backgroundColor(Brand.colors.secondary)

        child(self, type("*")) style {
            width(135.percent)
            height(135.percent)
            property("zoom", "0.75");
            property("-moz-transform", "scale(0.75)");
            property("-moz-transform-origin", "0 0");
            property("-o-transform", "scale(0.75)");
            property("-o-transform-origin", "0 0");
            property("-webkit-transform", "scale(0.75)");
            property("-webkit-transform-origin", "0 0");
            property("border", "none")
        }
    }

    val helloGridContainer by style {
        display(DisplayStyle.Grid)
        alignContent(AlignContent.Center)
        justifyContent(JustifyContent.Center)
        width(100.vw)
        height(100.vh)
        gap(0.px, 0.px)

        media(mediaMinWidth(Dimensions.Screen.lg)) {
            self style {
                gridTemplateColumns("1fr 1fr 1fr 1fr")
                gridTemplateRows("1fr 1fr 0 3fr")
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
            "$helloLinks $helloLinks",
            "$helloSearch $helloSearch",
            "$helloHeader $helloOptions",
            "$helloCustomGradient $helloCustomGradient",
            "$helloCustom $helloCustom",
        )

        media(mediaMaxHeight(150.px)) {
            style(className(helloSearch)) {
                background("none")
            }

            gridTemplateRows("0 1fr 0 0 0")

            style(className(container)) {
                padding(0.px)
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

object Keyboard {
    object Codes {
        val keyUp: Int = 38
        val keyDown: Int = 40
        val meta: Int = 91
    }
}
