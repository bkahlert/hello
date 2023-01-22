package playground.experiments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.bkahlert.kommons.logging.InlineLogging
import com.bkahlert.semanticui.collection.Message
import com.bkahlert.semanticui.collection.info
import com.bkahlert.semanticui.collection.size
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Small
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.element.PrimaryButton
import com.bkahlert.semanticui.element.SecondaryButton
import com.bkahlert.semanticui.element.compact
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
        private val logger by InlineLogging

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

    Demo("Function based Hot Flow") {
        val model = remember { FunctionBasedHotFlowModel("foo bar baz") }

        Message({
            v.info()
            v.size(Small)
        }) {
            val state by model.testState.collectAsState(TextTestState("initial"))
            Text(state.text.also { console.log("rendering $it") })
        }
        PrimaryButton({
            v.compact()
            onClick { model.rotate() }
        }) {
            Text("Rotate")
        }
        SecondaryButton({
            v.compact()
            onClick { model.reset() }
        }) {
            Text("Reset")
        }
    }
}
