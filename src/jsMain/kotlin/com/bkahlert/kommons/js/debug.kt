package com.bkahlert.kommons.js

import kotlin.js.Console

/**
 * Outputs a stack trace to `this` [Console].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/trace">console.trace()</a>
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Console.trace(vararg objects: Any?) {
    asDynamic().trace(objects)
}

/**
 * Creates a new inline group in `this` [Console] log,
 * causing any subsequent console messages to be indented by an additional level,
 * until [Console.groupEnd] is called.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/group">console.group()</a>
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Console.group() {
    asDynamic().group()
}

/**
 * Creates a new inline group with the specified [label] in `this` [Console] log,
 * causing any subsequent console messages to be indented by an additional level,
 * until [Console.groupEnd] is called.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/group">console.group()</a>
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Console.group(label: String) {
    asDynamic().group(label)
}

/**
 * Creates a new inline group in `this` [Console].
 *
 * Unlike [Console.grouping], however, the new group is created collapsed.
 * The user will need to use the disclosure button next to it to expand it,
 * revealing the entries created in the group.
 *
 * Call [Console.groupEnd] to back out to the parent group.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/groupCollapsed">console.groupCollapsed()</a>
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Console.groupCollapsed() {
    asDynamic().groupCollapsed()
}

/**
 * Creates a new inline group with the specified [label] in `this` [Console].
 *
 * Unlike [Console.grouping], however, the new group is created collapsed.
 * The user will need to use the disclosure button next to it to expand it,
 * revealing the entries created in the group.
 *
 * Call [Console.groupEnd] to back out to the parent group.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/groupCollapsed">console.groupCollapsed()</a>
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Console.groupCollapsed(label: String) {
    asDynamic().groupCollapsed(label)
}

/**
 * Exits the current inline group in `this` [Console].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/groupEnd">console.groupEnd()</a>
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Console.groupEnd() {
    asDynamic().groupEnd()
}

/**
 * Displays tabular [data] as a table.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/table">console.table()</a>
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Console.table(data: Any) {
    asDynamic().table(data)
}

/**
 * Displays tabular [data] as a table of the specified [columns].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/table">console.table()</a>
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Console.table(data: Any, columns: Array<String>) {
    asDynamic().table(data, columns)
}

/**
 * Creates a new inline group with the optionally specified [label] in `this` [Console] log,
 * causing any subsequent console messages to be indented by an additional level,
 * until [Console.groupEnd] is called.
 */
inline fun <reified R> Console.groupCatching(label: String? = null, collapsed: Boolean = true, block: () -> R): Result<R> {
    if (collapsed) label?.also { groupCollapsed(it) } ?: groupCollapsed()
    else label?.also { group(it) } ?: group()
    val result = runCatching(block)
        .onSuccess { log("${label?.let { "$label " }}returned", it.toString()) }
        .onFailure { error("${label?.let { "$label " }}failed", it) }
    groupEnd()
    return result
}

/**
 * Creates a new inline group with the optionally specified [label] in `this` [Console] log,
 * causing any subsequent console messages to be indented by an additional level,
 * until [Console.groupEnd] is called.
 */
inline fun <reified R> Console.grouping(label: String? = null, collapsed: Boolean = true, block: () -> R): R =
    groupCatching(label, collapsed, block).getOrThrow()

/**
 * Prints a table containing the specified [data].
 */
fun Console.data(vararg data: Any) {
    table(data.toJsonArray())
}
