package com.bkahlert.aws.lambda

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty

public object SLF4J {

    @Suppress("KotlinLoggerInitializedWithForeignClass")
    public operator fun <T : Any> provideDelegate(thisRef: T, property: KProperty<*>): Lazy<Logger> =
        lazy { LoggerFactory.getLogger(thisRef::class.java) }
}
