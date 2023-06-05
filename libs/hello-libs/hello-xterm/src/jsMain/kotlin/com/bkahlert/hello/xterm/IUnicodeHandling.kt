@file:JsModule("xterm")

package com.bkahlert.hello.xterm

/**
 * (EXPERIMENTAL) Unicode handling interface.
 */
public external interface IUnicodeHandling {
    /**
     * Register a custom Unicode version provider.
     */
    public fun register(provider: IUnicodeVersionProvider)

    /**
     * Registered Unicode versions.
     */
    public val versions: Array<String>

    /**
     * Getter/setter for active Unicode version.
     */
    public val activeVersion: String
}
