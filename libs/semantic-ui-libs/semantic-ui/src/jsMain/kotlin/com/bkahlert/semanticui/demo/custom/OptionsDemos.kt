package com.bkahlert.semanticui.demo.custom

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.custom.Options
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.element.ImageHeader
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Paragraph
import com.bkahlert.semanticui.element.Placeholder
import com.bkahlert.semanticui.element.PlaceholderElement
import com.bkahlert.semanticui.element.fluid

public val OptionsDemos: SemanticDemo = SemanticDemo(
    null,
    "Options",
    Variations {
        Demo("No Options") {
            Options()
        }
        Demo("One Option") {
            Options(
                { Content() },
            )
        }
        Demo("Two Options") {
            Options(
                { Content() },
                { Content() },
            )
        }
        Demo("Three Options") {
            Options(
                { Content() },
                { Content() },
                { Content() },
            )
        }
    },
)


@Composable
private fun Content(
    attrs: SemanticAttrBuilderContext<PlaceholderElement>? = null,
) {
    Placeholder({
        v.fluid()
        attrs?.invoke(this)
    }) {
        ImageHeader {
            Line()
            Line()
        }
        Paragraph {
            Line()
            Line()
            Line()
            Line()
        }
    }
}
