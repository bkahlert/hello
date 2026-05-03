package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.module.DropdownMenuElement
import com.bkahlert.semanticui.module.DropdownMenuItemElement

@Composable
public fun SemanticElementScope<DropdownMenuElement>.ActivityItems(
    activities: Iterable<Activity<*>>,
    attrs: SemanticAttrBuilderContext<DropdownMenuItemElement>? = null,
) {
    activities.forEach { activity ->
        ActivityItem(activity, attrs)
    }
}
