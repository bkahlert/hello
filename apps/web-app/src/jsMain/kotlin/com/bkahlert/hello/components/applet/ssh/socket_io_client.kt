@file:JsModule("socket.io-client")
@file:JsNonModule

package com.bkahlert.hello.components.applet.ssh

external fun io(opts: ManagerOptions? = definedExternally): Socket;
external fun io(uri: String, opts: ManagerOptions? = definedExternally): Socket;

external interface SocketOptions {
    /**
     * the authentication payload sent when connecting to the Namespace
     */
    var auth: dynamic

    /**
     * The maximum Number of retries. Above the limit, the packet will be discarded.
     *
     * Using `Infinity` means the delivery guarantee is "at-least-once" (instead of "at-most-once" by default), but a
     * smaller value like 10 should be sufficient in practice.
     */
    var retries: Number?

    /**
     * The default timeout in milliseconds used when waiting for an acknowledgement.
     */
    var ackTimeout: Number?
}

external interface ManagerOptions : SocketOptions {
    /**
     * Should we force a new Manager for this connection?
     * @default false
     */
    var forceNew: Boolean;

    /**
     * Should we multiplex our connection (reuse existing Manager) ?
     * @default true
     */
    var multiplex: Boolean;

    /**
     * The path to get our client file from, in the case of the server
     * serving it
     * @default '/socket.io'
     */
    var path: String;

    /**
     * Should we allow reconnections?
     * @default true
     */
    var reconnection: Boolean;

    /**
     * How many reconnection attempts should we try?
     * @default Infinity
     */
    var reconnectionAttempts: Number;

    /**
     * The time delay in milliseconds between reconnection attempts
     * @default 1000
     */
    var reconnectionDelay: Number;

    /**
     * The max time delay in milliseconds between reconnection attempts
     * @default 5000
     */
    var reconnectionDelayMax: Number;

    /**
     * Used in the exponential backoff jitter when reconnecting
     * @default 0.5
     */
    var randomizationFactor: Number;

    /**
     * The timeout in milliseconds for our connection attempt
     * @default 20000
     */
    var timeout: Number;

    /**
     * Should we automatically connect?
     * @default true
     */
    var autoConnect: Boolean;

    /**
     * the parser to use. Defaults to an instance of the Parser that ships with socket.io.
     */
    var parser: Any;
}

/**
 * A Socket is the fundamental class for interacting with the server.
 *
 * A Socket belongs to a certain Namespace (by default /) and uses an underlying {@link Manager} to communicate.
 *
 * @example
 * const socket = io();
 *
 * socket.on("connect", () => {
 *   console.log("connected");
 * });
 *
 * // send an event to the server
 * socket.emit("foo", "bar");
 *
 * socket.on("foobar", () => {
 *   // an event was received from the server
 * });
 *
 * // upon disconnection
 * socket.on("disconnect", (reason) => {
 *   console.log(`disconnected due to ${reason}`);
 * });
 */
external class Socket : Emitter {

    /**
     * A unique identifier for the session.
     *
     * @example
     * const socket = io();
     *
     * console.log(socket.id); // undefined
     *
     * socket.on("connect", () => {
     *   console.log(socket.id); // "G5p5..."
     * });
     */
    val id: String

    /**
     * A reference to the underlying [Manager].
     */
    val io: Manager

    /**
     * Whether the socket is currently connected to the server.
     *
     * @example
     * const socket = io();
     *
     * socket.on("connect", () => {
     *   console.log(socket.connected); // true
     * });
     *
     * socket.on("disconnect", () => {
     *   console.log(socket.connected); // false
     * });
     */
    val connected: Boolean

    /**
     * Whether the connection state was recovered after a temporary disconnection. In that case, any missed packets will
     * be transmitted by the server.
     */
    val recovered: Boolean

    /**
     * Credentials that are sent when accessing a namespace.
     *
     * @example
     * const socket = io({
     *   auth: {
     *     token: "abcd"
     *   }
     * });
     *
     * // or with a function
     * const socket = io({
     *   auth: (cb) => {
     *     cb({ token: localStorage.token })
     *   }
     * });
     */
    public fun auth(objectOrCallback: dynamic);

    /**
     * Whether the socket is currently disconnected
     *
     * @example
     * const socket = io();
     *
     * socket.on("connect", () => {
     *   console.log(socket.disconnected); // false
     * });
     *
     * socket.on("disconnect", () => {
     *   console.log(socket.disconnected); // true
     * });
     */
    public val disconnected: Boolean

    /**
     * Whether the Socket will try to reconnect when its Manager connects or reconnects.
     *
     * @example
     * const socket = io();
     *
     * console.log(socket.active); // true
     *
     * socket.on("disconnect", (reason) => {
     *   if (reason === "io server disconnect") {
     *     // the disconnection was initiated by the server, you need to manually reconnect
     *     console.log(socket.active); // false
     *   }
     *   // else the socket will automatically try to reconnect
     *   console.log(socket.active); // true
     * });
     */
    val active: Boolean

    /**
     * "Opens" the socket.
     *
     * @example
     * const socket = io({
     *   autoConnect: false
     * });
     *
     * socket.connect();
     */
    fun connect(): Socket

    /**
     * Alias for {@link connect()}.
     */
    fun open(): Socket

    /**
     * Sends a `message` event.
     *
     * This method mimics the WebSocket.send() method.
     *
     * @see https://developer.mozilla.org/en-US/docs/Web/API/WebSocket/send
     *
     * @example
     * socket.send("hello");
     *
     * // this is equivalent to
     * socket.emit("message", "hello");
     *
     * @return self
     */
    fun send(vararg args: Any): Socket

    /**
     * Override `emit`.
     * If the event is in `events`, it's emitted normally.
     *
     * @example
     * socket.emit("hello", "world");
     *
     * // all serializable datastructures are supported (no need to call JSON.stringify)
     * socket.emit("hello", 1, "2", { 3: ["4"], 5: Uint8Array.from([6]) });
     *
     * // with an acknowledgement from the server
     * socket.emit("hello", "world", (val) => {
     *   // ...
     * });
     *
     * @return self
     */
    override fun emit(ev: String, vararg args: Any): Socket

    /**
     * Emits an event and waits for an acknowledgement
     *
     * @example
     * // without timeout
     * const response = await socket.emitWithAck("hello", "world");
     *
     * // with a specific timeout
     * try {
     *   const response = await socket.timeout(1000).emitWithAck("hello", "world");
     * } catch (err) {
     *   // the server did not acknowledge the event in the given delay
     * }
     *
     * @return a Promise that will be fulfilled when the server acknowledges the event
     */
    fun emitWithAck(ev: String, vararg args: Any)

    /**
     * Disconnects the socket manually. In that case, the socket will not try to reconnect.
     *
     * If this is the last active Socket instance of the {@link Manager}, the low-level connection will be closed.
     *
     * @example
     * const socket = io();
     *
     * socket.on("disconnect", (reason) => {
     *   // console.log(reason); prints "io client disconnect"
     * });
     *
     * socket.disconnect();
     *
     * @return self
     */
    fun disconnect(): Socket

    /**
     * Alias for {@link disconnect()}.
     *
     * @return self
     */
    fun close(): Socket

    /**
     * Sets the compress flag.
     *
     * @example
     * socket.compress(false).emit("hello");
     *
     * @param compress - if `true`, compresses the sending data
     * @return self
     */
    fun compress(compress: Boolean): Socket

    /**
     * Sets a modifier for a subsequent event emission that the event message will be dropped when this socket is not
     * ready to send messages.
     *
     * @example
     * socket.volatile.emit("hello"); // the server may or may not receive it
     *
     * @returns self
     */
    public val volatile: Socket

    /**
     * Sets a modifier for a subsequent event emission that the callback will be called with an error when the
     * given number of milliseconds have elapsed without an acknowledgement from the server:
     *
     * @example
     * socket.timeout(5000).emit("my-event", (err) => {
     *   if (err) {
     *     // the server did not acknowledge the event in the given delay
     *   }
     * });
     *
     * @returns self
     */
    public fun timeout(
        timeout: Number
    ): Socket

    /**
     * Adds a listener that will be fired when any event is emitted. The event name is passed as the first argument to the
     * callback.
     *
     * @example
     * socket.onAny((event, ...args) => {
     *   console.log(`got ${event}`);
     * });
     *
     * @param listener
     */
    fun onAny(listener: (dynamic) -> Unit): Socket

    /**
     * Adds a listener that will be fired when any event is emitted. The event name is passed as the first argument to the
     * callback. The listener is added to the beginning of the listeners array.
     *
     * @example
     * socket.prependAny((event, ...args) => {
     *   console.log(`got event ${event}`);
     * });
     *
     * @param listener
     */
    fun prependAny(listener: (dynamic) -> Unit): Socket

    /**
     * Removes the listener that will be fired when any event is emitted.
     *
     * @example
     * const catchAllListener = (event, ...args) => {
     *   console.log(`got event ${event}`);
     * }
     *
     * socket.onAny(catchAllListener);
     *
     * // remove a specific listener
     * socket.offAny(catchAllListener);
     *
     * // or remove all listeners
     * socket.offAny();
     *
     * @param listener
     */
    fun offAny(listener: (dynamic) -> Unit): Socket

    /**
     * Adds a listener that will be fired when any event is emitted. The event name is passed as the first argument to the
     * callback.
     *
     * Note: acknowledgements sent to the server are not included.
     *
     * @example
     * socket.onAnyOutgoing((event, ...args) => {
     *   console.log(`sent event ${event}`);
     * });
     *
     * @param listener
     */
    fun onAnyOutgoing(listener: (dynamic) -> Unit): Socket

    /**
     * Adds a listener that will be fired when any event is emitted. The event name is passed as the first argument to the
     * callback. The listener is added to the beginning of the listeners array.
     *
     * Note: acknowledgements sent to the server are not included.
     *
     * @example
     * socket.prependAnyOutgoing((event, ...args) => {
     *   console.log(`sent event ${event}`);
     * });
     *
     * @param listener
     */
    fun prependAnyOutgoing(listener: (dynamic) -> Unit): Socket

    /**
     * Removes the listener that will be fired when any event is emitted.
     *
     * @example
     * const catchAllListener = (event, ...args) => {
     *   console.log(`sent event ${event}`);
     * }
     *
     * socket.onAnyOutgoing(catchAllListener);
     *
     * // remove a specific listener
     * socket.offAnyOutgoing(catchAllListener);
     *
     * // or remove all listeners
     * socket.offAnyOutgoing();
     *
     * @param [listener] - the catch-all listener (optional)
     */
    fun offAnyOutgoing(listener: (dynamic) -> Unit): Socket
}


external abstract class Emitter {

    /**
     * Adds the `listener` function as an event listener for `ev`.
     *
     * @param ev Name of the event
     * @param listener Callback function
     */
    fun on(ev: String, listener: (args: dynamic) -> Unit): Emitter

    /**
     * Adds a one-time `listener` function as an event listener for `ev`.
     *
     * @param ev Name of the event
     * @param listener Callback function
     */
    fun once(ev: String, listener: (args: dynamic) -> Unit): Emitter

    /**
     * Removes the `listener` function as an event listener for `ev`.
     *
     * @param ev Name of the event
     * @param listener Callback function
     */
    fun off(ev: String, listener: (args: dynamic) -> Unit): Emitter

    /**
     * Emits an event.
     *
     * @param ev Name of the event
     * @param args Values to send to listeners of this event
     */
    open fun emit(ev: String, vararg args: dynamic): Emitter

    /**
     * Returns the listeners listening to an event.
     *
     * @param event Event name
     * @returns Array of listeners subscribed to `event`
     */
    fun listeners(event: String): Array<(args: dynamic) -> Unit>

    /**
     * Returns true if there is a listener for this event.
     *
     * @param event Event name
     * @returns boolean
     */
    fun hasListeners(event: String? = definedExternally): Boolean

    /**
     * Removes the `listener` function as an event listener for `ev`.
     *
     * @param ev Name of the event
     * @param listener Callback function
     */
    fun removeListener(ev: String? = definedExternally, listener: ((args: dynamic) -> Unit)? = definedExternally): Emitter

    /**
     * Removes all `listener` function as an event listener for `ev`.
     *
     * @param ev Name of the event
     */
    fun removeAllListeners(ev: String? = definedExternally): Emitter
}

external interface Manager {
    /** Returns the `reconnection` option. */
    fun reconnection(): Boolean

    /** Sets the `reconnection` option. */
    fun reconnection(value: Boolean): Manager
}
