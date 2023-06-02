package com.bkahlert.hello.xterm

/**
 * The set of localizable strings.
 */
@JsModule("xterm")
@JsNonModule
public external interface ILocalizableStrings {
    /**
     * The aria label for the underlying input textarea for the terminal.
     */
    public var promptLabel: String;

    /**
     * Announcement for when line reading is suppressed due to too many lines
     * being printed to the terminal when `screenReaderMode` is enabled.
     */
    public var tooMuchOutput: String;
}
