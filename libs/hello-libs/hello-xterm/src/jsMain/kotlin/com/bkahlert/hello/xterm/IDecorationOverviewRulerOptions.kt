package com.bkahlert.hello.xterm

/**
 * Overview ruler decoration options
 */
@JsModule("xterm")
@JsNonModule
public external interface IDecorationOverviewRulerOptions {
    public var color: String;

    /**
     * 'left' | 'center' | 'right' | 'full'
     */
    public var position: String?
}
