package app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.Either
import com.bkahlert.kommons.Either.Left
import com.bkahlert.kommons.Either.Right
import com.bkahlert.kommons.logging.InlineLogging
import com.bkahlert.kommons.toEither
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.element.Icon
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun <T : Any> Loader(
    name: String,
    load: suspend () -> T,
    onLoad: @Composable (T) -> Unit,
) {
    val logger by InlineLogging
    val coroutineScope = rememberCoroutineScope()
    var loaded: Either<T, Throwable>? by mutableStateOf(null)

    Div {
        when (val result = loaded) {
            null -> {
                Text("Loading $name")
                Text(" ")
                Icon("loading", "spinner")
                coroutineScope.launch {
                    logger.debug("Loading $name")
                    loaded = kotlin.runCatching { load() }.toEither()
                }
            }

            is Left -> {
                logger.debug("Loaded $name: ${result.value}")
                onLoad(result.value)
            }

            is Right -> {
                logger.warn("Failed to load $name: ${result.value}")
                ErrorMessage(result.value)
                Button({
                    onClick {
                        loaded = null
                    }
                }) {
                    Text("Retry")
                }
            }
        }
    }
}
