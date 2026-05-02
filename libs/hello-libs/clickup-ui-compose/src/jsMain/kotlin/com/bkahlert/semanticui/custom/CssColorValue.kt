package com.bkahlert.semanticui.custom

import com.bkahlert.kommons.color.Color
import org.jetbrains.compose.web.css.CSSBorder
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color

/** The color's [CSSColorValue]. */
public val Color.cssColorValue: CSSColorValue
    get() = CssColorValue(this)

private value class CssColorValue(
    private val color: Color,
) : CSSColorValue {
    override fun toString(): String = color.toString()
}

/** Sets the [color] to the specified [value]. */
public fun StyleScope.color(value: Color): Unit = color(value.cssColorValue)

/** Sets the [backgroundColor] to the specified [value]. */
public fun StyleScope.backgroundColor(value: Color): Unit = backgroundColor(value.cssColorValue)

/** Sets the [color] to the specified [value]. */
public fun CSSBorder.color(value: Color): Unit = color(value.cssColorValue)
