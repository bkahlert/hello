package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.IconElement
import org.jetbrains.compose.web.dom.Text

@Composable
public fun MetaIcon(
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
