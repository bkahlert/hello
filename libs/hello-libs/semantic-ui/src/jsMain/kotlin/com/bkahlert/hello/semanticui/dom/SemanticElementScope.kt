package com.bkahlert.hello.semanticui.dom

import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.Element

public interface SemanticElementScope<out TSemantic : SemanticElement, out TElement : Element> : ElementScope<TElement>

public class SemanticElementScopeBase<out TSemantic : SemanticElement, out TElement : Element>(
    elementScope: ElementScope<TElement>,
) : SemanticElementScope<TSemantic, TElement>, ElementScope<TElement> by elementScope
