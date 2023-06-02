package com.bkahlert.hello.xterm

/**
 * An object containing additional options for the terminal that can only be
 * set on start up.
 */
@JsModule("xterm")
@JsNonModule
public external interface ITerminalInitOnlyOptions {
    /**
     * The number of columns in the terminal.
     */
    public var cols: Number?;

    /**
     * The number of rows in the terminal.
     */
    public var rows: Number?;
}
