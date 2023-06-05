@file:JsModule("xterm")

package com.bkahlert.hello.xterm

/**
 * Terminal modes as set by SM/DECSET.
 */
public external interface IModes {
    /**
     * Application Cursor Keys (DECCKM): `CSI ? 1 h`
     */
    public val applicationCursorKeysMode: Boolean;

    /**
     * Application Keypad Mode (DECNKM): `CSI ? 6 6 h`
     */
    public val applicationKeypadMode: Boolean;

    /**
     * Bracketed Paste Mode: `CSI ? 2 0 0 4 h`
     */
    public val bracketedPasteMode: Boolean;

    /**
     * Insert Mode (IRM): `CSI 4 h`
     */
    public val insertMode: Boolean;

    /**
     * Mouse Tracking, this can be one of the following:
     * - none: This is the default value and can be reset with DECRST
     * - x10: Send Mouse X & Y on button press `CSI ? 9 h`
     * - vt200: Send Mouse X & Y on button press and release `CSI ? 1 0 0 0 h`
     * - drag: Use Cell Motion Mouse Tracking `CSI ? 1 0 0 2 h`
     * - any: Use All Motion Mouse Tracking `CSI ? 1 0 0 3 h`
     */
    public val mouseTrackingMode: dynamic;

    /**
     * Origin Mode (DECOM): `CSI ? 6 h`
     */
    public val originMode: Boolean;

    /**
     * Reverse-wraparound Mode: `CSI ? 4 5 h`
     */
    public val reverseWraparoundMode: Boolean;

    /**
     * Send FocusIn/FocusOut events: `CSI ? 1 0 0 4 h`
     */
    public val sendFocusMode: Boolean;

    /**
     * Auto-Wrap Mode (DECAWM): `CSI ? 7 h`
     */
    public val wraparoundMode: Boolean
}
