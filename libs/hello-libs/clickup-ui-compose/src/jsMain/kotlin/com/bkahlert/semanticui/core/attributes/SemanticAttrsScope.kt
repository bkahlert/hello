package com.bkahlert.semanticui.core.attributes

import com.bkahlert.semanticui.core.dom.SemanticElement
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.AttrsScopeBuilder
import org.w3c.dom.Element

public interface SemanticAttrsScope<out TSemantic : SemanticElement<Element>> : AttrsScope<Element> {
    public operator fun String.unaryPlus(): SemanticAttrsScope<TSemantic>
}

public open class SemanticAttrsScopeBuilder<out TSemantic : SemanticElement<Element>>(
    internal val attrsScope: AttrsScope<Element> = AttrsScopeBuilder(),
) : SemanticAttrsScope<TSemantic>,
    AttrsScope<Element> by attrsScope {

    override fun String.unaryPlus(): SemanticAttrsScope<TSemantic> {
        classes(this)
        return this@SemanticAttrsScopeBuilder
    }
}
