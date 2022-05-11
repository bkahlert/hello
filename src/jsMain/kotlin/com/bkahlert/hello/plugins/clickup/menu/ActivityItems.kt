package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.module.DropdownMenuElement
import com.semanticui.compose.module.DropdownMenuItemElement
import org.w3c.dom.HTMLDivElement

@Composable
fun SemanticElementScope<DropdownMenuElement, HTMLDivElement>.ActivityItems(
    activities: Iterable<Activity<*>>,
    attrs: SemanticAttrBuilder<DropdownMenuItemElement, HTMLDivElement>? = null,
) {
    activities.forEach { activity ->
        ActivityItem(activity, attrs)
    }
}
