package com.bkahlert.hello.links

import androidx.compose.runtime.Composable
import com.bkahlert.Brand.colors
import com.bkahlert.hello.Spinner
import com.bkahlert.kommons.backgroundImage
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.CSSBuilder
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.backgroundPosition
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.opacity
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Iframe
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.url.URL

@Composable
fun Custom(
    url: URL,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {
    Style(CustomStyleSheet)
    Div({
        classes(CustomStyleSheet.custom, CustomStyleSheet.loading, CustomStyleSheet.loaded)
        attrs?.also { apply(it) }
    }) {
        Iframe({
            attr("src", url.toString())
            attr("sandbox", "allow-scripts allow-same-origin allow-top-navigation-by-user-activation")
        })
    }
}

object CustomStyleSheet : StyleSheet() {

    val custom by style {
        height(100.percent)
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

    val loading by style {
        spinner()
        child(self, type("*")) style {
            opacity(0)
            backgroundRepeat("no-repeat")
            backgroundPosition("center center")
        }
    }

    val loaded by style {
        spinner()
        child(self, type("*")) style {
            property("transition", "opacity .4s ease-in")
            opacity(1)
        }
    }
}

@OptIn(ExperimentalComposeWebApi::class)
fun CSSBuilder.spinner() {
    backgroundImage(Spinner(colors.black.transparentize(.67)))
    backgroundRepeat("no-repeat")
    backgroundPosition("center center")
}
