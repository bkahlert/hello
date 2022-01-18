package com.bkahlert.hello

import com.bkahlert.Color
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.StyleBuilder
import org.jetbrains.compose.web.css.borderWidth
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.padding
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
