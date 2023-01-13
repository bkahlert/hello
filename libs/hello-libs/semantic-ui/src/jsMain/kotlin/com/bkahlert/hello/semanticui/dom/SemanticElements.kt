package com.bkahlert.hello.semanticui.dom

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.attributes.SemanticAttrsScope
import com.bkahlert.hello.semanticui.attributes.State
import com.bkahlert.hello.semanticui.attributes.Variation
import org.w3c.dom.Element

public typealias SemanticAttrBuilderContext<T> = SemanticAttrsScope<T>.() -> Unit
public typealias SemanticContentBuilder<T> = @Composable SemanticElementScope<T>.() -> Unit

public interface SemanticElement<out TElement : Element> {
    public fun classes(states: List<State>, variations: List<Variation>): List<String>
}

public interface SemanticElementType<TSegment : SemanticElement<Element>> {
    public val classNames: Array<out String>
    public operator fun invoke(attrsScope: SemanticAttrsScope<TSegment>) {
        attrsScope.classes(*classNames)
    }
}
