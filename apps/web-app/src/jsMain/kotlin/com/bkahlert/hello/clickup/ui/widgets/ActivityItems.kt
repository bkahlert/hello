package com.bkahlert.hello.clickup.ui.widgets

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.attributes.SemanticAttrsScope
import com.bkahlert.hello.semanticui.dom.SemanticElementScope
import com.bkahlert.hello.semanticui.module.DropdownMenuElement
import com.bkahlert.hello.semanticui.module.DropdownMenuItemElement
import org.w3c.dom.HTMLDivElement

@Composable
fun SemanticElementScope<DropdownMenuElement, HTMLDivElement>.ActivityItems(
    activities: Iterable<Activity<*>>,
    attrs: (SemanticAttrsScope<DropdownMenuItemElement, HTMLDivElement>.() -> Unit)? = null,
) {
    activities.forEach { activity ->
        ActivityItem(activity, attrs)
    }
}
