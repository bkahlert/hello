package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.semanticui.core.Device
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.core.matches
import com.bkahlert.semanticui.custom.textOverflow
import com.bkahlert.semanticui.module.DropdownMenuElement
import com.bkahlert.semanticui.module.DropdownMenuItemElement
import com.bkahlert.semanticui.module.Item
import com.bkahlert.semanticui.module.destroy
import com.bkahlert.semanticui.module.popup
import com.bkahlert.semanticui.module.show
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Text

private val logger = ConsoleLogger("ActivityItem")

@Composable
public fun SemanticElementScope<DropdownMenuElement>.ActivityItem(
    activity: Activity<*>,
    attrs: SemanticAttrBuilderContext<DropdownMenuItemElement>? = null,
) {
    if (Device.NoHoverFeature.matches || Device.Active <= Device.Mobile) {
        ActivityItemWithoutPopup(activity, attrs)
    } else {
        ActivityItemWithPopup(activity, attrs)
    }
}

@Composable
public fun SemanticElementScope<DropdownMenuElement>.ActivityItemWithPopup(
    activity: Activity<*>,
    attrs: SemanticAttrBuilderContext<DropdownMenuItemElement>? = null,
) {
    var showPopup by remember { mutableStateOf(false) }
    ActivityItemWithoutPopup(activity, {
        onMouseEnter {
            logger.debug("Activity: ${activity.id} — show popup")
            showPopup = true
        }
        onMouseLeave { showPopup = false }
        attrs?.invoke(this)
    }) {
        DisposableEffect(showPopup) {
            val popup = if (showPopup) {
                scopeElement
                    .popup {
                        lastResort = true
                        variation = "mini"
                        position = "left center"
                        html = activity.popupHtml()
                    }
                    .show()
            } else {
                null
            }
            onDispose {
                if (popup != null) {
                    logger.debug("Activity: ${activity.id} — destroy popup")
                    popup.destroy()
                }
            }
        }
    }
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

public fun Activity<*>.popupHtml(): String = buildString {
    append("""<div class="ui items">""")
    append("""<div class="item">""")
    append("""<div class="content">""")
    append("""<div class="meta">""")
    meta.forEach { (iconVariations, title, text) ->
        append("""<span title="""")
        append(title)
        append(""""><i class="""")
        iconVariations.forEach {
            append(it)
            append(" ")
        }
        append("""icon"></i> """)
        append(text)
        append("</span>")
    }
    append("""</div>""")
    append("""<div class="description">""")
    when (descriptions.size) {
        0 -> append("—")
        1 -> append(descriptions.entries.first().value?.asHTML() ?: "—")
        else -> {
            append("""<div class="ui list">""")
            descriptions.forEach { (name, text) ->
                append("""<div class="item">""")
                append("""<div class="sub header">""")
                append(name)
                append("""</div><div class="content">""")
                append(text?.asHTML() ?: "—")
                append("""</div>""")
            }
            append("""</div>""")
        }
    }
    append("""</div>""")
    if (tags.isNotEmpty()) {
        append("""<div class="extra">""")
        tags.joinTo(this, "") { """<span><i class="tag red icon" style="color: ${it.solidBackgroundColor} !important"></i> ${it.name}</span>""" }
        append("""</div>""")
    }
    append("""</div>""")
    append("""</div>""")
    append("""</div>""")
}

private fun String.asHTML(): String {
    return replace("\n", "<br>")
}
