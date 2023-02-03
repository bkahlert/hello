package com.bkahlert.semanticui.demo.custom

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.LinkItem
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Small
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.custom.apply
import com.bkahlert.semanticui.demo.DEMO_BASE_DELAY
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.States
import com.bkahlert.semanticui.demo.custom.SemanticDemoSection.Types
import com.bkahlert.semanticui.element.size
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.Text

public val LoadingStateDemos: SemanticDemo = SemanticDemo(
    null,
    "LoadingState",
    Types {
        Demo("${LoadingState.On::class.simpleName}") {
            var loadingState: LoadingState by mutableStateOf(LoadingState.On)

            Menu({ apply(loadingState) }) {
                apply(loadingState) { v.size(Small) }

                LaunchedEffect(loadingState) {
                    delay(DEMO_BASE_DELAY)
                    loadingState = LoadingState.Off
                }

                listOf(LoadingState.On, LoadingState.Indeterminate).forEach { newLoadingState ->
                    LinkItem({
                        onClick { loadingState = newLoadingState }
                    }) { Text("Set ${newLoadingState::class.simpleName}") }
                }
            }
        }
    },
    States {
        Demo("${LoadingState.On::class.simpleName}") {
            Menu({ apply(LoadingState.On) }) {
                apply(LoadingState.On) { v.size(Small) }
                Item { Text("Foo") }
                Item { Text("Bar") }
            }
        }
        Demo("${LoadingState.Indeterminate::class.simpleName}") {
            Menu({ apply(LoadingState.Indeterminate) }) {
                apply(LoadingState.Indeterminate) { v.size(Small) }
                Item { Text("Foo") }
                Item { Text("Bar") }
            }
        }
        Demo("${LoadingState.Off::class.simpleName}") {
            Menu({ apply(LoadingState.Off) }) {
                apply(LoadingState.Off) { v.size(Small) }
                Item { Text("Foo") }
                Item { Text("Bar") }
            }
        }
    },
)
