package com.bkahlert.hello.clickup.ui.widgets

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.element.Icon
import com.bkahlert.hello.semanticui.element.IconElement
import org.jetbrains.compose.web.dom.Text

@Composable
fun MetaIcon(
    meta: Meta,
    attrs: SemanticAttrBuilderContext<IconElement>? = null,
) {
    Icon {
        title(meta.title)
        attrs?.invoke(this)
        classes(*meta.iconVariations.toTypedArray())
    }
    meta.text?.also { Text(it) }
}
