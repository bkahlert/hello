package com.bkahlert.hello.xterm

import js.typedarrays.Uint8Array
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.KeyboardEvent

/**
 * The class that represents an xterm.js terminal.
 */
@JsModule("xterm")
@JsNonModule
public external class Terminal : IDisposable {
    /**
     * The element containing the terminal.
     */
    public val element: HTMLElement?

    /**
     * The textarea that accepts input for the terminal.
     */
    public val textarea: HTMLTextAreaElement?

    /**
     * The number of rows in the terminal's viewport. Use
     * `ITerminalOptions.rows` to set this in the constructor and
     * `Terminal.resize` for when the terminal exists.
     */
    public val rows: Number;

    /**
     * The number of columns in the terminal's viewport. Use
     * `ITerminalOptions.cols` to set this in the constructor and
     * `Terminal.resize` for when the terminal exists.
     */
    public val cols: Number;

    /**
     * Access to the terminal's normal and alt buffer.
     */
    public val buffer: dynamic;

    /**
     * (EXPERIMENTAL) Get all markers registered against the buffer. If the alt
     * buffer is active this will always return [].
     */
    public val markers: Array<IMarker>;

    /**
     * Get the parser interface to register custom escape sequence handlers.
     */
    public val parser: IParser;

    /**
     * (EXPERIMENTAL) Get the Unicode handling interface
     * to register and switch Unicode version.
     */
    public val unicode: IUnicodeHandling;

    /**
     * Gets the terminal modes as set by SM/DECSET.
     */
    public val modes: IModes;

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
    public var options: ITerminalOptions;

    /**
     * Creates a new `Terminal` object.
     *
     * @param options An object containing a set of options.
     */
    public constructor(options: ITerminalOptions? = definedExternally);
    /**
     * Creates a new `Terminal` object.
     *
     * @param options An object containing a set of options.
     */
    public constructor(options: ITerminalInitOnlyOptions);

    public interface KeyEvent {
        public val key: String
        public val domEvent: KeyboardEvent
    }

    public interface RenderEvent {
        public val start: Number
        public val end: Number
    }

    public interface ResizeEvent {
        public val cols: Number
        public val rows: Number
    }

    /**
     * Adds an event listener for when the bell is triggered.
     * @returns an `IDisposable` to stop listening.
     */
    public val onBell: (listener: () -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a binary event fires. This is used to
     * enable non UTF-8 conformant binary messages to be sent to the backend.
     * Currently this is only used for a certain type of mouse reports that
     * happen to be not UTF-8 compatible.
     * The event value is a JS string, pass it to the underlying pty as
     * binary data, e.g. `pty.write(Buffer.from(data, 'binary'))`.
     * @returns an `IDisposable` to stop listening.
     */
    public val onBinary: (listener: (String) -> Unit) -> IDisposable

    /**
     * Adds an event listener for the cursor moves.
     * @returns an `IDisposable` to stop listening.
     */
    public val onCursorMove: (listener: () -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a data event fires. This happens for
     * example when the user types or pastes into the terminal. The event value
     * is whatever `string` results, in a typical setup, this should be passed
     * on to the backing pty.
     * @returns an `IDisposable` to stop listening.
     */
    public val onData: (listener: (String) -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a key is pressed. The event value contains the
     * string that will be sent in the data event as well as the DOM event that
     * triggered it.
     * @returns an `IDisposable` to stop listening.
     */
    public val onKey: (listener: (KeyEvent) -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a line feed is added.
     * @returns an `IDisposable` to stop listening.
     */
    public val onLineFeed: (listener: () -> Unit) -> IDisposable

    /**
     * Adds an event listener for when rows are rendered. The event value
     * contains the start row and end rows of the rendered area (ranges from `0`
     * to `Terminal.rows - 1`).
     * @returns an `IDisposable` to stop listening.
     */
    public val onRender: (listener: (RenderEvent) -> Unit) -> IDisposable

    /**
     * Adds an event listener for when data has been parsed by the terminal,
     * after {@link write} is called. This event is useful to listen for any
     * changes in the buffer.
     *
     * This fires at most once per frame, after data parsing completes. Note
     * that this can fire when there are still writes pending if there is a lot
     * of data.
     */
    public val onWriteParsed: (listener: () -> Unit) -> IDisposable

    /**
     * Adds an event listener for when the terminal is resized. The event value
     * contains the new size.
     * @returns an `IDisposable` to stop listening.
     */
    public val onResize: (listener: (ResizeEvent) -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a scroll occurs. The event value is the
     * new position of the viewport.
     * @returns an `IDisposable` to stop listening.
     */
    public val onScroll: (listener: (Number) -> Unit) -> IDisposable

    /**
     * Adds an event listener for when a selection change occurs.
     * @returns an `IDisposable` to stop listening.
     */
    public val onSelectionChange: (listener: () -> Unit) -> IDisposable

    /**
     * Adds an event listener for when an OSC 0 or OSC 2 title change occurs.
     * The event value is the new title.
     * @returns an `IDisposable` to stop listening.
     */
    public val onTitleChange: (listener: (String) -> Unit) -> IDisposable

    /**
     * Unfocus the terminal.
     */
    public fun blur()

    /**
     * Focus the terminal.
     */
    public fun focus()

    /**
     * Resizes the terminal. It's best practice to debounce calls to resize,
     * this will help ensure that the pty can respond to the resize event
     * before another one occurs.
     * @param x The number of columns to resize to.
     * @param y The number of rows to resize to.
     */
    public fun resize(columns: Number, rows: Number): Unit;

    /**
     * Opens the terminal within an element.
     * @param parent The element to create the terminal within. This element
     * must be visible (have dimensions) when `open` is called as several DOM-
     * based measurements need to be performed when this function is called.
     */
    public fun open(parent: HTMLElement)

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
    public fun attachCustomKeyEventHandler(customKeyEventHandler: (event: KeyboardEvent) -> Boolean)

    /**
     * Gets whether the terminal has an active selection.
     */
    public fun hasSelection(): Boolean

    /**
     * Gets the terminal's current selection, this is useful for implementing
     * copy behavior outside of xterm.js.
     */
    public fun getSelection(): String

    /**
     * Gets the selection position or undefined if there is no selection.
     */
    public fun getSelectionPosition(): dynamic

    /**
     * Clears the current terminal selection.
     */
    public fun clearSelection()

    /**
     * Selects text within the terminal.
     * @param column The column the selection starts at.
     * @param row The row the selection starts at.
     * @param length The length of the selection.
     */
    public fun select(column: Number, row: Number, length: Number): Unit

    /**
     * Selects all text within the terminal.
     */
    public fun selectAll(): Unit;

    /**
     * Selects text in the buffer between 2 lines.
     * @param start The 0-based line index to select from (inclusive).
     * @param end The 0-based line index to select to (inclusive).
     */
    public fun selectLines(start: Number, end: Number): Unit;

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
    public fun scrollLines(amount: Number): Unit;

    /**
     * Scroll the display of the terminal by a number of pages.
     * @param pageCount The number of pages to scroll (negative scrolls up).
     */
    public fun scrollPages(pageCount: Number): Unit;

    /**
     * Scrolls the display of the terminal to the top.
     */
    public fun scrollToTop(): Unit;

    /**
     * Scrolls the display of the terminal to the bottom.
     */
    public fun scrollToBottom(): Unit;

    /**
     * Scrolls to a line within the buffer.
     * @param line The 0-based line index to scroll to.
     */
    public fun scrollToLine(line: Number): Unit;

    /**
     * Clear the entire buffer, making the prompt line the new first line.
     */
    public fun clear(): Unit;

    /**
     * Write data to the terminal.
     * @param data The data to write to the terminal. This can either be raw
     * bytes given as Uint8Array from the pty or a string. Raw bytes will always
     * be treated as UTF-8 encoded, string data as UTF-16.
     * @param callback Optional callback that fires when the data was processed
     * by the parser.
     */
    public fun write(data: String, callback: (() -> Unit)? = definedExternally): Unit;

    /**
     * Write data to the terminal.
     * @param data The data to write to the terminal. This can either be raw
     * bytes given as Uint8Array from the pty or a string. Raw bytes will always
     * be treated as UTF-8 encoded, string data as UTF-16.
     * @param callback Optional callback that fires when the data was processed
     * by the parser.
     */
    public fun write(data: Uint8Array, callback: (() -> Unit)? = definedExternally): Unit;

    /**
     * Writes data to the terminal, followed by a break line character (\n).
     * @param data The data to write to the terminal. This can either be raw
     * bytes given as Uint8Array from the pty or a string. Raw bytes will always
     * be treated as UTF-8 encoded, string data as UTF-16.
     * @param callback Optional callback that fires when the data was processed
     * by the parser.
     */
    public fun writeln(data: String, callback: (() -> Unit)? = definedExternally): Unit;

    /**
     * Writes data to the terminal, followed by a break line character (\n).
     * @param data The data to write to the terminal. This can either be raw
     * bytes given as Uint8Array from the pty or a string. Raw bytes will always
     * be treated as UTF-8 encoded, string data as UTF-16.
     * @param callback Optional callback that fires when the data was processed
     * by the parser.
     */
    public fun writeln(data: Uint8Array, callback: (() -> Unit)? = definedExternally): Unit;

    /**
     * Writes text to the terminal, performing the necessary transformations for pasted text.
     * @param data The text to write to the terminal.
     */
    public fun paste(data: String): Unit;

    /**
     * Tells the renderer to refresh terminal content between two rows
     * (inclusive) at the next opportunity.
     * @param start The row to start from (between 0 and this.rows - 1).
     * @param end The row to end at (between start and this.rows - 1).
     */
    public fun refresh(start: Number, end: Number): Unit;

    /**
     * Clears the texture atlas of the canvas renderer if it's active. Doing this will force a
     * redraw of all glyphs which can workaround issues causing the texture to become corrupt, for
     * example Chromium/Nvidia has an issue where the texture gets messed up when resuming the OS
     * from sleep.
     */
    public fun clearTextureAtlas(): Unit;

    /**
     * Perform a full reset (RIS, aka '\x1bc').
     */
    public fun reset(): Unit;

    /**
     * Loads an addon into this instance of xterm.js.
     * @param addon The addon to load.
     */
    public fun loadAddon(addon: ITerminalAddon): Unit;
}
