import com.bkahlert.kommons.dom.appendTypedElement
import com.bkahlert.kommons.js.Console
import com.bkahlert.kommons.js.tee
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.produceIn
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLPreElement

class VirtualConsole(from: Console) {
    val invocations = MutableSharedFlow<Pair<String, Array<out Any?>>>(
        replay = 20,
        onBufferOverflow = DROP_OLDEST,
    )

    private val attachments: MutableMap<Element, ReceiveChannel<*>> = mutableMapOf()

    init {
        listOf(
            "assert", "clear", "count", "countReset", "debug", "dir", "dirxml", "error", "group", "groupCollapsed", "groupEnd",
            "info", "log", "table", "time", "timeEnd", "timeLog", "timeStamp", "trace", "warn"
        ).forEach { fn ->
            from.tee(fn) { args -> invocations.tryEmit(fn to args) }
        }
    }

    fun attachTo(scope: CoroutineScope, container: Element) {
        invocations.onEach { (fn, args) ->
            container.appendTypedElement<HTMLPreElement>("pre") {
                appendTypedElement<HTMLAnchorElement>("a") {
                    classList.add("ui", "teal", "right", "ribbon", "label")
                    textContent = fn
                }
                appendTypedElement<HTMLElement>("code") {
                    with(style) {
                        marginTop = "0"
                        marginBottom = "0"
                    }
                    textContent = args.joinToString(" ")
                }
            }
        }.produceIn(scope).also {
            attachments.put(container, it)
        }
    }

    fun detachFrom(scopeElement: Element) {
        attachments.get(scopeElement)?.cancel()
    }
}
