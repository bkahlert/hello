package com.bkahlert.hello.semanticui.custom

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElementScope
import org.w3c.dom.HTMLDivElement

public interface Configurer<out T> {
    public val name: String
    public val icon: Array<String>
    public @Composable fun SemanticElementScope<SemanticElement<HTMLDivElement>>.Content(
        onComplete: (T) -> Unit,
    )
}
