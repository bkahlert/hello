package com.bkahlert.hello.xterm

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
@JsModule("xterm")
@JsNonModule
public external interface IParser {
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
    public fun registerCsiHandler(id: IFunctionIdentifier, callback: (params: dynamic) -> dynamic): IDisposable;

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
    public fun registerDcsHandler(id: IFunctionIdentifier, callback: (data: String, param: dynamic) -> dynamic): IDisposable;

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
    public fun registerEscHandler(id: IFunctionIdentifier, handler: () -> dynamic): IDisposable;

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
    public fun registerOscHandler(ident: Number, callback: (data: String) -> dynamic): IDisposable;
}
