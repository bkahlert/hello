package com.bkahlert.hello.xterm

/**
 * Contains colors to theme the terminal with.
 */
@JsModule("xterm")
@JsNonModule
public external interface ITheme {
    /** The default foreground color */
    public var foreground: String?;

    /** The default background color */
    public var background: String?;

    /** The cursor color */
    public var cursor: String?;

    /** The accent color of the cursor (fg color for a block cursor) */
    public var cursorAccent: String?;

    /** The selection background color (can be transparent) */
    public var selectionBackground: String?;

    /** The selection foreground color */
    public var selectionForeground: String?;

    /** The selection background color when the terminal does not have focus (can be transparent) */
    public var selectionInactiveBackground: String?;

    /** ANSI black (eg. `\x1b[30m`) */
    public var black: String?;

    /** ANSI red (eg. `\x1b[31m`) */
    public var red: String?;

    /** ANSI green (eg. `\x1b[32m`) */
    public var green: String?;

    /** ANSI yellow (eg. `\x1b[33m`) */
    public var yellow: String?;

    /** ANSI blue (eg. `\x1b[34m`) */
    public var blue: String?;

    /** ANSI magenta (eg. `\x1b[35m`) */
    public var magenta: String?;

    /** ANSI cyan (eg. `\x1b[36m`) */
    public var cyan: String?;

    /** ANSI white (eg. `\x1b[37m`) */
    public var white: String?;

    /** ANSI bright black (eg. `\x1b[1;30m`) */
    public var brightBlack: String?;

    /** ANSI bright red (eg. `\x1b[1;31m`) */
    public var brightRed: String?;

    /** ANSI bright green (eg. `\x1b[1;32m`) */
    public var brightGreen: String?;

    /** ANSI bright yellow (eg. `\x1b[1;33m`) */
    public var brightYellow: String?;

    /** ANSI bright blue (eg. `\x1b[1;34m`) */
    public var brightBlue: String?;

    /** ANSI bright magenta (eg. `\x1b[1;35m`) */
    public var brightMagenta: String?;

    /** ANSI bright cyan (eg. `\x1b[1;36m`) */
    public var brightCyan: String?;

    /** ANSI bright white (eg. `\x1b[1;37m`) */
    public var brightWhite: String?;

    /** ANSI extended colors (16-255) */
    public var extendedAnsi: Array<String>?;
}
