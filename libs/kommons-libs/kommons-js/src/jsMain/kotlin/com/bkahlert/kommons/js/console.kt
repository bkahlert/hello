package com.bkahlert.kommons.js

import kotlin.js.Console
import kotlin.reflect.KProperty

/*

    assert(condition?: boolean, ...data: any[]): void;
    clear(): void;
    count(label?: string): void;
    countReset(label?: string): void;
    debug(...data: any[]): void;
    dir(item?: any, options?: any): void;
    dirxml(...data: any[]): void;
    error(...data: any[]): void;
    group(...data: any[]): void;
    groupCollapsed(...data: any[]): void;
    groupEnd(): void;
    info(...data: any[]): void;
    log(...data: any[]): void;
    table(tabularData?: any, properties?: string[]): void;
    time(label?: string): void;
    timeEnd(label?: string): void;
    timeLog(label?: string, ...data: any[]): void;
    timeStamp(label?: string): void;
    trace(...data: any[]): void;
    warn(...data: any[]): void;
 */

/**
 * Outputs a message to this [Console] at the "debug" log level.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/debug">console.debug()</a>
 */
public inline fun Console.debug(vararg objects: Any?) {
    asDynamic().debug.apply(this, objects)
}

/**
 * Outputs a stack trace to this [Console].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/trace">console.trace()</a>
 */
public inline fun Console.trace(vararg objects: Any?) {
    asDynamic().trace.apply(this, objects)
}

/**
 * Creates a new inline group in this [Console] log,
 * causing any later console messages to be indented by an extra level,
 * until [Console.groupEnd] is called.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/group">console.group()</a>
 */
public inline fun Console.group() {
    asDynamic().group()
}

/**
 * Creates a new inline group with the specified [label] in this [Console] log,
 * causing any later console messages to be indented by an extra level,
 * until [Console.groupEnd] is called.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/group">console.group()</a>
 */
public inline fun Console.group(label: String) {
    asDynamic().group(label)
}

/**
 * Creates a new inline group in this [Console].
 *
 * Unlike [Console.group], however, the new group is created collapsed.
 * The user needs to use the disclosure button next to it to expand it,
 * revealing the entries created in the group.
 *
 * Call [Console.groupEnd] to back out to the parent group.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/groupCollapsed">console.groupCollapsed()</a>
 */
public inline fun Console.groupCollapsed() {
    asDynamic().groupCollapsed()
}

/**
 * Creates a new inline group with the specified [label] in this [Console].
 *
 * Unlike [Console.group], however, the new group is created collapsed.
 * The user needs to use the disclosure button next to it to expand it,
 * revealing the entries created in the group.
 *
 * Call [Console.groupEnd] to back out to the parent group.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/groupCollapsed">console.groupCollapsed()</a>
 */
public inline fun Console.groupCollapsed(label: String) {
    asDynamic().groupCollapsed(label)
}

/**
 * Exits the current inline group in this [Console].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/groupEnd">console.groupEnd()</a>
 */
public inline fun Console.groupEnd() {
    asDynamic().groupEnd()
}

/**
 * Displays tabular [data] as a table.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/table">console.table()</a>
 */
public inline fun Console.table(data: Any) {
    asDynamic().table(data)
}

/**
 * Displays tabular [data] as a table of the specified [columns].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/table">console.table()</a>
 */
public inline fun Console.table(data: Any, columns: Array<String>) {
    asDynamic().table(data, columns)
}

/* EXTENSIONS */

/**
 * Creates a new—optionally [collapsed]—inline group in this [Console] log,
 * causing any later console messages to be indented by an extra level,
 * until [Console.groupEnd] is called.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/group">console.group()</a>
 */
public inline fun Console.group(collapsed: Boolean) {
    if (collapsed) groupCollapsed() else group()
}

/**
 * Creates a new—optionally [collapsed]—inline group with the specified [label] in this [Console] log,
 * causing any later console messages to be indented by an extra level,
 * until [Console.groupEnd] is called.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/group">console.group()</a>
 */
public inline fun Console.group(label: String, collapsed: Boolean) {
    if (collapsed) groupCollapsed(label) else group(label)
}

public const val debugging: Boolean = false

/**
 * Runs the specified [block] wrapped by an optionally [collapsed] group
 * with the specified [label].
 */
public suspend inline fun <R> Console.grouping(
    label: String,
    collapsed: Boolean = true,
    crossinline block: suspend () -> R,
): R {
    if (debugging) console.info("SUSPENDABLE GROUP START($label)") else group(label = label, collapsed = collapsed)
    try {
        return block()
    } catch (ex: Throwable) {
        if (debugging) console.error("SUSPENDABLE GROUP EXCEPTION($label)", ex)
        throw ex
    } finally {
        if (debugging) console.info("SUSPENDABLE GROUP END($label)") else groupEnd()
    }
}

/**
 * Runs the specified [block] wrapped by an optionally [collapsed] group
 * with the specified [label].
 */
public inline fun <R> Console.grouping(
    label: String,
    collapsed: Boolean = true,
    block: () -> R,
): R {
    if (debugging) console.info("GROUP START($label)") else group(label = label, collapsed = collapsed)
    try {
        return block()
    } catch (ex: Throwable) {
        if (debugging) console.error("GROUP EXCEPTION($label)", ex)
        throw ex
    } finally {
        if (debugging) console.info("GROUP END($label)") else groupEnd()
    }
}


/**
 * Displays the specified [data] as a table
 * optionally filtered to the specified [columns].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/table">console.table()</a>
 */
public fun <T> Console.table(
    data: Collection<T>,
    vararg columns: KProperty<*>,
    name: String? = null,
    label: (T) -> String = { it.toString() },
) {
    table(data.associateBy(label), *columns, name = name)
}

/**
 * Displays the specified [data] as a table
 * optionally filtered to the specified [columns].
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/table">console.table()</a>
 */
public fun Console.table(
    data: Map<String, Any?>,
    vararg columns: KProperty<*>,
    name: String? = null,
) {
    val table: () -> Unit = {
        if (columns.isEmpty()) {
            table(json(data))
        } else {
            table(json(data), columns.map { it.name }.toTypedArray())
        }
    }

    if (name != null) grouping(name, block = table)
    else table()
}
