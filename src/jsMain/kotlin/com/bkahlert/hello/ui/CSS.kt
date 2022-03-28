package com.bkahlert.hello.ui

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.SVGImage
import com.bkahlert.kommons.text.quoted
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.borderWidth
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.name
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.css.width
import org.w3c.dom.HTMLElement

/**
 * Truncates overflowing text using the specified [marker].
 *
 * In order for that to actually occur text must be forced
 * to overflow which, which done by [forceOverflow].
 */
fun StyleScope.textOverflow(
    string: String = "ellipsis",
    forceOverflow: Boolean = true,
) {
    if (forceOverflow) {
        whiteSpace("nowrap")
        overflow("hidden")
    }
    property("text-overflow", if (string == "ellipsis" || string == "…") "ellipsis" else string.quoted)
}

/**
 * Visually hides affected elements.
 *
 * Screen readers treat these elements like any other visual element.
 *
 * @see <a href="https://css-tricks.com/html-inputs-and-labels-a-love-story/">HTML Inputs and Labels: A Love Story</a>
 */
fun StyleScope.visuallyHidden() {
    position(Position.Absolute)
    width(1.px)
    height(1.px)
    padding(0.px)
    property("clip", "rect(1px, 1px, 1px, 1px)")
    borderWidth(0.px)
    overflow("hidden")
    whiteSpace("nowrap")
}

/**
 * Visually hides this [HTMLElement].
 *
 * Screen readers treat these elements like any other visual element.
 *
 * @see <a href="https://css-tricks.com/html-inputs-and-labels-a-love-story/">HTML Inputs and Labels: A Love Story</a>
 */
fun HTMLElement.hideVisually() {
    style.apply {
        position = "absolute"
        width = "1px"
        height = "1px"
        padding = "0"
        clip = "rect(1px, 1px, 1px, 1px)"
        borderWidth = "0"
        overflowX = "hidden"
        overflowY = "hidden"
        whiteSpace = "nowrap"
    }
}

/**
 * Centers affected elements horizontally and vertically.
 */
@Deprecated("use solution without height")
fun StyleScope.center(direction: FlexDirection = FlexDirection.Column) {
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

// https://developer.mozilla.org/en-US/docs/Web/CSS/grid-area
fun <T : Enum<T>> StyleScope.gridArea(rowStart: T) {
    property("grid-area", rowStart.name)
}

@Deprecated("use semantic UI loader")
class Spinner(
    color: Color,
    d: Int = 38,
    s: Int = 2,
    r: Double = (d - s) / 2.0,
) : SVGImage(
    // language=SVG
    """
    <svg width="${d}px" height="${d}px" viewBox="0 0 $d $d" xmlns="http://www.w3.org/2000/svg" stroke="$color" stroke-opacity=".5">
      <g fill="none" fill-rule="evenodd">
        <g transform="translate(${s / 2} ${s / 2})" stroke-width="$s">
          <circle cx="$r" cy="$r" r="$r"/>
          <path d="M${d - s} ${r}c0-9.94-8.06-$r-$r-$r">
            <animateTransform attributeName="transform" type="rotate" from="0 $r $r" to="360 $r $r" dur="1s" repeatCount="indefinite"/>
          </path>
        </g>
      </g>
    </svg>
    """.trimIndent())

class MagnifyingGlass(
    color: Color,
) : SVGImage(
    // language=SVG
    """
    <svg focusable="false" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="$color">
      <path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"></path>
    </svg>
    """.trimIndent())

infix fun Int.fmod(other: Int): Int = ((this % other) + other) % other
infix fun Double.fmod(other: Double): Double = ((this % other) + other) % other

fun gradient(type: String, vararg args: String): String =
    args.joinToString(",", "$type-gradient(", ")")

fun linearGradient(vararg colors: CSSColorValue): String =
    gradient("linear", "180deg", *colors.map { it.toString() }.toTypedArray())

fun radialGradient(vararg colors: CSSColorValue): String =
    gradient("radial", "circle at 50% 50%", *colors.map { it.toString() }.toTypedArray())

fun radialGradient(colors: List<CSSColorValue>): String =
    radialGradient(*colors.toTypedArray())

fun metalicGradient(color: Color): String =
    radialGradient(
        color.transparentize(1.0),
        color.transparentize(0.8),
        color.transparentize(1.0),
        color.transparentize(0.9),
        color.transparentize(1.0),
        color.transparentize(0.9),
    )

fun StyleScope.fontFamily(fonts: List<String>) = fontFamily(*fonts.toTypedArray())
