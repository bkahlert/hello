package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticElementScope
import com.bkahlert.hello.semanticui.module.DropdownMenuElement
import com.bkahlert.hello.semanticui.module.DropdownMenuItemElement

@Composable
public fun SemanticElementScope<DropdownMenuElement>.ActivityItems(
    activities: Iterable<Activity<*>>,
    attrs: SemanticAttrBuilderContext<DropdownMenuItemElement>? = null,
) {
    activities.forEach { activity ->
        ActivityItem(activity, attrs)
    }
}