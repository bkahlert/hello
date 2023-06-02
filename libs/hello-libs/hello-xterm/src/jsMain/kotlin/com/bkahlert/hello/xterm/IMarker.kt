package com.bkahlert.hello.xterm

/**
 * Represents a specific line in the terminal that is tracked when scrollback
 * is trimmed and lines are added or removed. This is a single line that may
 * be part of a larger wrapped line.
 */
@JsModule("xterm")
@JsNonModule
public external interface IMarker : IDisposableWithEvent {
    /**
     * A unique identifier for this marker.
     */
    public val id: Number;

    /**
     * The actual line index in the buffer at this point in time. This is set to `-1` if the marker has been disposed.
     */
    public val line: Number;
}
