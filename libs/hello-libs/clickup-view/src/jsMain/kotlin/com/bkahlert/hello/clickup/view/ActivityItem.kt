package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.js.debug
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.core.jQuery
import com.bkahlert.semanticui.custom.textOverflow
import com.bkahlert.semanticui.module.DropdownMenuElement
import com.bkahlert.semanticui.module.DropdownMenuItemElement
import com.bkahlert.semanticui.module.Item
import com.bkahlert.semanticui.module.popup
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Text

@Suppress("unused")
@Composable
public fun SemanticElementScope<DropdownMenuElement>.ActivityItem(
    activity: Activity<*>,
    attrs: SemanticAttrBuilderContext<DropdownMenuItemElement>? = null,
) {

    var showPopup by remember { mutableStateOf(false) }
    Item({
        attr("data-text", activity.name)
        attr("data-value", activity.id.typedStringValue)
        style { textOverflow() }
        style { maxWidth(100.percent) }
        onMouseEnter {
            console.debug("Activity: ${activity.id} — show popup")
            showPopup = true
        }
        onMouseLeave { showPopup = false }
        attrs?.invoke(this)
    }) {
        ActivityIcon(activity)
        Text(activity.name)
        DisposableEffect(showPopup) {
            val popup = if (showPopup) {
                jQuery(scopeElement)
                    .popup(
                        "lastResort" to true,
                        "variation" to "mini",
                        "position" to "left center",
                        "html" to activity.popupHtml(),
                    )
                    .popup("show")
            } else {
                null
            }
            onDispose {
                if (popup != null) {
                    console.debug("Activity: ${activity.id} — destroy popup")
                    popup.popup("destroy")
                }
            }
        }
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
