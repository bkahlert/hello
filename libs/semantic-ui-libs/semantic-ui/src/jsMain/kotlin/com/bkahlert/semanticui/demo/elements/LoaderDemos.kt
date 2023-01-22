package com.bkahlert.semanticui.demo.elements

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.LoremIpsumText
import com.bkahlert.semanticui.demo.custom.SemanticDemo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.States
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.element.ImageHeader
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.Paragraph
import com.bkahlert.semanticui.element.Placeholder
import com.bkahlert.semanticui.element.PlaceholderElement
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.TextLoader
import com.bkahlert.semanticui.element.active
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.fluid
import com.bkahlert.semanticui.element.indeterminate
import com.bkahlert.semanticui.element.inline
import com.bkahlert.semanticui.element.inlineCenter
import com.bkahlert.semanticui.element.inverted
import com.bkahlert.semanticui.element.size
import org.jetbrains.compose.web.dom.Text

public val LoaderDemos: SemanticDemo = SemanticDemo(
    "Loader",
    Types {
        Demo("Loader", { v.inverted() }) {
            Content { v.inverted() }
            Loader({ s.active() })
        }

        Demo("Text Loader", { v.inverted() }) {
            Content { v.inverted() }
            TextLoader({ s.active() }) { Text("Loading...") }
        }
    },
    States {
        Demo("Indeterminate") {
            Content()
            S("ui", "active", "dimmer") {
                Loader({ s.indeterminate() })
            }
        }
        Demo("Not Active") {
            Content()
            S("ui", "dimmer") {
                Loader()
            }
        }
        Demo("Disabled") {
            Content()
            S("ui", "active", "dimmer") {
                Loader({ s.disabled() })
            }
        }
    },
    Variations {
        Demo("Inline", { v.inverted() }) {
            Loader({
                s.active()
                v.inline()
            })
            LoremIpsumText()
        }
        Demo("Inline Center", { v.inverted() }) {
            Loader({
                s.active()
                v.inlineCenter()
            })
            LoremIpsumText()
        }
        Demo("Size") {
            Size.forEach {
                Segment({ v.inverted() }) {
                    Content { v.inverted() }
                    TextLoader({
                        s.active()
                        v.size(it)
                    }) { Text("$it") }
                }
            }
        }
        Demo("Inverted") {
            Content { v.inverted() }
            Loader({
                s.active()
                v.inverted()
            })
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
