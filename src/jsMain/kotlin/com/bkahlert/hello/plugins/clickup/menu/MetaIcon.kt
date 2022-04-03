package com.bkahlert.hello.plugins.clickup.menu

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.IconElement
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

@Composable
fun MetaIcon(
    meta: Meta,
    attrs: SemanticAttrBuilder<IconElement, HTMLElement>? = null,
) {
    Icon {
        title(meta.title)
        attrs?.invoke(this)
        style { classes(*meta.iconVariations.toTypedArray()) }
    }
    meta.text?.also { Text(it) }
}
