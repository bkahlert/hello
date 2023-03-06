package com.bkahlert.kommons.js

import kotlin.js.Date
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/** Exposes the [console API](https://developer.mozilla.org/en/DOM/console) to Kotlin. */
public external interface Console {
    public fun assert(condition: Boolean? = definedExternally, vararg data: Any?)
    public fun clear()
    public fun count(label: String? = definedExternally)
    public fun countReset(label: String = definedExternally)

    /**
     * Outputs a message to this [Console] at the "debug" log level.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/debug">console.debug()</a>
     */
    public fun debug(vararg data: Any?)
    public fun dir(item: Any? = definedExternally, options: Any? = definedExternally)
    public fun dirxml(vararg data: Any?)
    public fun error(vararg data: Any?)

    /**
     * Creates a new inline group in this [Console] log,
     * causing any later console messages to be indented by an extra level,
     * until [Console.groupEnd] is called.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/group">console.group()</a>
     */
    public fun group(vararg data: Any?)

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
    public fun groupCollapsed(vararg data: Any?)

    /**
     * Exits the current inline group in this [Console].
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/groupEnd">console.groupEnd()</a>
     */
    public fun groupEnd()
    public fun info(vararg data: Any?)
    public fun log(vararg data: Any?)

    /**
     * Displays tabular [tabularData] as a table of the specified [properties].
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/table">console.table()</a>
     */
    public fun table(tabularData: Any? = definedExternally, properties: Array<String>? = definedExternally)

    /**
     * Starts a timer you can use to track how long an operation takes.
     *
     * You give each timer a unique name, and may have up to 10,000 timers running on a given page.
     * When you call [timeEnd] with the same name, the browser outputs the time, in milliseconds, which elapsed since the timer was started.
     *
     * @param label A string representing the name to give the new timer.
     * Use the same name when calling [timeEnd] to stop the timer and get the time output to the console.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/time">console.time()</a>
     */
    public fun time(label: String? = definedExternally)

    /**
     * Stops a timer that was started before by calling [time].
     *
     * See [Timers](https://developer.mozilla.org/en-US/docs/Web/API/console#timers) in the documentation for details and examples.
     *
     * @param label A string representing the name of the timer to stop.
     * Once stopped, the elapsed time is automatically displayed in the Web console along with an indicator that the time has ended.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/timeEnd">console.timeEnd()</a>
     */
    public fun timeEnd(label: String? = definedExternally)

    /**
     * Logs the current value of a timer that was started before by calling [time].
     *
     * @param label The name of the timer to log to the console. If this is omitted the label "default" is used.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/timeLog">console.timeLog()</a>
     */
    public fun timeLog(label: String? = definedExternally, vararg data: Any?)

    /**
     * The console.timeStamp method adds a single marker to the browser's Performance tool (Firefox, Chrome). This lets you correlate a point in your code with the other events recorded in the timeline, such as layout and paint events.
     *
     * @param label Label for the timestamp. Optional.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/timeStamp">console.time()</a>
     */
    @Deprecated("Non-standard, should not be used in production")
    public fun timeStamp(label: String? = definedExternally)

    /**
     * Outputs a stack trace to this [Console].
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/console/trace">console.trace()</a>
     */
    public fun trace(vararg data: Any?)
    public fun warn(vararg data: Any?)
}

/** Exposes the [console API](https://developer.mozilla.org/en/DOM/console) to Kotlin. */
public external val console: Console


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

public var CONSOLE_DEBUGGING: Boolean = false
public var CONSOLE_DEFAULT_COLLAPSED: Boolean = !CONSOLE_DEBUGGING

public val timeId: (Int) -> String by lazy {
    val random = Random(Date.now().toLong())
    val range: CharRange = '0'..'z'
    val gen: (Int) -> String = { length -> (1..length).joinToString("") { range.random(random).toString() } }
    gen
}

/**
 * Runs the specified [block] wrapped by an optionally [collapsed] group
 * with the specified [label].
 */
public inline fun <R> Console.grouping(
    label: String,
    collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED,
    block: () -> R,
): R {
    if (CONSOLE_DEBUGGING) console.info("GROUP START($label)") else group(label = label, collapsed = collapsed)
    try {
        val timeLabel = "$label-${timeId(4)}"
        time(timeLabel)
        val result = block()
        timeEnd(timeLabel)
        if (CONSOLE_DEBUGGING) console.info("GROUP RESULT($label)", result)
        else when (result) {
            Unit -> {}
            is Map<*, Any?> -> console.table(data = result.mapKeys { it.toString() })
            else -> console.debug("⏎ ", result)
        }
        return result
    } catch (ex: Throwable) {
        if (CONSOLE_DEBUGGING) console.error("GROUP EXCEPTION($label)", ex)
        else console.warn(ex::class.simpleName)
        throw ex
    } finally {
        if (CONSOLE_DEBUGGING) console.info("GROUP END($label)") else groupEnd()
    }
}

/**
 * Runs the specified [block] wrapped by an optionally [collapsed] group
 * with the specified [type] and [operation] as its label.
 */
public inline fun <R> Console.grouping(
    type: KClass<*>,
    operation: String? = null,
    vararg args: Any?,
    collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED,
    block: () -> R,
): R = grouping(buildString {
    append(type.simpleName ?: "<object>")
    if (operation != null) append(".$operation(")
    args.joinTo(this)
    if (operation != null) append(")")
    "${type.simpleName}${operation?.let { ": $it(" } ?: ""}"
}, collapsed, block)

/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */
public inline fun <P1 : Any, R> Console.grouping(
    type: KClass<P1>,
    operation: kotlin.reflect.KCallable<R>,
    vararg args: Array<out Any?>,
    collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED,
    block: () -> R,
): R = grouping(
    type = type,
    operation = operation.name,
    args = args,
    collapsed = collapsed,
    block = block
)

// TODO use arguments instead of having to pass args manually; might be good to remove Console receiver
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */
public inline fun <reified P1 : Any, R> grouping2(
    operation: kotlin.reflect.KCallable<R>,
    collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED,
    block: () -> R,
): R {
    val args = js("arguments")
    val argsList = buildList { for (i in 0 until args.length as Int) add(args[i]) }
    console.log("ARGS", args, js("Array.from(arguments)"), argsList)
    return console.grouping(
        type = P1::class,
        operation = operation.name,
        args = buildList { for (i in 0 until args.length as Int) add(args[i]) }.toTypedArray(),
        collapsed = collapsed,
        block = block
    )
}

// @formatter:off
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, R> Console.grouping(operation: kotlin.reflect.KFunction1<P1, R>, collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED, block: () -> R): R = grouping(type = P1::class, operation=operation.name, args = emptyArray(), collapsed = collapsed, block=block)
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, R> Console.grouping(operation: kotlin.reflect.KFunction2<P1, P2, R>, p2: P2, collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2), collapsed=collapsed, block=block)
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, R> Console.grouping(operation: kotlin.reflect.KFunction3<P1, P2, P3, R>, p2: P2, p3: P3, collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3), collapsed=collapsed, block=block)
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, P4, R> Console.grouping(operation: kotlin.reflect.KFunction4<P1, P2, P3, P4, R>, p2: P2, p3: P3, p4: P4, collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3, p4), collapsed=collapsed, block=block)
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, P4, P5, R> Console.grouping(operation: kotlin.reflect.KFunction5<P1, P2, P3, P4, P5, R>, p2: P2, p3: P3, p4: P4, p5: P5, collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3, p4, p5), collapsed=collapsed, block=block)
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, R> Console.grouping(operation: kotlin.reflect.KSuspendFunction1<P1, R>, collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = emptyArray(), collapsed=collapsed, block=block)
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, R> Console.grouping(operation: kotlin.reflect.KSuspendFunction2<P1, P2, R>, p2: P2, collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2), collapsed=collapsed, block=block)
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, R> Console.grouping(operation: kotlin.reflect.KSuspendFunction3<P1, P2, P3, R>, p2: P2, p3: P3, collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3), collapsed=collapsed, block=block)
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, P4, R> Console.grouping(operation: kotlin.reflect.KSuspendFunction4<P1, P2, P3, P4, R>, p2: P2, p3: P3, p4: P4, collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3, p4), collapsed=collapsed, block=block)
/** Runs the specified [block] wrapped by an optionally [collapsed] group with the specified [operation] as its label. */ @Suppress("LongLine") public inline fun <reified P1, P2, P3, P4, P5, R> Console.grouping(operation: kotlin.reflect.KSuspendFunction5<P1, P2, P3, P4, P5, R>, p2: P2, p3: P3, p4: P4, p5: P5, collapsed: Boolean = CONSOLE_DEFAULT_COLLAPSED, block: () -> R): R = grouping(type=P1::class, operation=operation.name, args = arrayOf(p2, p3, p4, p5), collapsed=collapsed, block=block)
// @formatter:on

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
