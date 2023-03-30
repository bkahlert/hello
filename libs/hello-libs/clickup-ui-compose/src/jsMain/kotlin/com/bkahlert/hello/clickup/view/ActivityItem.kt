package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.custom.textOverflow
import com.bkahlert.semanticui.module.DropdownMenuElement
import com.bkahlert.semanticui.module.DropdownMenuItemElement
import com.bkahlert.semanticui.module.Item
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Text

@Composable
public fun SemanticElementScope<DropdownMenuElement>.ActivityItem(
    activity: Activity<*>,
    attrs: SemanticAttrBuilderContext<DropdownMenuItemElement>? = null,
) {
    ActivityItemWithoutPopup(activity, attrs)
}

@Composable
public fun SemanticElementScope<DropdownMenuElement>.ActivityItemWithoutPopup(
    activity: Activity<*>,
    attrs: SemanticAttrBuilderContext<DropdownMenuItemElement>? = null,
    content: SemanticContentBuilder<DropdownMenuItemElement>? = null,
) {
    Item({
        attr("data-text", activity.name)
        attr("data-value", activity.id.typedStringValue)
        style { textOverflow() }
        style { maxWidth(100.percent) }
        attrs?.invoke(this)
    }) {
        ActivityIcon(activity)
        Text(activity.name)
        content?.invoke(this)
    }
}
