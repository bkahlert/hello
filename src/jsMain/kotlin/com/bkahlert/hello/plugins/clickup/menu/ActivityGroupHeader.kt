package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.SiteColors
import com.semanticui.compose.Variation.Floated
import com.semanticui.compose.element.Button
import com.semanticui.compose.element.Icon
import com.semanticui.compose.module.DropdownMenuElement
import com.semanticui.compose.module.Header
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.padding
import org.w3c.dom.HTMLDivElement

@Composable
fun SemanticElementScope<DropdownMenuElement, HTMLDivElement>.ActivityGroupHeader(
    group: ActivityGroup,
    onCreate: (() -> Unit)? = null,
) {
    Header({ style { group.color?.also { color(it) } } }) {
        group.name.forEachIndexed { index, meta ->
            if (index > 0) Icon("inverted")
            MetaIcon(meta)
        }

        onCreate?.also { add ->
            Button({
                +Floated.Right + Colored.Green + Icon + Size.Mini
                style {
                    val color = SiteColors.GreenTextColor
                    backgroundColor(Color.transparent)
                    color(color)
                    borderRadius(1.25.em)
                    padding(.35.em)
                    marginTop(.75.em)
                    property(
                        "box-shadow",
                        listOf(
                            "rgba(46,206,64,0.3) 1px 1px 1px 2px",
                            "rgba(46, 206, 64, 0.3) 2px 2px 0px 1px",
                            "rgba(46, 206, 64, 0.2) 5px 5px 0px 1px",
                            "rgba(46, 206, 64, 0.1) 8px 8px 0px 1px"
                        ).joinToString(",")
                    )
                }
                onClick { add() }
            }) { Icon("white", "plus") }
        }
    }
}
