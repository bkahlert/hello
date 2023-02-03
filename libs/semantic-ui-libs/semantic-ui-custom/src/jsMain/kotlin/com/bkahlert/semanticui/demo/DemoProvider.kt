package com.bkahlert.semanticui.demo

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import org.w3c.dom.HTMLDivElement

public data class DemoProvider(
    public val id: String,
    public val name: String,
    public val logo: CharSequence? = null,
    public val content: DemoContentBuilder,
)

public typealias DemoElement = SemanticElement<HTMLDivElement>
public typealias DemoContentBuilder = SemanticContentBuilder<DemoElement>

@Composable
public fun SemanticElementScope<DemoElement>.Grid(
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
): Unit = S("ui", "two", "column", "doubling", "grid", "container", attrs = attrs, content = content)

@Composable
public fun SemanticElementScope<DemoElement>.Column(
    attrs: SemanticAttrBuilderContext<SemanticElement<HTMLDivElement>>? = null,
    content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
): Unit = S("column", attrs = attrs, content = content)
