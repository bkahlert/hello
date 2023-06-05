@file:JsModule("xterm")

package com.bkahlert.hello.xterm

/**
 * Represents a disposable that tracks is disposed state.
 * @param onDispose event listener and
 * @param isDisposed property.
 */
public external interface IDisposableWithEvent : IDisposable {
    /**
     * Event listener to get notified when this gets disposed.
     */
    public val onDispose: (listener: () -> Unit) -> IDisposable

    /**
     * Whether this is disposed.
     */
    public val isDisposed: Boolean;
}
