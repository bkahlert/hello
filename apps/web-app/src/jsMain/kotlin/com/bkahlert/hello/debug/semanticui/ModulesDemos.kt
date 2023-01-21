package com.bkahlert.hello.debug.semanticui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.element.Divider
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Paragraph
import com.bkahlert.semanticui.element.Placeholder
import com.bkahlert.semanticui.module.Checkbox
import com.bkahlert.semanticui.module.CheckboxElementType.Radio
import com.bkahlert.semanticui.module.CheckboxElementType.Slider
import com.bkahlert.semanticui.module.CheckboxElementType.Toggle
import com.bkahlert.semanticui.module.Dimmer
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.events.SyntheticChangeEvent

@Composable
fun ModulesDemos() {
    Demos("Modules") {
        Demo("checkbox") {
            val handler: (SyntheticChangeEvent<*, *>) -> Unit = { console.info("${it.target} changed to ${it.value}") }

            Checkbox {
                Input(Checkbox) {
                    name("my-checkbox")
                    onChange(handler)
                }
                Label { Text("Checkbox") }
            }
            Divider()
            Checkbox(Radio) {
                Input(Checkbox) {
                    name("my-radio")
                    onChange(handler)
                }
                Label { Text("Radio") }
            }
            Divider()
            Checkbox(Slider) {
                Input(Checkbox) {
                    name("my-slider")
                    onChange(handler)
                }
                Label { Text("Slider") }
            }
            Divider()
            Checkbox(Toggle) {
                Input(Checkbox) {
                    name("my-toggle")
                    onChange(handler)
                }
                Label { Text("Toggle") }
            }
        }
        Demo("Dimmer") {
            Placeholder {
                Paragraph {
                    Line()
                    Line()
                    Line()
                    Line()
                }
            }
            Dimmer({ raw(Modifier.State.Active) })
        }
        Demo("Dimmer (Inverted)") {
            Placeholder {
                Paragraph {
                    Line()
                    Line()
                    Line()
                    Line()
                }
            }
            Dimmer({ raw(Modifier.State.Active, Modifier.Variation.Inverted) })
        }
    }
}
