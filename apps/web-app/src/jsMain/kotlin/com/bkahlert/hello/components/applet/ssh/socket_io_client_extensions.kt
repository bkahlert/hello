package com.bkahlert.hello.components.applet.ssh

import com.bkahlert.kommons.js.ConsoleLogger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** A flow [event] named and [T] typed events. */
fun <T : Any> Socket.on(event: String): Flow<T> {
    val logger = ConsoleLogger("hello.ws-ssh.$event")
    return callbackFlow {
        on(event) { data ->
            trySend(data).onFailure { logger.error("Failed to send data", data, event, it) }
        }

        awaitClose { removeListener(event) }
    }
}

/** A flow of [connect](https://socket.io/docs/v4/client-api/#event-connect) events. */
fun Socket.onConnect(): Flow<Unit> = on("connect")

/** A flow of [disconnect](https://socket.io/docs/v4/client-api/#event-connect) events. */
fun Socket.onDisconnect(): Flow<String> = on("disconnect")
