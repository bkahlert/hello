package com.bkahlert.hello.dom

import com.bkahlert.hello.color.Color
import org.w3c.dom.HTMLElement


/**
 * Visually hides this [HTMLElement].
 *
 * Screen readers treat these elements like any other visual element.
 *
 * @see <a href="https://css-tricks.com/html-inputs-and-labels-a-love-story/">HTML Inputs and Labels: A Love Story</a>
 */
public fun HTMLElement.hideVisually() {
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

@Deprecated("use semantic UI loader")
public class Spinner(
    color: Color,
    d: Int = 38,
    s: Int = 2,
    r: Double = (d - s) / 2.0,
) : SvgImage(
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
    """.trimIndent()
)

public class MagnifyingGlass(
    color: Color,
) : SvgImage(
    // language=SVG
    """
    <svg focusable="false" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="$color">
      <path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"></path>
    </svg>
    """.trimIndent()
)

public infix fun Int.fmod(other: Int): Int = ((this % other) + other) % other
public infix fun Double.fmod(other: Double): Double = ((this % other) + other) % other

public fun gradient(type: String, vararg args: String): String =
    args.joinToString(",", "$type-gradient(", ")")

public fun linearGradient(vararg colors: Color): String =
    gradient("linear", "180deg", *colors.map { it.toString() }.toTypedArray())

public fun radialGradient(vararg colors: Color): String =
    gradient("radial", "circle at 50% 50%", *colors.map { it.toString() }.toTypedArray())

public fun radialGradient(colors: List<Color>): String =
    radialGradient(*colors.toTypedArray())

public fun metalicGradient(color: Color): String =
    radialGradient(
        color.fade(1.0),
        color.fade(0.8),
        color.fade(1.0),
        color.fade(0.9),
        color.fade(1.0),
        color.fade(0.9),
    )
