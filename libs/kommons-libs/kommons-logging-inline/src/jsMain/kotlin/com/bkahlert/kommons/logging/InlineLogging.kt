package com.bkahlert.kommons.logging

import kotlin.properties.PropertyDelegateProvider
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

/**
 * Factory for instances of [InlineLogger].
 */
public object InlineLogging {

    public fun logger(name: String): InlineLogger = InlineLogger(name)

    /**
     * Returns a logger property of which the name is derived from
     * the owning class,
     * respectively the companion object's owning class, or if missing,
     * the file class.
     */
    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Lazy<InlineLogger> =
        invoke().provideDelegate(thisRef, property)

    /**
     * Provides [InlineLogger] instances with the specified [name].
     */
    public operator fun invoke(name: String? = null, init: (InlineLogger) -> Unit = {}): PropertyDelegateProvider<Any?, Lazy<InlineLogger>> =
        LazyLoggerPropertyDelegateProvider { thisRef ->
            InlineLogger(
                name ?: thisRef.loggerName(::provideDelegate)
            ).apply(init)
        }

    /** Returns the name for logging using the specified [fn] to compute the subject in case `this` object is `null`. */
    private fun Any?.loggerName(@Suppress("UNUSED_PARAMETER") fn: KFunction<*>): String =
        when (this) {
            null -> caller("loggerName", "provideDelegate") ?: "<global>"
            else -> this::class.js.name
        }

    @Suppress("NOTHING_TO_INLINE") // inline to avoid impact on stack trace
    private inline fun caller(vararg callers: String): String? {
        val callerPatterns = callers.flatMap { it.patterns() }

        val stackTraceItem = stackTrace()
            .dropWhile { callerPatterns.any { pattern -> it.contains(pattern) } }
            .firstOrNull()

        return stackTraceItem
            ?.replaceFirst(Regex("\\s*at\\s*"), "")
            ?.split('.', ' ', limit = 2)
            ?.first()
            ?.replace(Regex("(.*_kt)_[a-z0-9]+$")) {
                it.groupValues[1]
            }
    }

    private fun String.patterns() = listOf(".$this", " $this", "$this@")

    @Suppress("NOTHING_TO_INLINE") // inline to avoid impact on stack trace
    private inline fun stackTrace() = try {
        throw RuntimeException()
    } catch (ex: Throwable) {
        ex.stackTraceToString().removeSuffix("\n")
    }.lineSequence()
        .dropWhile { it.startsWith("RuntimeException") || it.startsWith("captureStack") }
}

private class LazyLoggerPropertyDelegateProvider(private val initializer: (Any?) -> InlineLogger) : PropertyDelegateProvider<Any?, Lazy<InlineLogger>> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>): Lazy<InlineLogger> = lazy { initializer(thisRef) }
}
