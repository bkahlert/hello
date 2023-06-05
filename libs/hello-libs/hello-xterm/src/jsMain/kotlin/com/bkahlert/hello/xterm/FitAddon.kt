@file:JsModule("xterm-addon-fit")

package com.bkahlert.hello.xterm

/**
 * A xterm.js addon that enables resizing the terminal to the dimensions of
 * its containing element.
 */
public external class FitAddon : ITerminalAddon {

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
    public fun fit()

    /**
     * Gets the proposed dimensions that will be used for a fit.
     */
    public fun proposeDimensions(): ITerminalDimensions?
}
