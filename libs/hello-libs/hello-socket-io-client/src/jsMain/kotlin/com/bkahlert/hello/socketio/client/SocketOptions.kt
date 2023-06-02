package com.bkahlert.hello.socketio.client

@JsModule("socket.io-client")
@JsNonModule
public external interface SocketOptions {
    /**
     * the authentication payload sent when connecting to the Namespace
     */
    public var auth: dynamic

    /**
     * The maximum Number of retries. Above the limit, the packet will be discarded.
     *
     * Using `Infinity` means the delivery guarantee is "at-least-once" (instead of "at-most-once" by default), but a
     * smaller value like 10 should be sufficient in practice.
     */
    public var retries: Number?

    /**
     * The default timeout in milliseconds used when waiting for an acknowledgement.
     */
    public var ackTimeout: Number?
}
