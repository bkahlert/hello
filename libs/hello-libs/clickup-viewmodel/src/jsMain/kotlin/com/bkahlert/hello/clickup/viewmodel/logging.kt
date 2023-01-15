package com.bkahlert.hello.clickup.viewmodel

import com.bkahlert.kommons.logging.InlineLogger

public inline fun <reified R> InlineLogger.grouping(
    label: String? = null,
    collapsed: Boolean = true,
    render: (R) -> Any? = { JSON.stringify(it) },
    crossinline block: () -> R,
): R =
    console.grouping(label, collapsed, render, block)

public suspend inline fun <reified R> InlineLogger.groupCatching(
    label: String? = null,
    collapsed: Boolean = true,
    render: (R) -> Any? = { JSON.stringify(it) },
    crossinline block: suspend () -> R,
): Result<R> =
    console.groupCatching(label, collapsed, render) { block() }

/** Exposes the [console API](https://developer.mozilla.org/en/DOM/console) to Kotlin. */
public external interface Console : kotlin.js.Console {

    /**
     * Outputs a stack trace to this [Console].
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/trace">console.trace()</a>
     */
    public fun trace(vararg objects: Any?)

    /**
     * Creates a new inline group with the specified [label] in this [Console] log,
     * causing any later console messages to be indented by an extra level,
     * until [Console.groupEnd] is called.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/group">console.group()</a>
     */
    public fun group(label: String? = definedExternally)

    /**
     * Creates a new inline group with the specified [label] in this [Console].
     *
     * Unlike [Console.grouping], however, the new group is created collapsed.
     * The user needs to use the disclosure button next to it to expand it,
     * revealing the entries created in the group.
     *
     * Call [Console.groupEnd] to back out to the parent group.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/groupCollapsed">console.groupCollapsed()</a>
     */
    public fun groupCollapsed(label: String? = definedExternally)

    /**
     * Exits the current inline group in this [Console].
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/groupEnd">console.groupEnd()</a>
     */
    public fun groupEnd()

    /**
     * Displays tabular [data] as a table of the specified [columns].
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/table">console.table()</a>
     */
    public fun table(data: Any, columns: Array<String>? = definedExternally)
}

/** Exposes the [console API](https://developer.mozilla.org/en/DOM/console) to Kotlin. */
public external val console: Console

/**
 * Creates a new inline group with the optionally specified [label] in this [Console] log,
 * causing any later console messages to be indented by an extra level,
 * until [Console.groupEnd] is called.
 *
 * In case of [Result.isSuccess] the specified [render] applied to the result is logged,
 * otherwise the thrown exception is logged as an error.
 */
public inline fun <reified R> Console.groupCatching(
    label: String? = null,
    collapsed: Boolean = true,
    render: (R) -> Any? = { JSON.stringify(it) },
    block: () -> R,
): Result<R> {
    if (collapsed) label?.also { groupCollapsed(it) } ?: groupCollapsed()
    else label?.also { group(it) } ?: group()
    val result = runCatching(block)
        .onSuccess { log("${label?.let { "$label " }}returned", render(it)) }
        .onFailure { error("${label?.let { "$label " }}failed", it) }
    groupEnd()
    return result
}

/**
 * Creates a new inline group with the optionally specified [label] in this [Console] log,
 * causing any later console messages to be indented by an extra level,
 * until [Console.groupEnd] is called.
 *
 * In case of [Result.isSuccess] the specified [render] applied to the result is logged,
 * otherwise the thrown exception is logged as an error.
 */
public inline fun <reified R> Console.grouping(
    label: String? = null,
    collapsed: Boolean = true,
    render: (R) -> Any? = { JSON.stringify(it) },
    block: () -> R,
): R =
    groupCatching(label, collapsed, render, block).getOrThrow()
