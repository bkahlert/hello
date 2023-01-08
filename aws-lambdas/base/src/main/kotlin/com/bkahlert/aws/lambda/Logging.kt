package com.bkahlert.aws.lambda

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.PropertyDelegateProvider
import kotlin.reflect.KProperty

public object SLF4J : PropertyDelegateProvider<Any?, Lazy<Logger>> {

    /**
     * Provides [Logger] instances with the name derived from the owning class.
     */
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>): Lazy<Logger> =
        invoke().provideDelegate(thisRef, property)

    /**
     * Provides [Logger] instances with the specified [name].
     */
    public operator fun invoke(name: String? = null, init: (Logger) -> Unit = {}): PropertyDelegateProvider<Any?, Lazy<Logger>> =
        LazyLoggerPropertyDelegateProvider { thisRef ->
            LoggerFactory.getLogger(
                name ?: when (thisRef) {
                    null -> "<unknown>"
                    else -> thisRef::class.java.name
                }
            ).apply(init)
        }
}

private class LazyLoggerPropertyDelegateProvider(private val initializer: (Any?) -> Logger) : PropertyDelegateProvider<Any?, Lazy<Logger>> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>): Lazy<Logger> = lazy { initializer(thisRef) }
}
