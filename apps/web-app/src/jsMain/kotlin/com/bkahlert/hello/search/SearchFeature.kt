package com.bkahlert.hello.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.bkahlert.hello.Feature
import com.bkahlert.semanticui.core.jQuery
import org.jetbrains.compose.web.dom.DOMScope
import org.w3c.dom.HTMLElement

class SearchFeature : Feature {
    override val name: String = "Search"
    override val loaded: Boolean = true
    override val content: @Composable DOMScope<HTMLElement>.() -> Unit = {
        PasteHandlingMultiSearchInput()
        DisposableEffect(Unit) {
            jQuery(scopeElement).find("[type=search]").focus()
            onDispose { }
        }
    }
}
