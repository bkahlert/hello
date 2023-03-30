package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.custom.color
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.module.DropdownMenuElement
import org.jetbrains.compose.web.css.Position.Companion.Relative
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.dom.A

@Composable
public fun SemanticElementScope<DropdownMenuElement>.ActivityGroupHeader(
    group: ActivityGroup,
    onCreate: (() -> Unit)? = null,
) {
    S("header", attrs = {
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
