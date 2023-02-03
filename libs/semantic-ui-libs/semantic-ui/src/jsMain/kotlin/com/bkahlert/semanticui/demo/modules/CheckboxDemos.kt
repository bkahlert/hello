package com.bkahlert.semanticui.demo.modules

import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.ComponentType
import com.bkahlert.semanticui.demo.custom.SemanticDemo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.module.Checkbox
import com.bkahlert.semanticui.module.CheckboxElementType.Radio
import com.bkahlert.semanticui.module.CheckboxElementType.Slider
import com.bkahlert.semanticui.module.CheckboxElementType.Toggle
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.events.SyntheticChangeEvent

public val CheckboxDemos: SemanticDemo = SemanticDemo(
    ComponentType.Module,
    "Checkbox",
    Types {
        Demo("Checkbox") {
            Checkbox {
                Input(Checkbox) {
                    name("my-checkbox")
                    onChange(handler)
                }
                Label { Text("Checkbox") }
            }
        }
        Demo("Radio") {
            Checkbox(Radio) {
                Input(Checkbox) {
                    name("my-radio")
                    onChange(handler)
                }
                Label { Text("Radio") }
            }
        }
        Demo("Slider") {
            Checkbox(Slider) {
                Input(Checkbox) {
                    name("my-slider")
                    onChange(handler)
                }
                Label { Text("Slider") }
            }
        }
        Demo("Toggle") {
            Checkbox(Toggle) {
                Input(Checkbox) {
                    name("my-toggle")
                    onChange(handler)
                }
                Label { Text("Toggle") }
            }
        }
    },
)

private val handler: (SyntheticChangeEvent<*, *>) -> Unit = { console.info("${it.target} changed to ${it.value}") }
