package com.bkahlert.semanticui.demo

import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

public data class DemoProvider(
    public val id: String,
    public val name: String,
    public val logo: CharSequence?,
    public val content: List<DemoContentBuilder>,
) {
    public constructor(
        id: String,
        name: String,
        logo: CharSequence?,
        vararg content: DemoContentBuilder,
    ) : this(id, name, logo, content.asList())

    public constructor(
        id: String,
        name: String,
        vararg content: DemoContentBuilder,
    ) : this(id, name, null, content.asList())
}

public typealias DemoContentBuilder = SemanticContentBuilder<SemanticElement<HTMLDivElement>>
