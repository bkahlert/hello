@file:JsModule("xterm")

package com.bkahlert.hello.xterm

/**
 * Options that define the presentation of the decoration.
 */
public external interface IDecorationOptions {
    /**
     * The line in the terminal where
     * the decoration will be displayed
     */
    public val marker: IMarker;

    /**
     * Where the decoration will be anchored -
     * defaults to the left edge
     *
     * 'right' | 'left'
     */
    public var anchor: String?

    /**
     * The x position offset relative to the anchor
     */
    public val x: Number?;

    /**
     * The width of the decoration in cells, defaults to 1.
     */
    public val width: Number?;

    /**
     * The height of the decoration in cells, defaults to 1.
     */
    public val height: Number?;

    /**
     * The background color of the cell(s). When 2 decorations both set the foreground color the
     * last registered decoration will be used. Only the `#RRGGBB` format is supported.
     */
    public val backgroundColor: String?;

    /**
     * The foreground color of the cell(s). When 2 decorations both set the foreground color the
     * last registered decoration will be used. Only the `#RRGGBB` format is supported.
     */
    public val foregroundColor: String?;

    /**
     * What layer to render the decoration at when {@link backgroundColor} or
     * {@link foregroundColor} are used. `'bottom'` will render under the selection, `'top`' will
     * render above the selection\*.
     *
     * *\* The selection will render on top regardless of layer on the canvas renderer due to how
     * it renders selection separately.*
     *
     * 'bottom' | 'top'
     */
    public val layer: String?;

    /**
     * When defined, renders the decoration in the overview ruler to the right
     * of the terminal. {@link ITerminalOptions.overviewRulerWidth} must be set
     * in order to see the overview ruler.
     * @param color The color of the decoration.
     * @param position The position of the decoration.
     */
    public var overviewRulerOptions: IDecorationOverviewRulerOptions?
}
