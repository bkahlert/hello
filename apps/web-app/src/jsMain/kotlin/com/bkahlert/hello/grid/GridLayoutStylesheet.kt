package com.bkahlert.hello.grid

import com.bkahlert.Brand
import com.bkahlert.hello.ViewportDimension
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Custom
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.CustomGradient
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Header
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Links
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Margin
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Search
import com.bkahlert.hello.grid.GridLayoutStylesheet.Grid.Tasks
import com.bkahlert.semanticui.custom.Length
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.gap
import org.jetbrains.compose.web.css.gridTemplateAreas
import org.jetbrains.compose.web.css.gridTemplateColumns
import org.jetbrains.compose.web.css.gridTemplateRows
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.media
import org.jetbrains.compose.web.css.mediaMinWidth
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.times
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.css.width

object GridLayoutStylesheet : StyleSheet() {

    val HEADER_HEIGHT: Length = 4.px
    val GRADIENT_HEIGHT: Length = 0.3.cssRem
    val MIN_HEIGHT: Length = 2.5.cssRem
    val CUSTOM_BACKGROUND_COLOR = Brand.colors.white

    enum class Grid {
        Links, Header, Search, Tasks, Margin, CustomGradient, Custom
    }

    init {
        id("root") style {
//            overflow("hidden")
        }
    }


    val container by style {
        display(DisplayStyle.Grid)
        alignContent(AlignContent.Center)
        justifyContent(JustifyContent.Center)
        width(100.vw)
        height(100.vh)
        gap(0.px, 0.px)

        // Links, Tasks and Search in separate rows
        gridTemplateColumns("1fr 2fr")
        gridTemplateRows("$HEADER_HEIGHT ${MIN_HEIGHT * 2} ${MIN_HEIGHT * 1.5} ${MIN_HEIGHT * 2} ${MIN_HEIGHT * 0.5} 0 1fr")
        gridTemplateAreas(
            "$Header $Header",
            "$Links $Links",
            "$Tasks $Tasks",
            "$Search $Search",
            "$Margin $Margin",
            "$CustomGradient $CustomGradient",
            "$Custom $Custom",
        )

        // Links, Tasks and Search in two rows at top
        media(mediaMinWidth(ViewportDimension.medium)) {
            self style {
                gridTemplateRows("$HEADER_HEIGHT ${MIN_HEIGHT * 1.5} ${MIN_HEIGHT * 1.5} ${MIN_HEIGHT * 0.5} 0 1fr")
                gridTemplateAreas(
                    "$Header $Header",
                    "$Links $Tasks",
                    "$Search $Search",
                    "$Margin $Margin",
                    "$CustomGradient $CustomGradient",
                    "$Custom $Custom",
                )
            }
        }

        // Links, Tasks and Search in one row at top
        media(mediaMinWidth(ViewportDimension.xLarge)) {
            self style {
                // minmax enforces cell content to not consume more space
                // https://css-tricks.com/preventing-a-grid-blowout/
                gridTemplateColumns("1fr 1fr 1fr minmax(0, 3fr)")
                gridTemplateRows("$HEADER_HEIGHT ${MIN_HEIGHT * 2} $GRADIENT_HEIGHT 0 1fr")
                gridTemplateAreas(
                    "$Header $Header $Header $Header",
                    "$Links $Search $Search $Tasks",
                    "$Margin $Margin $Margin $Margin",
                    "$CustomGradient $CustomGradient $CustomGradient $CustomGradient",
                    "$Custom $Custom $Custom $Custom",
                )
            }
        }
    }
}
