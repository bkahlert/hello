package com.bkahlert.kommons.dom

import com.bkahlert.hello.url.ParametersPropertyDelegate
import com.bkahlert.hello.url.ParametersPropertyDelegateProvider
import com.bkahlert.hello.url.binding
import io.ktor.http.Parameters
import org.w3c.dom.Location
import kotlin.reflect.KProperty

/** Returns a property that binds to hash/fragments [Parameters] of `this` location. */
inline operator fun Location.provideDelegate(thisRef: Any?, property: KProperty<*>): ParametersPropertyDelegate<String?> =
    binding<String?>(this::fragment, null).provideDelegate(thisRef, property)

/** Returns a property that binds to hash/fragments [Parameters] of `this` location using the specified [default]. */
inline infix fun <reified V> Location.default(default: V): ParametersPropertyDelegateProvider<V> =
    binding(this::fragment, default)

/** Returns a property that binds to hash/fragments [Parameters] of `this` location using the specified [defaults]. */
inline infix fun <reified V> Location.defaults(defaults: Pair<V, V>): ParametersPropertyDelegateProvider<V> =
    binding(this::fragment, defaults.first, defaults.second)
