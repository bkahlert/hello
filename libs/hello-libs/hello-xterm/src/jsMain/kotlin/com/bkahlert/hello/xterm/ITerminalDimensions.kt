@file:JsModule("xterm-addon-fit")

package com.bkahlert.hello.xterm

/**
 * Represents the dimensions of a terminal.
 */
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
