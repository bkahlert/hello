package com.bkahlert.hello.ui.compose

import com.bkahlert.hello.color.Color
import com.bkahlert.hello.dom.Image
import com.bkahlert.kommons.quoted
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.CSSBorder
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.borderWidth
import org.jetbrains.compose.web.css.color
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

private fun Color.toCssColorValue() = object : CSSColorValue {
    override fun toString(): String = this@toCssColorValue.toString()
}

public fun StyleScope.color(value: Color): Unit = color(value.toCssColorValue())

public fun StyleScope.backgroundColor(value: Color): Unit = backgroundColor(value.toCssColorValue())

public fun CSSBorder.color(value: Color): Unit = color(value.toCssColorValue())


public fun StyleScope.backgroundImage(image: Image) {
    backgroundImage("""url("${image.dataURI}")""")
}


/**
 * Truncates overflowing text using the specified [marker].
 *
 * In order for that to actually occur text must be forced
 * to overflow which, which done by [forceOverflow].
 */
public fun StyleScope.textOverflow(
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
public fun StyleScope.visuallyHidden() {
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
@Deprecated("use solution without height")
public fun StyleScope.center(direction: FlexDirection = FlexDirection.Column) {
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
public fun <T : Enum<T>> StyleScope.gridArea(rowStart: T) {
    property("grid-area", rowStart.name)
}

public fun StyleScope.fontFamily(fonts: List<String>): Unit = fontFamily(*fonts.toTypedArray())
