package com.bkahlert.hello.integration

import androidx.compose.runtime.Composable
import com.bkahlert.hello.center
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
fun ClickUp(
    text: String,
    attrs: AttrBuilderContext<HTMLHeadingElement>? = null,
) {
    Style(OptionsStyleSheet)
    Div({
        style {
            center()
        }
    }) {
        H1({
            classes(OptionsStyleSheet.header)
            attrs?.also { apply(it) }
        }) {
            Text(text)
        }
    }
}

object OptionsStyleSheet : StyleSheet() {

    val header by style {
        textAlign("center")
        fontSize(1.em)
    }
}