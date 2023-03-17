package playground.components

import dev.fritz2.core.RenderContext
import dev.fritz2.core.ScopeContext
import dev.fritz2.core.classes
import dev.fritz2.headless.foundation.addComponentStructureInfo
import playground.fritz2.ContentBuilder

fun RenderContext.proseBox(
    classes: String? = null,
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    content: ContentBuilder? = null,
) {
    addComponentStructureInfo("container", this@proseBox.scope, this)
    div(classes("box-prose", classes), id, scope) { content?.invoke(this) }
}
