package com.bkahlert.semanticui.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.kommons.dom.appendDivElement
import com.bkahlert.kommons.dom.body
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.js.toString
import com.bkahlert.semanticui.module.Modal
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.dom.appendElement
import kotlinx.dom.appendText
import org.w3c.dom.Element
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A static [Modal] that shows the specified [message] and [throwable].
 */
@Suppress("FunctionName")
public fun ErrorMessageModal(
    message: String,
    throwable: Throwable,
) {
    lateinit var container: Element
    container = document.body().appendDivElement {
        classList.add("ui", "dimmer", "modals", "page", "visible", "active")
        style.display = "flex !important"
        appendDivElement {
            classList.add("ui", "small", "basic", "modal", "visible", "active")
            style.display = "block !important"
            appendDivElement {
                classList.add("ui", "icon", "header")
                appendElement("i") { classList.add("red", "exclamation", "triangle", "icon") }
                appendDivElement {
                    classList.add("content")
                    appendText("Oops, an error occurred")
                    appendElement("br") {}
                    appendElement("small") { appendText(message) }
                }
            }
            appendDivElement {
                classList.add("scrolling", "content")
                appendElement("p") { appendText(throwable.errorMessage) }
                appendDivElement {
                    classList.add("ui", "inverted", "accordion")
                    appendDivElement {
                        classList.add("active", "title")
                        appendElement("i") { classList.add("dropdown", "icon") }
                        appendText("Stacktrace")
                    }
                    appendDivElement {
                        classList.add("active", "content")
                        appendElement("pre") {
                            with(style) {
                                textAlign = "left"
                                overflowX = "hidden"
                                textOverflow = "ellipsis"
                            }
                            appendText(throwable.stackTraceToString())
                        }
                    }
                }
            }
            appendDivElement {
                classList.add("actions")
                appendDivElement {
                    classList.add("ui", "inverted", "red", "button")
                    onclick = fun(event) { if (event.target == this) container.remove() }
                    appendElement("i") { classList.add("remove", "icon") }
                    appendText("Abort")
                }
                appendDivElement {
                    classList.add("ui", "inverted", "yellow", "button")
                    onclick = fun(event) { if (event.target == this) window.location.reload() }
                    appendElement("i") { classList.add("redo", "alternate", "icon") }
                    appendText("Reload")
                }
            }
        }
    }
}

private val logger = ConsoleLogger("CoroutineExceptionHandler")

/**
 * An [CoroutineExceptionHandler] that handles exceptions by showing a [ErrorMessageModal].
 */
public val ErrorMessageModalCoroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { context, exception ->
    val message = "Uncaught exception in $context"
    logger.error(message, exception)
    ErrorMessageModal(message, exception)
}.also { handler ->
    handler.toString { "ErrorMessageModalCoroutineExceptionHandlerSingleton" }
}

/**
 * Launches a new coroutine using [launch]
 * and [ErrorMessageModalCoroutineExceptionHandler].
 */
public fun CoroutineScope.launchReporting(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
): Job = launch(ErrorMessageModalCoroutineExceptionHandler, start, block)

/**
 * Creates a [CoroutineScope] with a [SupervisorJob] and the [ErrorMessageModalCoroutineExceptionHandler]
 * installed.
 */
public fun ReportingCoroutineScope(): CoroutineScope =
    CoroutineScope(SupervisorJob() + ErrorMessageModalCoroutineExceptionHandler)

/**
 * Return a [CoroutineScope] using [rememberCoroutineScope]
 * but with an [ErrorMessageModalCoroutineExceptionHandler] installed.
 */
@Composable
public inline fun rememberReportingCoroutineScope(
    crossinline getContext: @DisallowComposableCalls () -> CoroutineContext =
        { EmptyCoroutineContext },
): CoroutineScope = rememberCoroutineScope { getContext() + ErrorMessageModalCoroutineExceptionHandler }
