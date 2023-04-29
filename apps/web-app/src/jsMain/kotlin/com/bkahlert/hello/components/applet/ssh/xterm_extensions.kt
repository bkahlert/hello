package com.bkahlert.hello.components.applet.ssh

import com.bkahlert.kommons.js.ConsoleLogger
import js.core.jso
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** The dimensions of the terminal. */
val Terminal.dimensions
    get() = jso<ITerminalDimensions> {
        this.cols = cols;
        this.rows = rows
    }

/** A flow of [Terminal.RenderEvent] instances. */
val Terminal.renderings: Flow<Terminal.RenderEvent>
    get() {
        val logger = ConsoleLogger("hello.ws-ssh.render")
        return callbackFlow {
            val listener = onRender { event: Terminal.RenderEvent ->
                trySend(event).onFailure { logger.error("Failed to send RenverEvent", event, it) }
            }
            awaitClose { listener.dispose() }
        }
    }

/** A flow of title changes. */
val Terminal.titleChanges: Flow<String>
    get() {
        val logger = ConsoleLogger("hello.ws-ssh.title")
        return callbackFlow {
            val listener = onTitleChange { title: String ->
                trySend(title).onFailure { logger.error("Failed to send title", title, it) }
            }
            awaitClose { listener.dispose() }
        }
    }

/**
 * A flow of data written to the terminal.
 * @see [Terminal.onData]
 */
val Terminal.data: Flow<String>
    get() {
        val logger = ConsoleLogger("hello.ws-ssh.data")
        return callbackFlow {
            val listener = onData { data: String ->
                trySend(data).onFailure { logger.error("Failed to send data", data, it) }
            }
            awaitClose { listener.dispose() }
        }
    }
