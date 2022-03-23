package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import com.semanticui.compose.view.Item

/**
 * Presentation of the specified [meta] information.
 */
@Composable
fun MetaItems(
    meta: List<Meta>?,
) {
    meta?.forEach {
        Item({ variation(Borderless) }) {
            MetaIcon(it)
        }
    }
}
