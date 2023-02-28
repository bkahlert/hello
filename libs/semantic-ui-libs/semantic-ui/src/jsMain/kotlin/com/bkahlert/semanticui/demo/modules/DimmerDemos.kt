package com.bkahlert.semanticui.demo.modules

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.VerticallyAligned.Bottom
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.jQuery
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
import com.bkahlert.semanticui.module.dimmer
import com.bkahlert.semanticui.module.disabled
import com.bkahlert.semanticui.module.inverted
import com.bkahlert.semanticui.module.verticallyAligned
import org.jetbrains.compose.web.dom.Text

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

        Demo("Page Dimmer") {
            val logger = remember { ConsoleLogger("PageDimmerDemo") }
            var dim by remember { mutableStateOf(false) }
            Button({
                if (dim) s.disabled()
                else onClick { dim = true }
            }) {
                if (dim) Text("Dimmed")
                else Text("Trigger")
            }
            PageDimmer {
                IconSubHeader("heart", attrs = { v.inverted() }) { Text("Dimmed Message!") }
                if (dim) {
                    DisposableEffect(Unit) {
                        val dimmer = jQuery(scopeElement.parentElement)
                            .dimmer(
                                "onHide" to {
                                    logger.info("onHide")
                                    dim = false
                                },
                            )
                            .dimmer("show")
                        onDispose { dimmer.dimmer("destroy") }
                    }
                }
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
        Demo("Blurring", { v.blurring().dimmable() }) {
            Segment({ v.blurring().dimmable() }) {
                Content()
                Dimmer {
                    DisposableEffect(Unit) {
                        val dimmer = jQuery(scopeElement.parentElement).dimmer("show")
                        onDispose {
                            dimmer.dimmer("destroy").remove()
                        }
                    }
                }
            }
            Segment({ v.blurring().dimmable() }) {
                Content()
                Dimmer({ v.inverted() }) {
                    DisposableEffect(Unit) {
                        val dimmer = jQuery(scopeElement.parentElement).dimmer("show")
                        onDispose {
                            dimmer.dimmer("destroy").remove()
                        }
                    }
                }
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
