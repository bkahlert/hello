package com.bkahlert.hello.socketio.client

@JsModule("socket.io-client")
@JsNonModule
public abstract external class Emitter {

    /**
     * Adds the `listener` function as an event listener for `ev`.
     *
     * @param ev Name of the event
     * @param listener Callback function
     */
    public fun on(ev: String, listener: (args: dynamic) -> Unit): Emitter

    /**
     * Adds a one-time `listener` function as an event listener for `ev`.
     *
     * @param ev Name of the event
     * @param listener Callback function
     */
    public fun once(ev: String, listener: (args: dynamic) -> Unit): Emitter

    /**
     * Removes the `listener` function as an event listener for `ev`.
     *
     * @param ev Name of the event
     * @param listener Callback function
     */
    public fun off(ev: String, listener: (args: dynamic) -> Unit): Emitter

    /**
     * Emits an event.
     *
     * @param ev Name of the event
     * @param args Values to send to listeners of this event
     */
    public open fun emit(ev: String, vararg args: dynamic): Emitter

    /**
     * Returns the listeners listening to an event.
     *
     * @param event Event name
     * @returns Array of listeners subscribed to `event`
     */
    public fun listeners(event: String): Array<(args: dynamic) -> Unit>

    /**
     * Returns true if there is a listener for this event.
     *
     * @param event Event name
     * @returns boolean
     */
    public fun hasListeners(event: String? = definedExternally): Boolean

    /**
     * Removes the `listener` function as an event listener for `ev`.
     *
     * @param ev Name of the event
     * @param listener Callback function
     */
    public fun removeListener(ev: String? = definedExternally, listener: ((args: dynamic) -> Unit)? = definedExternally): Emitter

    /**
     * Removes all `listener` function as an event listener for `ev`.
     *
     * @param ev Name of the event
     */
    public fun removeAllListeners(ev: String? = definedExternally): Emitter
}
