package com.bkahlert.hello.xterm

/**
 * (EXPERIMENTAL) Unicode version provider.
 * Used to register custom Unicode versions with `Terminal.unicode.register`.
 */
@JsModule("xterm")
@JsNonModule
public external interface IUnicodeVersionProvider {
    /**
     * String indicating the Unicode version provided.
     */
    public val version: String;

    /**
     * Unicode version dependent wcwidth implementation.
     */
    public fun wcwidth(codepoint: Number): Int;
}
