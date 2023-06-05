@file:JsModule("xterm")

package com.bkahlert.hello.xterm

/**
 * An addon that can provide additional functionality to the terminal.
 */
public external interface ITerminalAddon : IDisposable {
    /**
     * This is called when the addon is activated.
     */
    public fun activate(terminal: Terminal)
}
