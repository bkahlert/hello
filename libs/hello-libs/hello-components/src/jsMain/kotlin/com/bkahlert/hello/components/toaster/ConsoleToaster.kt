package com.bkahlert.hello.components.toaster

import com.bkahlert.hello.font.FontFamilies
import com.bkahlert.kommons.errorMessage
import com.bkahlert.kommons.js.Console
import dev.fritz2.core.fill
import dev.fritz2.core.viewBox
import dev.fritz2.headless.components.Toast
import net.pearx.kasechange.universalWordSplitter
import org.w3c.dom.HTMLLIElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

/**
 * A [Toaster] that listens to [console] messages.
 */
public open class ConsoleToaster(
    vararg consoleFns: Pair<String, Duration> = arrayOf(
        "error" to 1.days,
        "warn" to 1.hours,
        "info" to 10.seconds,
        "log" to 10.seconds,
    ),
    parse: ConsoleMessageParser = DebugConsoleMessageParser(),
    render: ConsoleMessageRenderer = DefaultConsoleMessageRenderer(),
) : Toaster<ConsoleMessage>(render = render) {
    init {
        com.bkahlert.kommons.js.console.tee(*consoleFns.map { it.first }.toTypedArray()) { consoleFn, args ->
            val consoleMessage = parse(consoleFn, args)
            if (consoleMessage != null) toast(
                data = consoleMessage,
                duration = consoleFns.firstNotNullOfOrNull { (fn, duration) -> duration.takeIf { fn == consoleFn } } ?: 10.seconds,
            )
        }
    }
}

/**
 * A captured console message.
 */
public data class ConsoleMessage(
    /** Name of the console function that was called. */
    public val consoleFn: String,
    /** The message itself. */
    public val message: String,
    /** Name of the unit the [message] relates to. */
    public val logger: String?,
    /** Color of the unit the [message] relates to. */
    public val color: String?,
)

/** Parser for [ConsoleMessage]s. */
public typealias ConsoleMessageParser = (consoleFn: String, args: Array<out Any?>) -> ConsoleMessage?

/** Renderer for [ConsoleMessage]s. */
public typealias ConsoleMessageRenderer = Toast<HTMLLIElement>.(ConsoleMessage) -> Unit

/** [Console.log] function not processed by [ConsoleToaster]. */
private val log: (Array<out Any?>) -> Unit = js("console.log.bind(console)") as Function1<Array<out Any?>, Unit>

@Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER", "UNUSED_ANONYMOUS_PARAMETER")
private fun Any.tee(vararg fns: String, target: (String, Array<out Any?>) -> Unit) {
    val self = this
    fns.forEach { fn ->
        @Suppress("UnsafeCastFromDynamic")
        js("self[fn] = (function(o) { return function() { target(o.name, Array.from(arguments)); o.apply(this, Array.from(arguments)); }; })(self[fn]);")
    }
}


/**
 * Returns a [ConsoleMessageParser] that parses the console message
 * generates by [debug](https://github.com/debug-js/debug).
 */
public fun DebugConsoleMessageParser(
    ignoredPatterns: List<Regex> = listOf(
        Regex("^TRACE:.*$"),
        Regex("^⏎.*$"),
        Regex("^\\[webpack-.*$"),
        Regex("^\\[HMR.*$"),
    ),
    formatLogger: (String?) -> String? = { it },
): ConsoleMessageParser = { consoleFn, args ->
    val exception: Throwable? = args.firstNotNullOfOrNull { it as? Throwable }

    val formatPattern = "(?:%\\w)+" // for example, %c
    val formatRegex = Regex(formatPattern)
    val formattedMessage: String = exception?.errorMessage ?: args
        .mapNotNull {
            it?.toString().orEmpty()
                .replace(formatRegex, "")
                .replace("\r\n", " ")
                .replace("\r", " ")
                .replace("\n", " ")
                .takeUnless(String::isEmpty)
        }
        .joinToString(" ")

    if (ignoredPatterns.none { it.matches(formattedMessage) }) {
        val (logger: String?, message: String?) = exception
            ?.let { null to it.errorMessage }
            ?: args.firstNotNullOfOrNull { it as? String }
                ?.replace(Regex("\\s+\\+?\\d+\\w+$"), "") // remove +20ms
                ?.replace(Regex("^$formatPattern"), "") // remove %c from start
                ?.replace(Regex("$formatPattern$"), "") // remove %c from end
                ?.split(formatRegex, limit = 2)
                ?.let {
                    when (it.size) {
                        2 -> it[0].substringBefore('@') to it[1].replace(Regex("(?:%\\w)+"), "")
                        else -> null to it[0].replace(Regex("(?:%\\w)+"), "")
                    }
                }
            ?: (null to null)
        val color = args.firstNotNullOfOrNull { (it as? String).orEmpty().substringAfter("color: ", "").takeUnless { it.isEmpty() } }
        if (message != null) ConsoleMessage(consoleFn, message, formatLogger(logger), color)
        else null
    } else {
        null
    }
}


/**
 * Returns a [DefaultConsoleMessageRenderer] that renders
 * `error` and `warn` logs different from `log` and `info`.
 */
public fun DefaultConsoleMessageRenderer(customize: Toast<HTMLLIElement>.(ConsoleMessage) -> Unit = {}): ConsoleMessageRenderer =
    {
        when (it.consoleFn) {
            "error" -> {
                className("bg-red-500/30 dark:bg-red-500/50")
                div("flex gap-2 items-start") {
                    div("flex-1 flex flex-col gap-1 overflow-hidden") {
                        it.logger?.also { div("font-semibold truncate") { +it } }
                        div { +it.message }
                    }
                }
            }

            "warn" -> {
                className("bg-yellow-500/30 dark:bg-yellow-500/50")
                div("flex gap-2 items-start") {
                    div("flex-1 flex flex-col gap-1 overflow-hidden") {
                        it.logger?.also { div("font-semibold truncate") { +it } }
                        div { +it.message }
                    }
                }
            }

            else -> {
                val initials = it.logger?.let { universalWordSplitter().splitToWords(it.replace(':', ' ')).take(2) }.orEmpty().let { words ->
                    log(arrayOf("B"))
                    when (words.size) {
                        0 -> null
                        1 -> words[0].take(2)
                        else -> words.filterNot(String::isBlank).joinToString("") { it.first().toString() }
                    }
                }
                div("flex gap-2 items-start") {
                    if (initials != null) {
                        svg("w-5 h-5 shrink-0") {
                            attr("xmlns", "http://www.w3.org/2000/svg")
                            viewBox("0 0 24 24")
                            fill("currentColor")
                            attr("stroke", "currentColor")
                            domNode.innerHTML =
                                """
                        <circle fill="${it.color ?: "transparent"}" fill-opacity=".6" stroke-linejoin="round" stroke-width="1.5" cx="12" cy="12" r="11"></circle>
                        <text x="50%" y="50%" stroke="none" text-anchor="middle" dy=".25rem"
                            font-family="${FontFamilies.SYSTEM_UI}" font-size=".7rem"
                            >$initials</text>
                        """.trimIndent()
                        }
                    }
                    div("flex-1 flex flex-col gap-1 overflow-hidden") {
                        it.logger?.also { div("font-semibold truncate") { +it } }
                        div { +it.message }
                    }
                }
            }
        }
        customize(it)
    }
