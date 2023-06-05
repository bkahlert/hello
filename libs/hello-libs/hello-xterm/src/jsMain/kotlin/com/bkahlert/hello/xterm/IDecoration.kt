@file:JsModule("xterm")

package com.bkahlert.hello.xterm

import org.w3c.dom.HTMLElement

/**
 * Represents a decoration in the terminal that is associated with a particular marker and DOM element.
 */
public external interface IDecoration : IDisposableWithEvent {
    /*
     * The marker for the decoration in the terminal.
     */
    public val marker: IMarker;

    /**
     * An event fired when the decoration
     * is rendered, returns the dom element
     * associated with the decoration.
     */
    public val onRender: (listener: (HTMLElement) -> Unit) -> IDisposable

    /**
     * The element that the decoration is rendered to. This will be undefined
     * until it is rendered for the first time by {@link IDecoration.onRender}.
     * that.
     */
    public var element: HTMLElement?;

    /**
     * The options for the overview ruler that can be updated.
     * This will only take effect when {@link IDecorationOptions.overviewRulerOptions}
     * were provided initially.
     */
//public    val options: Pick<IDecorationOptions, 'overviewRulerOptions'>;
}
