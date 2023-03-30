@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2

import dev.fritz2.core.Tag
import dev.fritz2.headless.foundation.TagFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.w3c.dom.Element

/** Returns a [TagFactory] that handles [E] typed elements instead of the generic [Element] as vanilla [RenderContext.custom] does. */
public fun <E : Element> custom(localName: String): TagFactory<Tag<E>> = { renderContext, baseClass, customId, customScope, content ->
    renderContext.custom(localName, baseClass, customId, customScope) {
        content.invoke(unsafeCast<Tag<E>>())
    }.unsafeCast<Tag<E>>()
}

public operator fun Flow<Boolean>.not(): Flow<Boolean> = map { !it }
