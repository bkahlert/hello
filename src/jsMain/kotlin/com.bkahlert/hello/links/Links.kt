package com.bkahlert.hello.links

import androidx.compose.runtime.Composable
import com.bkahlert.RGB
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundPosition
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.left
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.opacity
import org.jetbrains.compose.web.css.outline
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.textDecoration
import org.jetbrains.compose.web.css.top
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun Links(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {
    Style(LinksStyleSheet)
    Div({
        classes("grid")
        style {
            textAlign("center")
        }
        attrs?.also { apply(it) }
    }) {
        // TODO parametrize
        A("https://start.me/p/4K6MOy/dashboard", {
            classes(LinksStyleSheet.gridItem, LinksStyleSheet.gridItemStartMe, LinksStyleSheet.btn, LinksStyleSheet.btnPrimary)
        }) {
            Span { Text("start.me") }
        }
        A("https://home.bkahlert.com", {
            classes(LinksStyleSheet.gridItem, LinksStyleSheet.gridItemHome, LinksStyleSheet.btn, LinksStyleSheet.btnPrimary)
        }) {
            Span { Text("home") }
        }
    }
}

object LinksStyleSheet : StyleSheet() {
    // TODO make look good when small
    val btn by style {
        position(Position.Relative)
        display(DisplayStyle.InlineBlock)
        fontWeight(400)
        color(RGB("#44476a"))
        textAlign("center")
        property("vertical-align", "middle")
        property("-webkit-user-select", "none")
        property("-ms-user-select", "none")
        property("user-select", "none")
        backgroundColor(RGB("#d1d9e6"))
        border(0.px, LineStyle.Solid, Color.transparent)
        padding(.55.cssRem, .95.cssRem)
        fontSize(1.cssRem)
        lineHeight(150.percent)
        borderRadius(.25.cssRem)
        property("transition", "all .2s ease")
        property("box-shadow", "10px 10px 30px #47b5f0,-10px -10px 30px #63c0f2")

        self + focus style {
            outline("0")
            self + after style {
                opacity(1)
            }
        }

        self + hover style {
            color(RGB("#44476a"))
            textDecoration("none")
        }
    }

    val btnPrimary by style {
        color(RGB("#ecf0f3"))
        backgroundColor(RGB("#55b9f3"))
        backgroundImage("linear-gradient(145deg,#55b9f3,#55b9f3)")
        property("border-color", "#55b9f3")
        property("box-shadow", "10px 10px 30px #129eee,-10px -10px 30px #6ad1f7")

        self + before style {
            backgroundPosition("center")
        }

        self + after style {
            property("content", "''")
            position(Position.Absolute)
            top(0.px)
            left(0.px)
            display(DisplayStyle.Block)
            overflow("hidden")
            width(100.percent)
            height(100.percent)
            property("box-shadow", "inset 5px 5px 5px rgba(123,201,246,.9), inset -5px -5px 5px rgba(155,224,250,.8)")
            opacity(0)
            borderRadius(.25.cssRem)
        }

        self + hover style {
            color(RGB("#ecf0f3"))
            backgroundColor(RGB("#31aaf0"))
            backgroundImage("linear-gradient(145deg,#42b1f2,#68c1f4)")
            property("border-color", "#55b9f3")
        }

        self + ":not(:disabled):not(.disabled):active" style {
            color(RGB("#ecf0f3"))
            backgroundColor(RGB("#25a5f0"))
            property("box-shadow", "5px 5px 5px #47b3f2, -5px -5px 30px #59c0f4, inset 5px 5px 10px #129eee, inset -5px -5px 10px #6ad1f7")
            property("border-color", "#55b9f3")

            self + before style {
                backgroundSize("95%")
            }
        }
    }

    val gridItem by style {
        width(5.vh)
        height(5.vh)
        margin(.5.em)
        padding(0.px)

        self + before style {
            property("content", "''")
            display(DisplayStyle.Block)
            overflow("hidden")
            width(100.percent)
            height(100.percent)
            property("mix-blend-mode", "lighten")
            backgroundSize("cover")
            backgroundRepeat("no-repeat")
        }

        child(self, type("*")) style {
            display(DisplayStyle.None)
        }
    }

    val gridItemStartMe by style {
        self + before style { backgroundImage("url(web.svg)") }
    }

    val gridItemHome by style {
        self + before style { backgroundImage("url(home.svg)") }
    }
}
