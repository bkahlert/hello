package com.bkahlert.hello.clickup.ui.widgets

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.attributes.SemanticAttrsScope
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.element.IconElement
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

@Composable
fun MetaIcon(
    meta: Meta,
    attrs: (SemanticAttrsScope<IconElement, HTMLElement>.() -> Unit)? = null,
) {
    Icon {
        title(meta.title)
        attrs?.invoke(this)
        classes(*meta.iconVariations.toTypedArray())
    }
    meta.text?.also { Text(it) }
}
