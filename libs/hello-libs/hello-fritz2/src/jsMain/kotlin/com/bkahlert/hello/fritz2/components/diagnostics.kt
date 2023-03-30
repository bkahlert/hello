package com.bkahlert.hello.fritz2.components

import com.bkahlert.hello.fritz2.ContentBuilder
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.Window
import dev.fritz2.core.storeOf
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

public fun RenderContext.diagnostics(
    enabled: Store<Boolean> = storeOf(false),
    content: ContentBuilder? = null,
) {

    Window.keydowns
        .filter { it.key.equals("f4", ignoreCase = true) }
        .map { !enabled.current } handledBy enabled.update

    slideOver(
        enabled,
        name = "Diagnostics",
    ) {
        div {
            +"Hello, Fritz2!"
        }
        content?.invoke(this)
    }
}
