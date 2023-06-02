package com.bkahlert.hello.xterm

/**
 * An object containing options for the terminal.
 */
@JsModule("xterm")
@JsNonModule
public external interface ITerminalOptions {
    /**
     * Whether to allow the use of proposed API. When false, any usage of APIs
     * marked as experimental/proposed will throw an error. The default is false.
     */
    public var allowProposedApi: Boolean?;

    /**
     * Whether background should support non-opaque color. It must be set before
     * executing the `Terminal.open()` method and can't be changed later without
     * executing it again. Note that enabling this can negatively impact
     * performance.
     */
    public var allowTransparency: Boolean?;

    /**
     * If enabled, alt + click will move the prompt cursor to position
     * underneath the mouse. The default is true.
     */
    public var altClickMovesCursor: Boolean?;

    /**
     * When enabled the cursor will be set to the beginning of the next line
     * with every new line. This is equivalent to sending '\r\n' for each '\n'.
     * Normally the termios settings of the underlying PTY deals with the
     * translation of '\n' to '\r\n' and this setting should not be used. If you
     * deal with data from a non-PTY related source, this settings might be
     * useful.
     */
    public var convertEol: Boolean?;

    /**
     * Whether the cursor blinks.
     */
    public var cursorBlink: Boolean?;

    /**
     * The style of the cursor.
     */
    public var cursorStyle: String?;

    /**
     * The width of the cursor in CSS pixels when `cursorStyle` is set to 'bar'.
     */
    public var cursorWidth: Number?;

    /**
     * Whether to draw custom glyphs for block element and box drawing characters instead of using
     * the font. This should typically result in better rendering with continuous lines, even when
     * line height and letter spacing is used. Note that this doesn't work with the DOM renderer
     * which renders all characters using the font. The default is true.
     */
    public var customGlyphs: Boolean?;

    /**
     * Whether input should be disabled.
     */
    public var disableStdin: Boolean?;

    /**
     * Whether to draw bold text in bright colors. The default is true.
     */
    public var drawBoldTextInBrightColors: Boolean?;

    /**
     * The modifier key hold to multiply scroll speed.
     */
    public var fastScrollModifier: String?

    /**
     * The scroll speed multiplier used for fast scrolling.
     */
    public var fastScrollSensitivity: Number?;

    /**
     * The font size used to render text.
     */
    public var fontSize: Number?;

    /**
     * The font family used to render text.
     */
    public var fontFamily: String?;

    /**
     * The font weight used to render non-bold text.
     */
    public var fontWeight: String?;

    /**
     * The font weight used to render bold text.
     */
    public var fontWeightBold: String?;

    /**
     * The spacing in whole pixels between characters.
     */
    public var letterSpacing: Number?;

    /**
     * The line height used to render text.
     */
    public var lineHeight: Number?;

    /**
     * The handler for OSC 8 hyperlinks. Links will use the `confirm` browser
     * API with a strongly worded warning if no link handler is set.
     *
     * When setting this, consider the security of users opening these links,
     * at a minimum there should be a tooltip or a prompt when hovering or
     * activating the link respectively. An example of what might be possible is
     * a terminal app writing link in the form `javascript:...` that runs some
     * javascript, a safe approach to prevent that is to validate the link
     * starts with http(s)://.
     */
    public var linkHandler: dynamic;

    /**
     * What log level to use, this will log for all levels below and including
     * what is set:
     *
     * 1. debug
     * 2. info (default)
     * 3. warn
     * 4. error
     * 5. off
     */
    public var logLevel: String?;

    /**
     * Whether to treat option as the meta key.
     */
    public var macOptionIsMeta: Boolean?;

    /**
     * Whether holding a modifier key will force normal selection behavior,
     * regardless of whether the terminal is in mouse events mode. This will
     * also prevent mouse events from being emitted by the terminal. For
     * example, this allows you to use xterm.js' regular selection inside tmux
     * with mouse mode enabled.
     */
    public var macOptionClickForcesSelection: Boolean?;

    /**
     * The minimum contrast ratio for text in the terminal, setting this will
     * change the foreground color dynamically depending on whether the contrast
     * ratio is met. Example values:
     *
     * - 1: The default, do nothing.
     * - 4.5: Minimum for WCAG AA compliance.
     * - 7: Minimum for WCAG AAA compliance.
     * - 21: White on black or black on white.
     */
    public var minimumContrastRatio: Number?;

    /**
     * Whether to select the word under the cursor on right click, this is
     * standard behavior in a lot of macOS applications.
     */
    public var rightClickSelectsWord: Boolean?;

    /**
     * Whether screen reader support is enabled. When on this will expose
     * supporting elements in the DOM to support NVDA on Windows and VoiceOver
     * on macOS.
     */
    public var screenReaderMode: Boolean?;

    /**
     * The amount of scrollback in the terminal. Scrollback is the amount of
     * rows that are retained when lines are scrolled beyond the initial
     * viewport.
     */
    public var scrollback: Number?;

    /**
     * Whether to scroll to the bottom whenever there is some user input. The
     * default is true.
     */
    public var scrollOnUserInput: Boolean?;

    /**
     * The scrolling speed multiplier used for adjusting normal scrolling speed.
     */
    public var scrollSensitivity: Number?;

    /**
     * The duration to smoothly scroll between the origin and the target in
     * milliseconds. Set to 0 to disable smooth scrolling and scroll instantly.
     */
    public var smoothScrollDuration: Number?;

    /**
     * The size of tab stops in the terminal.
     */
    public var tabStopWidth: Number?;

    /**
     * The color theme of the terminal.
     */
    public var theme: ITheme?;

    /**
     * Whether "Windows mode" is enabled. Because Windows backends winpty and
     * conpty operate by doing line wrapping on their side, xterm.js does not
     * have access to wrapped lines. When Windows mode is enabled the following
     * changes will be in effect:
     *
     * - Reflow is disabled.
     * - Lines are assumed to be wrapped if the last character of the line is
     *   not whitespace.
     *
     * When using conpty on Windows 11 version >= 21376, it is recommended to
     * disable this because native text wrapping sequences are output correctly
     * thanks to https://github.com/microsoft/terminal/issues/405
     */
    public var windowsMode: Boolean?;

    /**
     * A string containing all characters that are considered word separated by the
     * double click to select work logic.
     */
    public var wordSeparator: String?;

    /**
     * Enable various window manipulation and report features.
     * All features are disabled by default for security reasons.
     */
    public var windowOptions: IWindowOptions?;

    /**
     * The width, in pixels, of the canvas for the overview ruler. The overview
     * ruler will be hidden when not set.
     */
    public var overviewRulerWidth: Number?;
}
