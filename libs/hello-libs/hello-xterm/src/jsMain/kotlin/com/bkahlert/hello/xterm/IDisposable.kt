package com.bkahlert.hello.xterm

/**
 * An object that can be disposed via a dispose function.
 */
@JsModule("xterm")
@JsNonModule
public external interface IDisposable {
    public fun dispose()
}
