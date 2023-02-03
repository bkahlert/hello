package com.bkahlert.kommons.dom

import com.bkahlert.kommons.js.debug
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterIsInstance
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

/**
 * Returns a cold [Flow] that starts emitting events registered
 * by an [EventListener] added to the [EventTarget] with the specified [type] and [options]
 * the moment it's collected until the flow collection is cancelled.
 */
public fun EventTarget.asEventFlow(
    type: String,
    options: Any? = null,
): Flow<Event> = callbackFlow {
    val eventListener = EventListener { event: Event ->
        this.trySend(event)
            .onFailure { throwable ->
                console.error("Failed to send", type, event, throwable)
            }
    }
    console.debug("EventTarget: Adding", type, "eventListener")
    if (options != null) this@asEventFlow.addEventListener(type, eventListener, options)
    else this@asEventFlow.addEventListener(type, eventListener)
    console.info("EventTarget: Added", type, "eventListener")

    awaitClose {
        console.debug("EventTarget: Removing", type, "eventListener")
        if (options != null) this@asEventFlow.removeEventListener(type, eventListener, options)
        else this@asEventFlow.removeEventListener(type, eventListener)
        console.info("EventTarget: Removed", type, "eventListener")
    }
}

/**
 * Returns a cold [Flow] that starts emitting [T]-typed events registered
 * by an [EventListener] added to the [EventTarget] with the specified [options]
 * the moment it's collected until the flow collection is cancelled.
 *
 * The [type] used to register the event listener is derived from the
 * lowercase name of [T] without the `event` suffix (e.g. `SubmitEvent` -> **`submit`** *`event`*).
 *
 * Events like [KeyboardEvent] are used for different types (e.g. `keydown`, `keyup`, etc.) and
 * require manually specified [type] to work.
 */
public inline fun <reified T : Event> EventTarget.asEventFlow(
    type: String = checkNotNull(T::class.js.name) { "Missing event name for ${T::class.js}" }
        .lowercase()
        .removeSuffix("event"),
    options: Any? = null,
): Flow<T> = asEventFlow(
    type = type,
    options = options,
).filterIsInstance()
