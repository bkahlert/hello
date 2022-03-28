package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bkahlert.hello.ui.textOverflow
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.jQuery
import com.semanticui.compose.popup
import com.semanticui.compose.view.Item
import com.semanticui.compose.view.ItemElement
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun ActivityItem(
    activity: Activity<*>,
    attrs: SemanticAttrBuilder<ItemElement, HTMLDivElement>? = null,
) {
    Item({
        attr("data-text", activity.name)
        attr("data-value", activity.id?.typedStringValue ?: "")
        attr("data-variation", "mini")
        attr("data-offset", "0")
        attr("data-position", "left center")
        attr("data-html", activity.popupHtml())
        style {
            textOverflow()
        }
        attrs?.invoke(this)
    }) {
        ActivityIcon(activity)
        Text(activity.name)
        DisposableEffect(activity) {
            jQuery(scopeElement).popup("lastResort" to true)
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
