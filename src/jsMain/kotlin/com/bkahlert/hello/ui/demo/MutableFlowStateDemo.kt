package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.kommons.coroutines.flow.FlowUpdate
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.applyUpdates
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.extend
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.reset
import com.semanticui.compose.collection.Message
import com.semanticui.compose.collection.MessageElementType.Info
import com.semanticui.compose.element.Button
import com.semanticui.compose.element.ButtonElementType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration.Companion.seconds

@Composable
fun MutableFlowStateDemo() {

    data class TextTestState(
        val text: String,
    )

    class FunctionBasedHotFlowModel(initialText: String) {
        private val logger = simpleLogger()

        private val _testState = MutableStateFlow(TextTestState(initialText))
        private val updateFunction = MutableStateFlow<(suspend (TextTestState) -> TextTestState)?>(null)
        private val updatedTestState = MutableStateFlow<TextTestState?>(null)
        val testState: Flow<TextTestState> =
            _testState.combine(updatedTestState) { old, new ->
                val merged = new ?: old
                logger.debug("combined $old and $new to $merged")
                merged
            }.combine(updateFunction) { state: TextTestState, update ->
                if (update != null) {
                    logger.debug("updating $state")
                    update(state)
                } else {
                    logger.debug("taking $state")
                    state
                }
            }.onEach { updated ->
                logger.debug("computed $updated")
                updatedTestState.update { updated }
                updateFunction.update { null }
            }

        private fun update(updateFunction: suspend TextTestState.() -> TextTestState) {
            this.updateFunction.update { updateFunction }
        }

        fun reset() {
            updatedTestState.update { null }
        }

        fun rotate() {
            update {
                logger.debug("rotating $text")
                delay(1.seconds)
                copy(text = text.split(' ').let {
                    buildList {
                        add(it.last())
                        addAll(it.dropLast(1))
                    }
                }.joinToString(" "))
            }
        }
    }


    class FlowUpdateBasedHotFlowModel(initialText: String) {
        private val logger = simpleLogger()

        private val _testState = MutableStateFlow(TextTestState(initialText))
        private val testStateUpdate = FlowUpdate<TextTestState>()
        val testState: Flow<TextTestState> = testStateUpdate.applyUpdates(_testState)

        fun rotate() {
            testStateUpdate.extend {
                logger.debug("rotating ${it.text}")
                delay(1.seconds)
                it.copy(text = it.text.split(' ').let {
                    buildList {
                        add(it.last())
                        addAll(it.dropLast(1))
                    }
                }.joinToString(" "))
            }
        }

        fun reset() {
            testStateUpdate.reset()
        }
    }

    Demos("Mutable Flow State") {
        Demo("Function based Hot Flow") {
            val model = remember { FunctionBasedHotFlowModel("foo bar baz") }

            Message(Info, { +Size.Small }) {
                val state by model.testState.collectAsState(TextTestState("initial"))
                Text(state.text.also { console.log("rendering $it") })
            }
            Button(ButtonElementType.Primary, {
                +Compact
                onClick { model.rotate() }
            }) {
                Text("Rotate")
            }
            Button(ButtonElementType.Secondary, {
                +Compact
                onClick { model.reset() }
            }) {
                Text("Reset")
            }
        }

        Demo("FlowUpdate based Hot Flow") {
            val model = remember { FlowUpdateBasedHotFlowModel("foo bar baz") }

            Message(Info, { +Size.Small }) {
                val state by model.testState.collectAsState(TextTestState("initial"))
                Text(state.text.also { console.log("rendering $it") })
            }
            Button(ButtonElementType.Primary, {
                +Compact
                onClick { model.rotate() }
            }) {
                Text("Rotate")
            }
            Button(ButtonElementType.Secondary, {
                +Compact
                onClick { model.reset() }
            }) {
                Text("Reset")
            }
        }
    }
}
