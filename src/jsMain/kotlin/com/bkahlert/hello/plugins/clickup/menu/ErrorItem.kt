package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bkahlert.hello.ui.errorMessage
import com.clickup.api.rest.ClickUpException
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.Variation
import com.semanticui.compose.Variation.Colored
import com.semanticui.compose.collection.MenuElement
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.IconGroup
import com.semanticui.compose.jQuery
import com.semanticui.compose.popup
import com.semanticui.compose.view.Item
import org.jetbrains.compose.web.css.cursor
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.textDecoration
import org.jetbrains.compose.web.css.textDecorationStyle
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement, *>.ErrorItem(
    exception: Throwable,
) {
    val warning = exception is ClickUpException
    val icon = if (warning) arrayOf("warning", "circle") else arrayOf("exclamation", "circle")
    val iconVariation = if (warning) Colored.Yellow else Colored.Red
    Item({
        +Borderless
        attr("data-variation", "mini inverted")
        attr("data-offset", "0")
        attr("data-position", "bottom center")
        attr("data-html", """
                <i class="${(icon + iconVariation.classNames).joinToString(" ")} icon"></i>
                ${exception.errorMessage}
            """.trimIndent())
    }) {
        IconGroup {
            Icon("cloud", { +Disabled })
            Icon({ classes(*icon);variation(iconVariation, Variation.Position.Bottom, Variation.Position.Right, Corner) })
        }
        Span({
            style {
                marginLeft(0.25.em)
                textDecoration("underline")
                textDecorationStyle("dotted")
                cursor("help")
            }
        }) {
            Text("Something went wrong")
        }
        DisposableEffect(exception) {
            jQuery(scopeElement).popup("lastResort" to true)
            onDispose {
                jQuery(scopeElement).popup("destroy")
            }
        }
    }
}
