package com.bkahlert.hello.links

import androidx.compose.runtime.Composable
import com.bkahlert.hello.ui.center
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLHeadingElement

@Composable
fun Header(
    text: String? = null,
    attrs: AttrBuilderContext<HTMLHeadingElement>? = null,
) {
    Style(HeaderStyleSheet)
    Div({
        style {
            center()
        }
    }) {
        H1({
            classes(HeaderStyleSheet.header)
            attrs?.invoke(this)
        }) {
            text?.let { Text(it) }
        }
    }
}

object HeaderStyleSheet : StyleSheet() {

    val header by style {
        textAlign("center")
        fontSize(1.em)
    }
}
