package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import com.semanticui.compose.view.Item

/**
 * Presentation of the meta information
 * of the specified [Activity].
 */
@Composable
fun MetaItems(
    activity: Activity<*>?,
) {
    activity?.meta?.forEach {
        Item({ variation(Borderless) }) {
            MetaIcon(it)
        }
    }
}
