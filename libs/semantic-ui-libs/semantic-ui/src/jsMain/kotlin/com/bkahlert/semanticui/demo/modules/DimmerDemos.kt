package com.bkahlert.semanticui.demo.modules

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.VerticallyAligned.Bottom
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.demo.DEMO_BASE_DELAY
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.ComponentType
import com.bkahlert.semanticui.demo.custom.SemanticDemo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.States
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Variations
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.IconSubHeader
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Paragraph
import com.bkahlert.semanticui.element.Placeholder
import com.bkahlert.semanticui.element.PlaceholderElement
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.SubHeader
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.fluid
import com.bkahlert.semanticui.module.Dimmer
import com.bkahlert.semanticui.module.PageDimmer
import com.bkahlert.semanticui.module.active
import com.bkahlert.semanticui.module.blurring
import com.bkahlert.semanticui.module.dimmable
import com.bkahlert.semanticui.module.disabled
import com.bkahlert.semanticui.module.inverted
import com.bkahlert.semanticui.module.verticallyAligned
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Text
import kotlin.time.times

public val DimmerDemos: SemanticDemo = SemanticDemo(
    ComponentType.Module,
    "Dimmer",
    Types {
        Demo("Dimmer") {
            Segment {
                Content()
                Dimmer({ s.active() })
            }
        }

        Demo("Content Dimmer") {
            Segment {
                Content()
                Dimmer({ s.active() }) {
                    IconSubHeader("heart", attrs = { v.inverted() }) { Text("Dimmed Message!") }
                }
            }
        }

        Demo("Page Dimmer") { scope ->
            val logger = remember { ConsoleLogger("PageDimmerDemo") }
            var dim by remember { mutableStateOf(false) }
            Button({
                if (dim) s.disabled()
                else onClick {
                    dim = true
                    scope.launch {
                        delay(2 * DEMO_BASE_DELAY)
                    }
                }
            }) {
                if (dim) Text("Dimmed")
                else Text("Trigger")
            }
            PageDimmer({
                settings {
                    if (dim) s.active()
                    onHide = {
                        logger.info("onHide")
                        dim = false
                    }
                }
            }) {
                IconSubHeader("heart", attrs = { v.inverted() }) { Text("Dimmed Message!") }
            }
        }
    },
    States {
        Demo("Active") {
            Segment {
                Content()
                Dimmer({ s.active() })
            }
        }
        Demo("Disabled") {
            Segment {
                Content()
                Dimmer({ s.disabled() })
            }
        }
    },
    Variations {
        Demo("Blurring") {
            Segment({ v.blurring().dimmable() }) {
                Content()
                Dimmer({ s.active() })
            }
            Segment({ v.blurring().dimmable() }) {
                Content()
                Dimmer({ v.inverted(); s.active() })
            }
        }
        Demo("Vertical Alignment") {
            Segment {
                Content()
                Dimmer({
                    s.active()
                    v.verticallyAligned(Bottom)
                }) {
                    SubHeader({ v.inverted() }) { Text("Bottom Aligned") }
                }
            }
        }
        Demo("Inverted") {
            Segment {
                Content()
                Dimmer({
                    s.active()
                    v.inverted()
                })
            }
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
        Paragraph {
            Line()
            Line()
            Line()
            Line()
        }
    }
}
