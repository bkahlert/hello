package com.bkahlert.hello.socketio.client

@JsModule("socket.io-client")
@JsNonModule
public external interface ManagerOptions : SocketOptions {
    /**
     * Should we force a new Manager for this connection?
     * @default false
     */
    public var forceNew: Boolean;

    /**
     * Should we multiplex our connection (reuse existing Manager) ?
     * @default true
     */
    public var multiplex: Boolean;

    /**
     * The path to get our client file from, in the case of the server
     * serving it
     * @default '/socket.io'
     */
    public var path: String;

    /**
     * Should we allow reconnections?
     * @default true
     */
    public var reconnection: Boolean;

    /**
     * How many reconnection attempts should we try?
     * @default Infinity
     */
    public var reconnectionAttempts: Number;

    /**
     * The time delay in milliseconds between reconnection attempts
     * @default 1000
     */
    public var reconnectionDelay: Number;

    /**
     * The max time delay in milliseconds between reconnection attempts
     * @default 5000
     */
    public var reconnectionDelayMax: Number;

    /**
     * Used in the exponential backoff jitter when reconnecting
     * @default 0.5
     */
    public var randomizationFactor: Number;

    /**
     * The timeout in milliseconds for our connection attempt
     * @default 20000
     */
    public var timeout: Number;

    /**
     * Should we automatically connect?
     * @default true
     */
    public var autoConnect: Boolean;

    /**
     * the parser to use. Defaults to an instance of the Parser that ships with socket.io.
     */
    public var parser: Any;
}
