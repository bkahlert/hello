package com.bkahlert.hello.links

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
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
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement

@Composable
fun Links(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Style(LinksStyleSheet)
    Div({
        style {
            textAlign("center")
        }
        attrs?.invoke(this)
    }) {
        content?.also { it(this) }
    }
}

@Composable
fun Link(
    href: String,
    text: String,
    backgroundImageUrl: String? = null,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
) {
    A(href, {
        classes(LinksStyleSheet.gridItem, LinksStyleSheet.btn)
        style {
            backgroundImageUrl?.also { backgroundImage("url($it)") }
        }
        attrs?.invoke(this)
    }) {
        Span { Text(text) }
    }
}

object LinksStyleSheet : StyleSheet() {

    val btn by style {
        position(Position.Relative)
        display(DisplayStyle.InlineBlock)
        fontWeight(400)
        color(Color.gray)
        textAlign("center")
        property("vertical-align", "middle")
        property("-webkit-user-select", "none")
        property("-ms-user-select", "none")
        property("user-select", "none")
        border(2.px, LineStyle.Solid, Color.gray)
        padding(.55.cssRem, .95.cssRem)
        fontSize(1.cssRem)
        lineHeight(150.percent)
        borderRadius(.25.cssRem)
        property("transition", "all .2s ease")

        opacity(.5)
        property("mix-blend-mode", "luminosity")
        backgroundSize("cover")
        backgroundRepeat("no-repeat")
        backgroundPosition("center")

        self + focus style {
            outline("0")
            opacity(1)
        }

        hover(self) style {
            opacity(1)
            textDecoration("none")
        }

        self + ":not(:disabled):not(.disabled):active" style {
            backgroundSize("95%")
        }
    }

    val gridItem by style {
        width(35.px)
        height(35.px)
        margin(.5.em)
        padding(0.px)

        self + before style {
            property("content", "''")
            display(DisplayStyle.Block)
            overflow("hidden")
            width(100.percent)
            height(100.percent)
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
