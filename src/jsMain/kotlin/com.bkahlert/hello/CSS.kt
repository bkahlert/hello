package com.bkahlert.hello

import com.bkahlert.Color
import com.bkahlert.kommons.SVGImage
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.StyleBuilder
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.borderWidth
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.css.width

/**
 * Visually hides affected elements.
 *
 * Screenreaders treat these elements like any other
 * visual element.
 *
 * @see <a href="https://css-tricks.com/html-inputs-and-labels-a-love-story/">HTML Inputs and Labels: A Love Story</a>
 */
fun StyleBuilder.visuallyHidden() {
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
 * Centers affected elements horizontally and vertically.
 */
fun StyleBuilder.center() {
    height(100.percent)
    display(DisplayStyle.Flex)
    alignContent(AlignContent.Center)
    alignItems(AlignItems.Stretch)
    flexDirection(FlexDirection.Column)
    flexWrap(FlexWrap.Nowrap)
    justifyContent(JustifyContent.SpaceAround)
}

class Spinner(
    color: Color,
    d: Int = 38,
    s: Int = 2,
    r: Double = (d - s) / 2.0,
) : SVGImage("""
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

infix fun Int.fmod(other: Int): Int = ((this % other) + other) % other
infix fun Double.fmod(other: Double): Double = ((this % other) + other) % other

fun gradient(type: String, vararg args: String): String =
    args.joinToString(",", "$type-gradient(", ")")

fun linearGradient(vararg colors: Color): String =
    gradient("linear", "180deg", *colors.map { it.toString() }.toTypedArray())

fun radialGradient(vararg colors: Color): String =
    gradient("radial", "circle at 50% 50%", *colors.map { it.toString() }.toTypedArray())

fun radialGradient(colors: List<Color>): String =
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

fun StyleBuilder.fontFamily(fonts: List<String>) = fontFamily(*fonts.toTypedArray())
