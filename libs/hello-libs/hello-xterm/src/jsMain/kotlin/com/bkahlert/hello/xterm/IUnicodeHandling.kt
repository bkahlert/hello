package com.bkahlert.hello.xterm

/**
 * (EXPERIMENTAL) Unicode handling interface.
 */
@JsModule("xterm")
@JsNonModule
public external interface IUnicodeHandling {
    /**
     * Register a custom Unicode version provider.
     */
    public fun register(provider: IUnicodeVersionProvider): Unit;

    /**
     * Registered Unicode versions.
     */
    public val versions: Array<String>;

    /**
     * Getter/setter for active Unicode version.
     */
    public val activeVersion: String;
}
