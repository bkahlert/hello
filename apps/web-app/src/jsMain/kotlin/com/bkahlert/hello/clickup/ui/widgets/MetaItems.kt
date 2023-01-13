package com.bkahlert.hello.clickup.ui.widgets

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.collection.LinkItem
import com.bkahlert.hello.semanticui.collection.MenuElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElementScope
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.cursor
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.paddingLeft

/**
 * Presentation of the specified [meta] information.
 */
@Suppress("unused")
@Composable
fun SemanticElementScope<MenuElement>.MetaItems(
    meta: List<Meta>,
) {
    meta.forEach {
        LinkItem({
            +Borderless
            style {
                paddingLeft(0.5.em)
                cursor("default")
                backgroundColor(Color.transparent)
            }
        }) {
            MetaIcon(it)
        }
    }
}
