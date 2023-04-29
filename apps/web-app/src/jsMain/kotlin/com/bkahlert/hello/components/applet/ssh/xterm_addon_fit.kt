@file:JsModule("xterm-addon-fit")

package com.bkahlert.hello.components.applet.ssh

/**
 * An xterm.js addon that enables resizing the terminal to the dimensions of
 * its containing element.
 */
external class FitAddon : ITerminalAddon {

    /**
     * Activates the addon
     * @param terminal The terminal the addon is being loaded in.
     */
    override fun activate(terminal: Terminal)

    /**
     * Disposes the addon.
     */
    override fun dispose()

    /**
     * Resizes the terminal to the dimensions of its containing element.
     */
    fun fit()

    /**
     * Gets the proposed dimensions that will be used for a fit.
     */
    fun proposeDimensions(): ITerminalDimensions?
}

/**
 * Reprepresents the dimensions of a terminal.
 */
external interface ITerminalDimensions {
    /**
     * The number of rows in the terminal.
     */
    var rows: Number;

    /**
     * The number of columns in the terminal.
     */
    var cols: Number;
}
