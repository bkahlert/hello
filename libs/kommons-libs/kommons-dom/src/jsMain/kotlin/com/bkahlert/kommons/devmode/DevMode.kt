package com.bkahlert.kommons.devmode

import com.bkahlert.kommons.devmode.DevModeState.Started
import com.bkahlert.kommons.devmode.DevModeState.Stopped
import com.bkahlert.kommons.devmode.DevModeState.Stopped.session
import com.bkahlert.kommons.dom.LocationFragmentParameters
import com.bkahlert.kommons.dom.appendDivElement
import com.bkahlert.kommons.dom.appendTypedElement
import com.bkahlert.kommons.dom.asEventFlow
import com.bkahlert.kommons.dom.body
import com.bkahlert.kommons.js.console
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.dom.appendText
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

/**
 * Development mode that binds the lifecycle of a [DevSession]
 * to the fragement parameter with the specified [name] (default: `debug`, e.g. `#debug`).
 *
 * The optional [keyboardEventTarget] (default: `document.body`) is used to bind
 * the `F4` to [DevMode.toggle], and
 * the `ESC` to [DevMode.stop].
 */
public class DevMode<out T : DevSession>(
    private val name: String = "debug",
    keyboardEventTarget: EventTarget? = document.body(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    scope: CoroutineScope = CoroutineScope(dispatcher),
    createSession: () -> T,
) {

    private val fragmentParameters = LocationFragmentParameters(window)
    private val fragmentParameterFlow = fragmentParameters.asStateFlow(scope, SharingStarted.Eagerly, name)

    public fun start(trigger: Event) {
        console.debug("Start triggered by", trigger)
        fragmentParameters.setAll(name, emptyList())
    }

    public fun toggle(trigger: Event): Boolean {
        console.debug("Toggle triggered by", trigger)
        val started = fragmentParameters.contains(name)
        if (started) stop(trigger)
        else start(trigger)
        return !started
    }

    public fun stop(trigger: Event) {
        console.debug("Stop triggered by", trigger)
        fragmentParameters[name] = null
    }

    private val sessionStateFlow: MutableStateFlow<DevModeState<T>> = MutableStateFlow(Stopped)

    public val activationFlow: StateFlow<Boolean> = fragmentParameterFlow
        .map { it != null }
        .map { activate ->
            sessionStateFlow.updateAndGet { current ->
                console.debug("DevMode: Update $current to $activate")
                when (current) {
                    is Started -> {
                        if (activate) {
                            current
                        } else {
                            current.session.dispose()
                            Stopped
                        }
                    }

                    is Stopped -> {
                        if (activate) {
                            Started(createSession)
                        } else {
                            current
                        }
                    }
                }
            } is Started
        }
        .stateIn(scope, SharingStarted.Eagerly, fragmentParameterFlow.value != null)


    init {
        if (false) keyboardEventTarget
            ?.asEventFlow<KeyboardEvent>("keydown")
            ?.filter { it.target == keyboardEventTarget }
            ?.onEach {
                when (it.key.uppercase()) {
                    "F4" -> toggle(it)
                    "ESCAPE" -> stop(it)
                }
            }
            ?.stateIn(scope, SharingStarted.Eagerly, null)

        bindMenu(document.body())
    }
}

/** State of a [DevMode] */
public sealed interface DevModeState<out T : DevSession> {
    /** The running session, if any. */
    public val session: T?

    /** Started state with [session]. */
    public class Started<T : DevSession>(
        create: () -> T,
    ) : DevModeState<T> {
        override val session: T = create()
        override fun toString(): String = "Started($session)"
    }

    /** Stopped state no [session]. */
    public object Stopped : DevModeState<Nothing> {
        override val session: Nothing? = null
        override fun toString(): String = "Stopped"
    }
}

/**
 * Creates a hover menu
 * that can be used to trigger [DevMode.toggle].
 */
private fun DevMode<*>.bindMenu(
    root: Element,
) {
    root.appendDivElement {
        val controls = this
        classList.add("dev-mode-controls")
        appendDivElement {
            classList.add("ui", "button")
            tabIndex = 0
            addEventListener("click", EventListener {
                when (toggle(it)) {
                    true -> controls.classList.add("active")
                    else -> controls.classList.remove("active")
                }
            })
            addEventListener("focus", EventListener {
                console.debug("DevMode: Focus event", it)
                it.target?.asDynamic()?.blur()
                Unit
            })
            appendText("Debug")
            appendTypedElement<HTMLElement>("i") { classList.add("right", "wrench", "icon") }
        }
    }
}
