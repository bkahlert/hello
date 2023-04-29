@file:JsModule("xterm")
@file:JsNonModule

package com.bkahlert.hello.components.applet.ssh

import js.typedarrays.Uint8Array
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.KeyboardEvent

/**
 * An object containing options for the terminal.
 */
external interface ITerminalOptions {
    /**
     * Whether to allow the use of proposed API. When false, any usage of APIs
     * marked as experimental/proposed will throw an error. The default is false.
     */
    var allowProposedApi: Boolean?;

    /**
     * Whether background should support non-opaque color. It must be set before
     * executing the `Terminal.open()` method and can't be changed later without
     * executing it again. Note that enabling this can negatively impact
     * performance.
     */
    var allowTransparency: Boolean?;

    /**
     * If enabled, alt + click will move the prompt cursor to position
     * underneath the mouse. The default is true.
     */
    var altClickMovesCursor: Boolean?;

    /**
     * When enabled the cursor will be set to the beginning of the next line
     * with every new line. This is equivalent to sending '\r\n' for each '\n'.
     * Normally the termios settings of the underlying PTY deals with the
     * translation of '\n' to '\r\n' and this setting should not be used. If you
     * deal with data from a non-PTY related source, this settings might be
     * useful.
     */
    var convertEol: Boolean?;

    /**
     * Whether the cursor blinks.
     */
    var cursorBlink: Boolean?;

    /**
     * The style of the cursor.
     */
    var cursorStyle: String?;

    /**
     * The width of the cursor in CSS pixels when `cursorStyle` is set to 'bar'.
     */
    var cursorWidth: Number?;

    /**
     * Whether to draw custom glyphs for block element and box drawing characters instead of using
     * the font. This should typically result in better rendering with continuous lines, even when
     * line height and letter spacing is used. Note that this doesn't work with the DOM renderer
     * which renders all characters using the font. The default is true.
     */
    var customGlyphs: Boolean?;

    /**
     * Whether input should be disabled.
     */
    var disableStdin: Boolean?;

    /**
     * Whether to draw bold text in bright colors. The default is true.
     */
    var drawBoldTextInBrightColors: Boolean?;

    /**
     * The modifier key hold to multiply scroll speed.
     */
    var fastScrollModifier: String?

    /**
     * The scroll speed multiplier used for fast scrolling.
     */
    var fastScrollSensitivity: Number?;

    /**
     * The font size used to render text.
     */
    var fontSize: Number?;

    /**
     * The font family used to render text.
     */
    var fontFamily: String?;

    /**
     * The font weight used to render non-bold text.
     */
    var fontWeight: String?;

    /**
     * The font weight used to render bold text.
     */
    var fontWeightBold: String?;

    /**
     * The spacing in whole pixels between characters.
     */
    var letterSpacing: Number?;

    /**
     * The line height used to render text.
     */
    var lineHeight: Number?;

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
    var linkHandler: dynamic;

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
    var logLevel: String?;

    /**
     * Whether to treat option as the meta key.
     */
    var macOptionIsMeta: Boolean?;

    /**
     * Whether holding a modifier key will force normal selection behavior,
     * regardless of whether the terminal is in mouse events mode. This will
     * also prevent mouse events from being emitted by the terminal. For
     * example, this allows you to use xterm.js' regular selection inside tmux
     * with mouse mode enabled.
     */
    var macOptionClickForcesSelection: Boolean?;

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
    var minimumContrastRatio: Number?;

    /**
     * Whether to select the word under the cursor on right click, this is
     * standard behavior in a lot of macOS applications.
     */
    var rightClickSelectsWord: Boolean?;

    /**
     * Whether screen reader support is enabled. When on this will expose
     * supporting elements in the DOM to support NVDA on Windows and VoiceOver
     * on macOS.
     */
    var screenReaderMode: Boolean?;

    /**
     * The amount of scrollback in the terminal. Scrollback is the amount of
     * rows that are retained when lines are scrolled beyond the initial
     * viewport.
     */
    var scrollback: Number?;

    /**
     * Whether to scroll to the bottom whenever there is some user input. The
     * default is true.
     */
    var scrollOnUserInput: Boolean?;

    /**
     * The scrolling speed multiplier used for adjusting normal scrolling speed.
     */
    var scrollSensitivity: Number?;

    /**
     * The duration to smoothly scroll between the origin and the target in
     * milliseconds. Set to 0 to disable smooth scrolling and scroll instantly.
     */
    var smoothScrollDuration: Number?;

    /**
     * The size of tab stops in the terminal.
     */
    var tabStopWidth: Number?;

    /**
     * The color theme of the terminal.
     */
    var theme: ITheme?;

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
    var windowsMode: Boolean?;

    /**
     * A string containing all characters that are considered word separated by the
     * double click to select work logic.
     */
    var wordSeparator: String?;

    /**
     * Enable various window manipulation and report features.
     * All features are disabled by default for security reasons.
     */
    var windowOptions: IWindowOptions?;

    /**
     * The width, in pixels, of the canvas for the overview ruler. The overview
     * ruler will be hidden when not set.
     */
    var overviewRulerWidth: Number?;
}

/**
 * An object containing additional options for the terminal that can only be
 * set on start up.
 */
external interface ITerminalInitOnlyOptions {
    /**
     * The number of columns in the terminal.
     */
    var cols: Number?;

    /**
     * The number of rows in the terminal.
     */
    var rows: Number?;
}

/**
 * Contains colors to theme the terminal with.
 */
external interface ITheme {
    /** The default foreground color */
    var foreground: String?;

    /** The default background color */
    var background: String?;

    /** The cursor color */
    var cursor: String?;

    /** The accent color of the cursor (fg color for a block cursor) */
    var cursorAccent: String?;

    /** The selection background color (can be transparent) */
    var selectionBackground: String?;

    /** The selection foreground color */
    var selectionForeground: String?;

    /** The selection background color when the terminal does not have focus (can be transparent) */
    var selectionInactiveBackground: String?;

    /** ANSI black (eg. `\x1b[30m`) */
    var black: String?;

    /** ANSI red (eg. `\x1b[31m`) */
    var red: String?;

    /** ANSI green (eg. `\x1b[32m`) */
    var green: String?;

    /** ANSI yellow (eg. `\x1b[33m`) */
    var yellow: String?;

    /** ANSI blue (eg. `\x1b[34m`) */
    var blue: String?;

    /** ANSI magenta (eg. `\x1b[35m`) */
    var magenta: String?;

    /** ANSI cyan (eg. `\x1b[36m`) */
    var cyan: String?;

    /** ANSI white (eg. `\x1b[37m`) */
    var white: String?;

    /** ANSI bright black (eg. `\x1b[1;30m`) */
    var brightBlack: String?;

    /** ANSI bright red (eg. `\x1b[1;31m`) */
    var brightRed: String?;

    /** ANSI bright green (eg. `\x1b[1;32m`) */
    var brightGreen: String?;

    /** ANSI bright yellow (eg. `\x1b[1;33m`) */
    var brightYellow: String?;

    /** ANSI bright blue (eg. `\x1b[1;34m`) */
    var brightBlue: String?;

    /** ANSI bright magenta (eg. `\x1b[1;35m`) */
    var brightMagenta: String?;

    /** ANSI bright cyan (eg. `\x1b[1;36m`) */
    var brightCyan: String?;

    /** ANSI bright white (eg. `\x1b[1;37m`) */
    var brightWhite: String?;

    /** ANSI extended colors (16-255) */
    var extendedAnsi: Array<String>?;
}

/**
 * An object that can be disposed via a dispose function.
 */
external interface IDisposable {
    fun dispose()
}

/**
 * Represents a specific line in the terminal that is tracked when scrollback
 * is trimmed and lines are added or removed. This is a single line that may
 * be part of a larger wrapped line.
 */
external interface IMarker : IDisposableWithEvent {
    /**
     * A unique identifier for this marker.
     */
    val id: Number;

    /**
     * The actual line index in the buffer at this point in time. This is set to `-1` if the marker has been disposed.
     */
    val line: Number;
}

/**
 * Represents a disposable that tracks is disposed state.
 * @param onDispose event listener and
 * @param isDisposed property.
 */
external interface IDisposableWithEvent : IDisposable {
    /**
     * Event listener to get notified when this gets disposed.
     */
    val onDispose: (listener: () -> Unit) -> IDisposable

    /**
     * Whether this is disposed.
     */
    val isDisposed: Boolean;
}

/**
 * Represents a decoration in the terminal that is associated with a particular marker and DOM element.
 */
external interface IDecoration : IDisposableWithEvent {
    /*
     * The marker for the decoration in the terminal.
     */
    val marker: IMarker;

    /**
     * An event fired when the decoration
     * is rendered, returns the dom element
     * associated with the decoration.
     */
    val onRender: (listener: (HTMLElement) -> Unit) -> IDisposable

    /**
     * The element that the decoration is rendered to. This will be undefined
     * until it is rendered for the first time by {@link IDecoration.onRender}.
     * that.
     */
    var element: HTMLElement?;

    /**
     * The options for the overview ruler that can be updated.
     * This will only take effect when {@link IDecorationOptions.overviewRulerOptions}
     * were provided initially.
     */
//    val options: Pick<IDecorationOptions, 'overviewRulerOptions'>;
}


/**
 * Overview ruler decoration options
 */
external interface IDecorationOverviewRulerOptions {
    var color: String;

    /**
     * 'left' | 'center' | 'right' | 'full'
     */
    var position: String?
}

/*
 * Options that define the presentation of the decoration.
 */
external interface IDecorationOptions {
    /**
     * The line in the terminal where
     * the decoration will be displayed
     */
    val marker: IMarker;

    /**
     * Where the decoration will be anchored -
     * defaults to the left edge
     *
     * 'right' | 'left'
     */
    var anchor: String?

    /**
     * The x position offset relative to the anchor
     */
    val x: Number?;

    /**
     * The width of the decoration in cells, defaults to 1.
     */
    val width: Number?;

    /**
     * The height of the decoration in cells, defaults to 1.
     */
    val height: Number?;

    /**
     * The background color of the cell(s). When 2 decorations both set the foreground color the
     * last registered decoration will be used. Only the `#RRGGBB` format is supported.
     */
    val backgroundColor: String?;

    /**
     * The foreground color of the cell(s). When 2 decorations both set the foreground color the
     * last registered decoration will be used. Only the `#RRGGBB` format is supported.
     */
    val foregroundColor: String?;

    /**
     * What layer to render the decoration at when {@link backgroundColor} or
     * {@link foregroundColor} are used. `'bottom'` will render under the selection, `'top`' will
     * render above the selection\*.
     *
     * *\* The selection will render on top regardless of layer on the canvas renderer due to how
     * it renders selection separately.*
     *
     * 'bottom' | 'top'
     */
    val layer: String?;

    /**
     * When defined, renders the decoration in the overview ruler to the right
     * of the terminal. {@link ITerminalOptions.overviewRulerWidth} must be set
     * in order to see the overview ruler.
     * @param color The color of the decoration.
     * @param position The position of the decoration.
     */
    var overviewRulerOptions: IDecorationOverviewRulerOptions?
}

/**
 * The set of localizable strings.
 */
external interface ILocalizableStrings {
    /**
     * The aria label for the underlying input textarea for the terminal.
     */
    var promptLabel: String;

    /**
     * Announcement for when line reading is suppressed due to too many lines
     * being printed to the terminal when `screenReaderMode` is enabled.
     */
    var tooMuchOutput: String;
}

/**
 * Enable various window manipulation and report features (CSI Ps ; Ps ; Ps t).
 *
 * Most settings have no default implementation, as they heavily rely on
 * the embedding environment.
 *
 * To implement a feature, create a custom CSI hook like this:
 * ```ts
 * term.parser.addCsiHandler({final: 't'}, params => {
 *   const ps = params[0];
 *   switch (ps) {
 *     case XY:
 *       ...            // your implementation for option XY
 *       return true;   // signal Ps=XY was handled
 *   }
 *   return false;      // any Ps that was not handled
 * });
 * ```
 *
 * Note on security:
 * Most features are meant to deal with some information of the host machine
 * where the terminal runs on. This is seen as a security risk possibly leaking
 * sensitive data of the host to the program in the terminal. Therefore all options
 * (even those without a default implementation) are guarded by the boolean flag
 * and disabled by default.
 */
external interface IWindowOptions {
    /**
     * Ps=1    De-iconify window.
     * No default implementation.
     */
    var restoreWin: Boolean?;

    /**
     * Ps=2    Iconify window.
     * No default implementation.
     */
    var minimizeWin: Boolean?;

    /**
     * Ps=3 ; x ; y
     * Move window to [x, y].
     * No default implementation.
     */
    var setWinPosition: Boolean?;

    /**
     * Ps = 4 ; height ; width
     * Resize the window to given `height` and `width` in pixels.
     * Omitted parameters should reuse the current height or width.
     * Zero parameters should use the display's height or width.
     * No default implementation.
     */
    var setWinSizePixels: Boolean?;

    /**
     * Ps=5    Raise the window to the front of the stacking order.
     * No default implementation.
     */
    var raiseWin: Boolean?;

    /**
     * Ps=6    Lower the xterm window to the bottom of the stacking order.
     * No default implementation.
     */
    var lowerWin: Boolean?;

    /** Ps=7    Refresh the window. */
    var refreshWin: Boolean?;

    /**
     * Ps = 8 ; height ; width
     * Resize the text area to given height and width in characters.
     * Omitted parameters should reuse the current height or width.
     * Zero parameters use the display's height or width.
     * No default implementation.
     */
    var setWinSizeChars: Boolean?;

    /**
     * Ps=9 ; 0   Restore maximized window.
     * Ps=9 ; 1   Maximize window (i.e., resize to screen size).
     * Ps=9 ; 2   Maximize window vertically.
     * Ps=9 ; 3   Maximize window horizontally.
     * No default implementation.
     */
    var maximizeWin: Boolean?;

    /**
     * Ps=10 ; 0  Undo full-screen mode.
     * Ps=10 ; 1  Change to full-screen.
     * Ps=10 ; 2  Toggle full-screen.
     * No default implementation.
     */
    var fullscreenWin: Boolean?;

    /** Ps=11   Report xterm window state.
     * If the xterm window is non-iconified, it returns "CSI 1 t".
     * If the xterm window is iconified, it returns "CSI 2 t".
     * No default implementation.
     */
    var getWinState: Boolean?;

    /**
     * Ps=13      Report xterm window position. Result is "CSI 3 ; x ; y t".
     * Ps=13 ; 2  Report xterm text-area position. Result is "CSI 3 ; x ; y t".
     * No default implementation.
     */
    var getWinPosition: Boolean?;

    /**
     * Ps=14      Report xterm text area size in pixels. Result is "CSI 4 ; height ; width t".
     * Ps=14 ; 2  Report xterm window size in pixels. Result is "CSI  4 ; height ; width t".
     * Has a default implementation.
     */
    var getWinSizePixels: Boolean?;

    /**
     * Ps=15    Report size of the screen in pixels. Result is "CSI 5 ; height ; width t".
     * No default implementation.
     */
    var getScreenSizePixels: Boolean?;

    /**
     * Ps=16  Report xterm character cell size in pixels. Result is "CSI 6 ; height ; width t".
     * Has a default implementation.
     */
    var getCellSizePixels: Boolean?;

    /**
     * Ps=18  Report the size of the text area in characters. Result is "CSI 8 ; height ; width t".
     * Has a default implementation.
     */
    var getWinSizeChars: Boolean?;

    /**
     * Ps=19  Report the size of the screen in characters. Result is "CSI 9 ; height ; width t".
     * No default implementation.
     */
    var getScreenSizeChars: Boolean?;

    /**
     * Ps=20  Report xterm window's icon label. Result is "OSC L label ST".
     * No default implementation.
     */
    var getIconTitle: Boolean?;

    /**
     * Ps=21  Report xterm window's title. Result is "OSC l label ST".
     * No default implementation.
     */
    var getWinTitle: Boolean?;

    /**
     * Ps=22 ; 0  Save xterm icon and window title on stack.
     * Ps=22 ; 1  Save xterm icon title on stack.
     * Ps=22 ; 2  Save xterm window title on stack.
     * All variants have a default implementation.
     */
    var pushTitle: Boolean?;

    /**
     * Ps=23 ; 0  Restore xterm icon and window title from stack.
     * Ps=23 ; 1  Restore xterm icon title from stack.
     * Ps=23 ; 2  Restore xterm window title from stack.
     * All variants have a default implementation.
     */
    var popTitle: Boolean?;

    /**
     * Ps>=24  Resize to Ps lines (DECSLPP).
     * DECSLPP is not implemented. This settings is also used to
     * enable / disable DECCOLM (earlier variant of DECSLPP).
     */
    var setWinLines: Boolean?;
}

/**
 * The class that represents an xterm.js terminal.
 */
external class Terminal : IDisposable {
    /**
     * The element containing the terminal.
     */
    val element: HTMLElement?

    /**
     * The textarea that accepts input for the terminal.
     */
    val textarea: HTMLTextAreaElement?

    /**
     * The number of rows in the terminal's viewport. Use
     * `ITerminalOptions.rows` to set this in the constructor and
     * `Terminal.resize` for when the terminal exists.
     */
    val rows: Number;

    /**
     * The number of columns in the terminal's viewport. Use
     * `ITerminalOptions.cols` to set this in the constructor and
     * `Terminal.resize` for when the terminal exists.
     */
    val cols: Number;

    /**
     * Access to the terminal's normal and alt buffer.
     */
    val buffer: dynamic;

    /**
     * (EXPERIMENTAL) Get all markers registered against the buffer. If the alt
     * buffer is active this will always return [].
     */
    val markers: Array<IMarker>;

    /**
     * Get the parser interface to register custom escape sequence handlers.
     */
    val parser: IParser;

    /**
     * (EXPERIMENTAL) Get the Unicode handling interface
     * to register and switch Unicode version.
     */
    val unicode: IUnicodeHandling;

    /**
     * Gets the terminal modes as set by SM/DECSET.
     */
    val modes: IModes;

    /**
     * Gets or sets the terminal options. This supports setting multiple
     * options.
     *
     * @example Get a single option
     * ```ts
     * console.log(terminal.options.fontSize);
     * ```
     *
     * @example Set a single option:
     * ```ts
     * terminal.options.fontSize = 12;
     * ```
     * Note that for options that are object, a new object must be used in order
     * to take effect as a reference comparison will be done:
     * ```ts
     * const newValue = terminal.options.theme;
     * newValue.background = '#000000';
     *
     * // This won't work
     * terminal.options.theme = newValue;
     *
     * // This will work
     * terminal.options.theme = { ...newValue };
     * ```
     *
     * @example Set multiple options
     * ```ts
     * terminal.options = {
     *   fontSize: 12,
     *   fontFamily: 'Courier New'
     * };
     * ```
     */
    var options: ITerminalOptions;

    /**
     * Creates a new `Terminal` object.
     *
     * @param options An object containing a set of options.
     */
    constructor(options: ITerminalOptions? = definedExternally);
    /**
     * Creates a new `Terminal` object.
     *
     * @param options An object containing a set of options.
     */
    constructor(options: ITerminalInitOnlyOptions);

    interface KeyEvent {
        val key: String
        val domEvent: KeyboardEvent
    }

    interface RenderEvent {
        val start: Number
        val end: Number
    }

    interface ResizeEvent {
        val cols: Number
        val rows: Number
    }

    /**
     * Adds an event listener for when the bell is triggered.
     * @returns an `IDisposable` to stop listening.
     */
    val onBell: (listener: () -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a binary event fires. This is used to
     * enable non UTF-8 conformant binary messages to be sent to the backend.
     * Currently this is only used for a certain type of mouse reports that
     * happen to be not UTF-8 compatible.
     * The event value is a JS string, pass it to the underlying pty as
     * binary data, e.g. `pty.write(Buffer.from(data, 'binary'))`.
     * @returns an `IDisposable` to stop listening.
     */
    val onBinary: (listener: (String) -> Unit) -> IDisposable

    /**
     * Adds an event listener for the cursor moves.
     * @returns an `IDisposable` to stop listening.
     */
    val onCursorMove: (listener: () -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a data event fires. This happens for
     * example when the user types or pastes into the terminal. The event value
     * is whatever `string` results, in a typical setup, this should be passed
     * on to the backing pty.
     * @returns an `IDisposable` to stop listening.
     */
    val onData: (listener: (String) -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a key is pressed. The event value contains the
     * string that will be sent in the data event as well as the DOM event that
     * triggered it.
     * @returns an `IDisposable` to stop listening.
     */
    val onKey: (listener: (KeyEvent) -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a line feed is added.
     * @returns an `IDisposable` to stop listening.
     */
    val onLineFeed: (listener: () -> Unit) -> IDisposable

    /**
     * Adds an event listener for when rows are rendered. The event value
     * contains the start row and end rows of the rendered area (ranges from `0`
     * to `Terminal.rows - 1`).
     * @returns an `IDisposable` to stop listening.
     */
    val onRender: (listener: (RenderEvent) -> Unit) -> IDisposable

    /**
     * Adds an event listener for when data has been parsed by the terminal,
     * after {@link write} is called. This event is useful to listen for any
     * changes in the buffer.
     *
     * This fires at most once per frame, after data parsing completes. Note
     * that this can fire when there are still writes pending if there is a lot
     * of data.
     */
    val onWriteParsed: (listener: () -> Unit) -> IDisposable

    /**
     * Adds an event listener for when the terminal is resized. The event value
     * contains the new size.
     * @returns an `IDisposable` to stop listening.
     */
    val onResize: (listener: (ResizeEvent) -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a scroll occurs. The event value is the
     * new position of the viewport.
     * @returns an `IDisposable` to stop listening.
     */
    val onScroll: (listener: (Number) -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a selection change occurs.
     * @returns an `IDisposable` to stop listening.
     */
    val onSelectionChange: (listener: () -> Unit) -> IDisposable

    /**
     * Adds an event listener for when an OSC 0 or OSC 2 title change occurs.
     * The event value is the new title.
     * @returns an `IDisposable` to stop listening.
     */
    val onTitleChange: (listener: (String) -> Unit) -> IDisposable

    /**
     * Unfocus the terminal.
     */
    fun blur()

    /**
     * Focus the terminal.
     */
    fun focus()

    /**
     * Resizes the terminal. It's best practice to debounce calls to resize,
     * this will help ensure that the pty can respond to the resize event
     * before another one occurs.
     * @param x The number of columns to resize to.
     * @param y The number of rows to resize to.
     */
    fun resize(columns: Number, rows: Number): Unit;

    /**
     * Opens the terminal within an element.
     * @param parent The element to create the terminal within. This element
     * must be visible (have dimensions) when `open` is called as several DOM-
     * based measurements need to be performed when this function is called.
     */
    fun open(parent: HTMLElement)

    /**
     * Attaches a custom key event handler which is run before keys are
     * processed, giving consumers of xterm.js ultimate control as to what keys
     * should be processed by the terminal and what keys should not.
     * @param customKeyEventHandler The custom KeyboardEvent handler to attach.
     * This is a function that takes a KeyboardEvent, allowing consumers to stop
     * propagation and/or prevent the default action. The function returns
     * whether the event should be processed by xterm.js.
     *
     * @example A custom keymap that overrides the backspace key
     * ```ts
     * const keymap = [
     *   { "key": "Backspace", "shiftKey": false, "mapCode": 8 },
     *   { "key": "Backspace", "shiftKey": true, "mapCode": 127 }
     * ];
     * term.attachCustomKeyEventHandler(ev => {
     *   if (ev.type === 'keydown') {
     *     for (let i in keymap) {
     *       if (keymap[i].key == ev.key && keymap[i].shiftKey == ev.shiftKey) {
     *         socket.send(String.fromCharCode(keymap[i].mapCode));
     *         return false;
     *       }
     *     }
     *   }
     * });
     * ```
     */
    fun attachCustomKeyEventHandler(customKeyEventHandler: (event: KeyboardEvent) -> Boolean)

    /**
     * Gets whether the terminal has an active selection.
     */
    fun hasSelection(): Boolean

    /**
     * Gets the terminal's current selection, this is useful for implementing
     * copy behavior outside of xterm.js.
     */
    fun getSelection(): String

    /**
     * Gets the selection position or undefined if there is no selection.
     */
    fun getSelectionPosition(): dynamic

    /**
     * Clears the current terminal selection.
     */
    fun clearSelection()

    /**
     * Selects text within the terminal.
     * @param column The column the selection starts at.
     * @param row The row the selection starts at.
     * @param length The length of the selection.
     */
    fun select(column: Number, row: Number, length: Number): Unit

    /**
     * Selects all text within the terminal.
     */
    fun selectAll(): Unit;

    /**
     * Selects text in the buffer between 2 lines.
     * @param start The 0-based line index to select from (inclusive).
     * @param end The 0-based line index to select to (inclusive).
     */
    fun selectLines(start: Number, end: Number): Unit;

    /*
     * Disposes of the terminal, detaching it from the DOM and removing any
     * active listeners. Once the terminal is disposed it should not be used
     * again.
     */
    override fun dispose()

    /**
     * Scroll the display of the terminal
     * @param amount The number of lines to scroll down (negative scroll up).
     */
    fun scrollLines(amount: Number): Unit;

    /**
     * Scroll the display of the terminal by a number of pages.
     * @param pageCount The number of pages to scroll (negative scrolls up).
     */
    fun scrollPages(pageCount: Number): Unit;

    /**
     * Scrolls the display of the terminal to the top.
     */
    fun scrollToTop(): Unit;

    /**
     * Scrolls the display of the terminal to the bottom.
     */
    fun scrollToBottom(): Unit;

    /**
     * Scrolls to a line within the buffer.
     * @param line The 0-based line index to scroll to.
     */
    fun scrollToLine(line: Number): Unit;

    /**
     * Clear the entire buffer, making the prompt line the new first line.
     */
    fun clear(): Unit;

    /**
     * Write data to the terminal.
     * @param data The data to write to the terminal. This can either be raw
     * bytes given as Uint8Array from the pty or a string. Raw bytes will always
     * be treated as UTF-8 encoded, string data as UTF-16.
     * @param callback Optional callback that fires when the data was processed
     * by the parser.
     */
    fun write(data: String, callback: (() -> Unit)? = definedExternally): Unit;

    /**
     * Write data to the terminal.
     * @param data The data to write to the terminal. This can either be raw
     * bytes given as Uint8Array from the pty or a string. Raw bytes will always
     * be treated as UTF-8 encoded, string data as UTF-16.
     * @param callback Optional callback that fires when the data was processed
     * by the parser.
     */
    fun write(data: Uint8Array, callback: (() -> Unit)? = definedExternally): Unit;

    /**
     * Writes data to the terminal, followed by a break line character (\n).
     * @param data The data to write to the terminal. This can either be raw
     * bytes given as Uint8Array from the pty or a string. Raw bytes will always
     * be treated as UTF-8 encoded, string data as UTF-16.
     * @param callback Optional callback that fires when the data was processed
     * by the parser.
     */
    fun writeln(data: String, callback: (() -> Unit)? = definedExternally): Unit;

    /**
     * Writes data to the terminal, followed by a break line character (\n).
     * @param data The data to write to the terminal. This can either be raw
     * bytes given as Uint8Array from the pty or a string. Raw bytes will always
     * be treated as UTF-8 encoded, string data as UTF-16.
     * @param callback Optional callback that fires when the data was processed
     * by the parser.
     */
    fun writeln(data: Uint8Array, callback: (() -> Unit)? = definedExternally): Unit;

    /**
     * Writes text to the terminal, performing the necessary transformations for pasted text.
     * @param data The text to write to the terminal.
     */
    fun paste(data: String): Unit;

    /**
     * Tells the renderer to refresh terminal content between two rows
     * (inclusive) at the next opportunity.
     * @param start The row to start from (between 0 and this.rows - 1).
     * @param end The row to end at (between start and this.rows - 1).
     */
    fun refresh(start: Number, end: Number): Unit;

    /**
     * Clears the texture atlas of the canvas renderer if it's active. Doing this will force a
     * redraw of all glyphs which can workaround issues causing the texture to become corrupt, for
     * example Chromium/Nvidia has an issue where the texture gets messed up when resuming the OS
     * from sleep.
     */
    fun clearTextureAtlas(): Unit;

    /**
     * Perform a full reset (RIS, aka '\x1bc').
     */
    fun reset(): Unit;

    /**
     * Loads an addon into this instance of xterm.js.
     * @param addon The addon to load.
     */
    fun loadAddon(addon: ITerminalAddon): Unit;
}

/**
 * An addon that can provide additional functionality to the terminal.
 */
external interface ITerminalAddon : IDisposable {
    /**
     * This is called when the addon is activated.
     */
    fun activate(terminal: Terminal)
}


/**
 * Data type to register a CSI, DCS or ESC callback in the parser
 * in the form:
 *    ESC I..I F
 *    CSI Prefix P..P I..I F
 *    DCS Prefix P..P I..I F data_bytes ST
 *
 * with these rules/restrictions:
 * - prefix can only be used with CSI and DCS
 * - only one leading prefix byte is recognized by the parser
 *   before any other parameter bytes (P..P)
 * - intermediate bytes are recognized up to 2
 *
 * For custom sequences make sure to read ECMA-48 and the resources at
 * vt100.net to not clash with existing sequences or reserved address space.
 * General recommendations:
 * - use private address space (see ECMA-48)
 * - use max one intermediate byte (technically not limited by the spec,
 *   in practice there are no sequences with more than one intermediate byte,
 *   thus parsers might get confused with more intermediates)
 * - test against other common emulators to check whether they escape/ignore
 *   the sequence correctly
 *
 * Notes: OSC command registration is handled differently (see addOscHandler)
 *        APC, PM or SOS is currently not supported.
 */
external interface IFunctionIdentifier {
    /**
     * Optional prefix byte, must be in range \x3c .. \x3f.
     * Usable in CSI and DCS.
     */
    var prefix: String?;

    /**
     * Optional intermediate bytes, must be in range \x20 .. \x2f.
     * Usable in CSI, DCS and ESC.
     */
    var intermediates: String?;

    /**
     * Final byte, must be in range \x40 .. \x7e for CSI and DCS,
     * \x30 .. \x7e for ESC.
     */
    var final: String;
}

/**
 * Allows hooking into the parser for custom handling of escape sequences.
 *
 * Note on sync vs. async handlers:
 * xterm.js implements all parser actions with synchronous handlers.
 * In general custom handlers should also operate in sync mode wherever
 * possible to keep the parser fast.
 * Still the exposed interfaces allow to register async handlers by returning
 * a `Promise<boolean>`. Here the parser will pause input processing until
 * the promise got resolved or rejected (in-band blocking). This "full stop"
 * on the input chain allows to implement backpressure from a certain async
 * action while the terminal state will not progress any further from input.
 * It does not mean that the terminal state will not change at all in between,
 * as user actions like resize or reset are still processed immediately.
 * It is an error to assume a stable terminal state while giving back control
 * in between, e.g. by multiple chained `then` calls.
 * Downside of an async handler is a rather bad throughput performance,
 * thus use async handlers only as a last resort or for actions that have
 * to rely on async interfaces itself.
 */
external interface IParser {
    /**
     * Adds a handler for CSI escape sequences.
     * @param id Specifies the function identifier under which the callback
     * gets registered, e.g. {final: 'm'} for SGR.
     * @param callback The function to handle the sequence. The callback is
     * called with the numerical params. If the sequence has subparams the
     * array will contain subarrays with their numercial values.
     * Return `true` if the sequence was handled, `false` if the parser should try
     * a previous handler. The most recently added handler is tried first.
     * @returns An IDisposable you can call to remove this handler.
     */
    fun registerCsiHandler(id: IFunctionIdentifier, callback: (params: dynamic) -> dynamic): IDisposable;

    /**
     * Adds a handler for DCS escape sequences.
     * @param id Specifies the function identifier under which the callback
     * gets registered, e.g. {intermediates: '$' final: 'q'} for DECRQSS.
     * @param callback The function to handle the sequence. Note that the
     * function will only be called once if the sequence finished sucessfully.
     * There is currently no way to intercept smaller data chunks, data chunks
     * will be stored up until the sequence is finished. Since DCS sequences
     * are not limited by the amount of data this might impose a problem for
     * big payloads. Currently xterm.js limits DCS payload to 10 MB
     * which should give enough room for most use cases.
     * The function gets the payload and numerical parameters as arguments.
     * Return `true` if the sequence was handled, `false` if the parser should try
     * a previous handler. The most recently added handler is tried first.
     * @returns An IDisposable you can call to remove this handler.
     */
    fun registerDcsHandler(id: IFunctionIdentifier, callback: (data: String, param: dynamic) -> dynamic): IDisposable;

    /**
     * Adds a handler for ESC escape sequences.
     * @param id Specifies the function identifier under which the callback
     * gets registered, e.g. {intermediates: '%' final: 'G'} for
     * default charset selection.
     * @param callback The function to handle the sequence.
     * Return `true` if the sequence was handled, `false` if the parser should try
     * a previous handler. The most recently added handler is tried first.
     * @returns An IDisposable you can call to remove this handler.
     */
    fun registerEscHandler(id: IFunctionIdentifier, handler: () -> dynamic): IDisposable;

    /**
     * Adds a handler for OSC escape sequences.
     * @param ident The number (first parameter) of the sequence.
     * @param callback The function to handle the sequence. Note that the
     * function will only be called once if the sequence finished sucessfully.
     * There is currently no way to intercept smaller data chunks, data chunks
     * will be stored up until the sequence is finished. Since OSC sequences
     * are not limited by the amount of data this might impose a problem for
     * big payloads. Currently xterm.js limits OSC payload to 10 MB
     * which should give enough room for most use cases.
     * The callback is called with OSC data string.
     * Return `true` if the sequence was handled, `false` if the parser should try
     * a previous handler. The most recently added handler is tried first.
     * @returns An IDisposable you can call to remove this handler.
     */
    fun registerOscHandler(ident: Number, callback: (data: String) -> dynamic): IDisposable;
}

/**
 * (EXPERIMENTAL) Unicode version provider.
 * Used to register custom Unicode versions with `Terminal.unicode.register`.
 */
external interface IUnicodeVersionProvider {
    /**
     * String indicating the Unicode version provided.
     */
    val version: String;

    /**
     * Unicode version dependent wcwidth implementation.
     */
    fun wcwidth(codepoint: Number): Int;
}

/**
 * (EXPERIMENTAL) Unicode handling interface.
 */
external interface IUnicodeHandling {
    /**
     * Register a custom Unicode version provider.
     */
    fun register(provider: IUnicodeVersionProvider): Unit;

    /**
     * Registered Unicode versions.
     */
    val versions: Array<String>;

    /**
     * Getter/setter for active Unicode version.
     */
    val activeVersion: String;
}

/**
 * Terminal modes as set by SM/DECSET.
 */
external interface IModes {
    /**
     * Application Cursor Keys (DECCKM): `CSI ? 1 h`
     */
    val applicationCursorKeysMode: Boolean;

    /**
     * Application Keypad Mode (DECNKM): `CSI ? 6 6 h`
     */
    val applicationKeypadMode: Boolean;

    /**
     * Bracketed Paste Mode: `CSI ? 2 0 0 4 h`
     */
    val bracketedPasteMode: Boolean;

    /**
     * Insert Mode (IRM): `CSI 4 h`
     */
    val insertMode: Boolean;

    /**
     * Mouse Tracking, this can be one of the following:
     * - none: This is the default value and can be reset with DECRST
     * - x10: Send Mouse X & Y on button press `CSI ? 9 h`
     * - vt200: Send Mouse X & Y on button press and release `CSI ? 1 0 0 0 h`
     * - drag: Use Cell Motion Mouse Tracking `CSI ? 1 0 0 2 h`
     * - any: Use All Motion Mouse Tracking `CSI ? 1 0 0 3 h`
     */
    val mouseTrackingMode: dynamic;

    /**
     * Origin Mode (DECOM): `CSI ? 6 h`
     */
    val originMode: Boolean;

    /**
     * Reverse-wraparound Mode: `CSI ? 4 5 h`
     */
    val reverseWraparoundMode: Boolean;

    /**
     * Send FocusIn/FocusOut events: `CSI ? 1 0 0 4 h`
     */
    val sendFocusMode: Boolean;

    /**
     * Auto-Wrap Mode (DECAWM): `CSI ? 7 h`
     */
    val wraparoundMode: Boolean
}
