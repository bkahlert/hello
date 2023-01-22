package playground.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.dom.data
import com.bkahlert.kommons.logging.debug
import com.bkahlert.semanticui.custom.DimmingLoader
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.element.Line
import com.bkahlert.semanticui.element.Placeholder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.events.EventListener

@Composable
fun <T> DispatchingLoader(
    name: String,
    load: suspend () -> T,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    onLoad: @Composable (T) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var loaded: Result<T>? by remember(name, load, onLoad) { mutableStateOf(null) }

    DimmingLoader(loaded == null) {
        Text("Loading $name")
    }

    when (val result = loaded) {
        null -> {
            Placeholder {
                Line()
                Line()
                Line()
                Line()

                DisposableEffect(name, load, onLoad) {
                    val container = scopeElement.parentElement as HTMLElement
                    container.data(DATA_BACKUP_KEY, container.data(DATA_BACKUP_KEY) ?: container.style.transition)

                    console.debug("Dimming", container)
                    container.addClass("dimmable")

                    val dimmer = container.children.asList().first { it.classList.contains("dimmer") }
                    val dimmerHeight = dimmer.scrollHeight

                    console.debug("Setting min-height=$dimmerHeight and max-height=$dimmerHeight with transition")
                    container.style.transition = "min-height 0.4s ease-in-out, max-height 0.4s ease-in-out"
                    container.style.minHeight = "${dimmerHeight}px"
                    container.style.maxHeight = "${dimmerHeight}px"

                    onDispose {
                        lateinit var transitionendEventListener: EventListener
                        transitionendEventListener = EventListener {
                            val backup = container.data(DATA_BACKUP_KEY)
                            container.data(DATA_BACKUP_KEY, null)
                            container.removeEventListener("transitionend", transitionendEventListener)
                            container.style.maxHeight = ""
                            container.style.minHeight = ""
                            container.style.transition = backup ?: ""
                            container.removeClass("dimmable")
                            console.debug("Cleaned up", container)
                        }
                        val containerHeight = container.scrollHeight + 100
                        val minHeight = "0px"
                        val maxHeight = "${containerHeight}px"
                        if (container.style.minHeight == "" && container.style.maxHeight == "") {
                            console.debug("Concurrent transition seems to have run")
                        } else {
                            container.addEventListener("transitionend", transitionendEventListener)
                            console.debug("Setting min-height=$minHeight and max-height=$maxHeight with transition")
                            container.style.minHeight = minHeight
                            container.style.maxHeight = maxHeight
                        }
                    }
                }
            }
            coroutineScope.launch(dispatcher) {
                console.debug("Loading $name")
                loaded = kotlin.runCatching { load() }
            }
        }

        else -> result
            .onSuccess {
                console.debug("Loaded $name: $it")
                onLoad(it)
            }.onFailure {
                console.warn("Failed to load $name: $it")
                ErrorMessage(it)
                Button({
                    onClick {
                        val target = it.target
                        when (val container = (target as? HTMLElement)?.parentElement as? HTMLElement) {
                            null -> console.warn("Failed to compute height of container")
                            else -> container.style.minHeight = "${container.scrollHeight}px"
                        }
                        loaded = null
                    }
                }) {
                    Text("Retry")
                }
            }
    }
}

private const val DATA_BACKUP_KEY = "dispatching-loader-transition-backup"
