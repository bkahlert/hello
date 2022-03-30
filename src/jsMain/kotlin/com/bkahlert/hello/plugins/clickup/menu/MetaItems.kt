package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import com.semanticui.compose.view.Item

/**
 * Presentation of the specified [meta] information.
 */
@Composable
fun MetaItems(
    meta: List<Meta>,
) {
    meta.forEach {
        Item({ +Borderless }) {
            MetaIcon(it)
        }
    }
}
