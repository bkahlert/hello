package com.bkahlert.semanticui.core.dom

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.SemanticAttrsScope
import org.w3c.dom.Element

public typealias SemanticAttrBuilderContext<T> = SemanticAttrsScope<T>.() -> Unit
public typealias SemanticContentBuilder<T> = @Composable SemanticElementScope<T>.() -> Unit

public interface SemanticElement<out TElement : Element>

public interface SemanticElementType<TSegment : SemanticElement<Element>> {
    public val classNames: Array<out String>
    public operator fun invoke(attrsScope: SemanticAttrsScope<TSegment>) {
        attrsScope.classes(*classNames)
    }
}
