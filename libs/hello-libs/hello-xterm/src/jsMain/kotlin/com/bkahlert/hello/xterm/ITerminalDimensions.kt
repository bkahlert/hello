package com.bkahlert.hello.xterm

/**
 * Reprepresents the dimensions of a terminal.
 */
@JsModule("xterm-addon-fit")
@JsNonModule
public external interface ITerminalDimensions {
    /**
     * The number of rows in the terminal.
     */
    public var rows: Number;

    /**
     * The number of columns in the terminal.
     */
    public var cols: Number;
}
