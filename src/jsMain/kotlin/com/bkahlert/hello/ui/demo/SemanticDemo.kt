package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import com.semanticui.compose.element.Divider
import com.semanticui.compose.module.Checkbox
import com.semanticui.compose.module.CheckboxElementType.Radio
import com.semanticui.compose.module.CheckboxElementType.Slider
import com.semanticui.compose.module.CheckboxElementType.Toggle
import org.jetbrains.compose.web.attributes.InputType.Checkbox
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.events.SyntheticChangeEvent

@Composable
fun SemanticDemo() {
    Demos("Semantic UI") {
        Demos("Elements") {
        }
        Demos("Collections") {
        }
        Demos("Views") {
        }
        Demos("Modules") {
            Demo("checkbox") {
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
        }
    }
}

private val handler: (SyntheticChangeEvent<*, *>) -> Unit = { console.info("${it.target} changed to ${it.value}") }
