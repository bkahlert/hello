package com.bkahlert.hello.semanticui.core.dom

import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.Element

public interface SemanticElementScope<out TSemantic : SemanticElement<Element>> : ElementScope<Element>

public class SemanticElementScopeBase<out TSemantic : SemanticElement<Element>>(
    elementScope: ElementScope<Element>,
) : SemanticElementScope<TSemantic>, ElementScope<Element> by elementScope
