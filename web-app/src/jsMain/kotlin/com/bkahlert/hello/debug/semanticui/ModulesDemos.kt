package com.bkahlert.hello.debug.semanticui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.hello.semanticui.element.Divider
import com.bkahlert.hello.semanticui.element.Line
import com.bkahlert.hello.semanticui.element.Paragraph
import com.bkahlert.hello.semanticui.element.Placeholder
import com.bkahlert.hello.semanticui.module.Checkbox
import com.bkahlert.hello.semanticui.module.CheckboxElementType.Radio
import com.bkahlert.hello.semanticui.module.CheckboxElementType.Slider
import com.bkahlert.hello.semanticui.module.CheckboxElementType.Toggle
import com.bkahlert.hello.semanticui.module.Dimmer
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
            Dimmer({ +Active })
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
            Dimmer({
                +Active
                +Inverted
            })
        }
    }
}
