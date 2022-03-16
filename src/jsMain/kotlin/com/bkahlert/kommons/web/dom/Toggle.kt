package com.bkahlert.kommons.web.dom

import androidx.compose.runtime.Composable
import com.bkahlert.Brand
import com.bkahlert.kommons.Color.RGB
import com.bkahlert.kommons.text.quoted
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.attributes.builders.InputAttrsScope
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.div
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.left
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.top
import org.jetbrains.compose.web.css.transform
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Input

@Composable
fun Toggle(
    label: String? = null,
    checkedLabel: String? = label,
    attrs: InputAttrsScope<Boolean>.() -> Unit = {},
) {
    val styleSheet = ToggleStyleSheet(label, checkedLabel).also { Style(it) }
    Input(Checkbox) {
        classes(styleSheet.toggle)
        attrs.invoke(this)
    }
}

class ToggleStyleSheet(
    label: String? = null,
    checkedLabel: String? = null,
) : StyleSheet() {

    val uncheckedColor = Brand.colors.border
    val checkedColor = RGB("#4ED164")

    //    val width = 50.px
//    val height = 30.px
    val width = 73.333.px
    val height = 44.px
    val borderWidth = 1.px
    val innerHeight = height - 4.px
    private val translateX = width - height

    @OptIn(ExperimentalComposeWebApi::class)
    val toggle by style {
        position(Position.Relative)
        property("appearance", "none")
        property("outline", "none")
        width(width)
        height(height)
        margin(0.px)
        backgroundColor(Brand.colors.input)
        border(borderWidth, LineStyle.Solid, uncheckedColor)
        borderRadius(width)
        property("box-shadow", "inset -$translateX 0 0 0 ${Color.white}")
        property("transition", "all .1s ease-in-out")

        self + checked style {
            border(borderWidth, LineStyle.Solid, checkedColor)
            property("box-shadow", "inset $translateX 0 0 0 $checkedColor")
        }

        val shadowOffsetX = 2.px
        val shadowOffsetY = 4.px
        val shadowBlurRadius = 6.px

        self + after style {
            position(Position.Absolute)
            top(borderWidth)
            left(borderWidth)
            width(innerHeight)
            height(innerHeight)
            lineHeight(innerHeight)
            textAlign("center")
            fontSize(1.2.em)
            fontWeight(900)
            property("content", (label ?: "").quoted)
            backgroundColor(Color.transparent)
            borderRadius(50.percent);
            property("box-shadow", "$shadowOffsetX $shadowOffsetY $shadowBlurRadius ${Brand.colors.black.transparentize(.2)}")
            property("transition", "all .2s ease")
        }

        self + checked + after style {
            property("content", (checkedLabel ?: label ?: "").quoted)
            transform { translateX(translateX) }
            property("box-shadow", "-$shadowOffsetX $shadowOffsetY ${shadowBlurRadius / 2} ${Brand.colors.black.transparentize(.05)}")
        }
    }
}
