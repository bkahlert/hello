@file:JsModule("xterm")

package com.bkahlert.hello.xterm

/**
 * Overview ruler decoration options
 */
public external interface IDecorationOverviewRulerOptions {
    public var color: String;

    /**
     * 'left' | 'center' | 'right' | 'full'
     */
    public var position: String?
}
