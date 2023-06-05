package com.bkahlert.kommons.dom

import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.js.grouping
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterIsInstance
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

private val logger = ConsoleLogger("hello.events")

/**
 * Returns a cold [Flow] that starts emitting events registered
 * by an [EventListener] added to the [EventTarget] with the specified [type] and [options]
 * the moment it's collected until the flow collection is cancelled.
 */
public fun EventTarget.asEventFlow(
    type: String,
    options: Any? = null,
): Flow<Event> = logger.grouping("asEventFlow", "target=${this::class.js.name}", "type=$type", "options=$options") {
    callbackFlow {
        val eventListener = EventListener { event: Event ->
            this.trySend(event)
                .onFailure { throwable ->
                    logger.error("Failed to send", type, event, throwable)
                }
        }

        val target = this@asEventFlow
        val targetName = this@asEventFlow::class.js.name

        logger.grouping("addEventListener", "target=$targetName", "type=$type", "options=$options") {
            if (options != null) target.addEventListener(type, eventListener, options)
            else target.addEventListener(type, eventListener)
        }

        awaitClose {
            logger.grouping("removeEventListener", "target=$targetName", "type=$type", "options=$options") {
                if (options != null) target.removeEventListener(type, eventListener, options)
                else target.removeEventListener(type, eventListener)
            }
        }
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
