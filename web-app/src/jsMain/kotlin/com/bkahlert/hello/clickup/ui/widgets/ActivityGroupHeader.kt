package com.bkahlert.hello.clickup.ui.widgets

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.SemanticElementScope
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.module.DropdownMenuElement
import com.bkahlert.hello.semanticui.module.Header
import org.jetbrains.compose.web.css.Position.Companion.Relative
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.dom.A
import org.w3c.dom.HTMLDivElement

@Composable
fun SemanticElementScope<DropdownMenuElement, HTMLDivElement>.ActivityGroupHeader(
    group: ActivityGroup,
    onCreate: (() -> Unit)? = null,
) {
    Header({
        style {
            group.color?.also { color(it) }
            position(Relative)
        }
    }) {
        group.name.forEachIndexed { index, meta ->
            if (index > 0) Icon("inverted")
            MetaIcon(meta)
        }

        onCreate?.also { add ->
            A(null, {
                classes("ui", "mini", "green", "right", "corner", "label")
                style {
                    marginTop((-1).cssRem)
                }
                onClick { add() }
            }) {
                Icon("plus")
            }
        }
    }
}
