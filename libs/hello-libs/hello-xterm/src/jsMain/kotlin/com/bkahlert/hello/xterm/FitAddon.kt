package com.bkahlert.hello.xterm

/**
 * An xterm.js addon that enables resizing the terminal to the dimensions of
 * its containing element.
 */
@JsModule("xterm-addon-fit")
@JsNonModule
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
