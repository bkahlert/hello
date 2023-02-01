package com.bkahlert.hello.header

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.name
import org.jetbrains.compose.web.css.percent
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


/**
 * Centers affected elements horizontally and vertically.
 */
@Deprecated("use solution without height")
internal fun StyleScope.center(direction: FlexDirection = FlexDirection.Column) {
    height(100.percent)
    display(DisplayStyle.Flex)
    if (direction.name.startsWith("Column")) {
        alignContent(AlignContent.Center)
        alignItems(AlignItems.Stretch)
    } else {
        alignContent(AlignContent.Stretch)
        alignItems(AlignItems.Center)
    }
    flexDirection(direction)
    flexWrap(FlexWrap.Nowrap)
    justifyContent(JustifyContent.SpaceAround)
}
