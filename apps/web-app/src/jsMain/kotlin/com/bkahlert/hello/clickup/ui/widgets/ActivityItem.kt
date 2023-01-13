package com.bkahlert.hello.clickup.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticElementScope
import com.bkahlert.hello.semanticui.core.jQuery
import com.bkahlert.hello.semanticui.core.popup
import com.bkahlert.hello.semanticui.module.DropdownMenuElement
import com.bkahlert.hello.semanticui.module.DropdownMenuItemElement
import com.bkahlert.hello.semanticui.module.Item
import com.bkahlert.hello.ui.textOverflow
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Text

@Suppress("unused")
@Composable
fun SemanticElementScope<DropdownMenuElement>.ActivityItem(
    activity: Activity<*>,
    attrs: SemanticAttrBuilderContext<DropdownMenuItemElement>? = null,
) {
    Item({
        attr("data-text", activity.name)
        attr("data-value", activity.id.typedStringValue)
        attr("data-variation", "mini")
        attr("data-offset", "0")
        attr("data-position", "left center")
        attr("data-html", activity.popupHtml())
        style { textOverflow() }
        style { maxWidth(100.percent) }
        onMouseEnter { jQuery(it.target).popup("lastResort" to true).popup("show") }
        attrs?.invoke(this)
    }) {
        ActivityIcon(activity)
        Text(activity.name)
        DisposableEffect(activity) {
            onDispose {
                jQuery(scopeElement).popup("destroy")
            }
        }
    }
}

fun Activity<*>.popupHtml(): String = buildString {
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
