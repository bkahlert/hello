package com.bkahlert.hello.clickup.view

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElementScope
import org.w3c.dom.HTMLDivElement

public interface Configurer<out T> {
    public val name: String
    public val icon: Array<String>

    //    public val content: @Composable DOMScope<HTMLElement>.(onComplete: (T) -> Unit) -> Unit
    public val content: @Composable SemanticElementScope<SemanticElement<HTMLDivElement>>.(onComplete: (T) -> Unit) -> Unit
}