package com.bkahlert.hello.semanticui.dom

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.attributes.SemanticAttrsScope
import com.bkahlert.hello.semanticui.attributes.State
import com.bkahlert.hello.semanticui.attributes.Variation

public typealias SemanticAttrBuilderContext<S, T> = SemanticAttrsScope<S, T>.() -> Unit
public typealias SemanticContentBuilder<S, T> = @Composable SemanticElementScope<S, T>.() -> Unit

public interface SemanticElement {
    public fun classes(states: List<State>, variations: List<Variation>): List<String>
}

public interface SemanticElementType<TSegment : SemanticElement> {
    public val classNames: Array<out String>
    public operator fun invoke(attrsScope: SemanticAttrsScope<TSegment, *>) {
        attrsScope.classes(*classNames)
    }
}
