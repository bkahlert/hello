package com.bkahlert.hello.socketio.client

@JsModule("socket.io-client")
@JsNonModule
public external interface Manager {
    /** Returns the `reconnection` option. */
    public fun reconnection(): Boolean

    /** Sets the `reconnection` option. */
    public fun reconnection(value: Boolean): Manager
}
