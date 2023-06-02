package com.bkahlert.hello.socketio.client

@JsModule("socket.io-client")
@JsNonModule
public external fun io(opts: ManagerOptions? = definedExternally): Socket

@JsModule("socket.io-client")
@JsNonModule
public external fun io(uri: String, opts: ManagerOptions? = definedExternally): Socket
