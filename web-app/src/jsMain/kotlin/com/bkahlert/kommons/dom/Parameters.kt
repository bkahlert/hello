package com.bkahlert.kommons.dom

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.text.toKebabCasedString
import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.formUrlEncode
import io.ktor.util.StringValues
import io.ktor.util.StringValuesBuilder
import org.w3c.dom.Location
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import io.ktor.http.formUrlEncode as ktorFormUrlEncode

/**
 * Builds [Parameters] with the optional [parameters]
 * as a starting point and the specified [init].
 */
fun buildParameters(parameters: Parameters = Parameters.Empty, init: ParametersBuilder.() -> Unit) =
    Parameters.build {
        parameters.forEach { key, values -> appendAll(key, values) }
        init()
    }

/**
 * Appends the specified [name] without any values.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun StringValuesBuilder.append(name: String) = appendAll(name, emptyList())

/**
 * Minus operator function that creates a new parameters instance from the original without the specified [name].
 */
operator fun Parameters.minus(name: String): Parameters =
    Parameters.build {
        this@minus.names().filter { it != name }.forEach { appendAll(it, this@minus.getAll(it) ?: emptyList()) }
    }

/**
 * Returns the first valid value for the specified [name],
 * or returns the specified [default] if no value could be deserialized,
 * or returns the specified [missing] if [name] is not present at all.
 */
fun <T> StringValues.getValid(
    name: String,
    default: T,
    missing: T = default,
    deserialize: (String) -> T,
): T = getAll(name).let {
    if (it == null) missing
    else {
        for (value in it) {
            val deserialized = kotlin.runCatching { deserialize(value) }
            if (deserialized.isSuccess) return deserialized.getOrThrow()
        }
        default
    }
}

/**
 * Returns the first valid value for the specified [name],
 * or returns the specified [default] if no value could be deserialized,
 * or returns the specified [missing] if [name] is not present at all.
 */
inline fun <reified T> StringValues.getValid(
    name: String,
    default: T,
    missing: T = default,
): T = getValid(name, default, missing) { it.deserialize() }

/**
 * Returns the parameters URL encoded, that is,
 * entries with no value become `key` (which is not the case for [Parameters.ktorFormUrlEncode]),
 * and entries with value become `key=value`.
 */
fun Parameters.formUrlEncode(): String = entries()
    .flatMap { (key, values) -> if (values.isNotEmpty()) values.map { key to it } else listOf(key to null) }
    .formUrlEncode()

/**
 * Property delegate that delegates to the specified [parametersProperty]
 * using the specified [name] to access the [Parameters] instance's values.
 * @see [StringValues.getValid]
 */
class ParametersPropertyDelegate<V>(
    val parametersProperty: KMutableProperty0<Parameters>,
    val name: String,
    val default: V,
    val missing: V = default,
    val serialize: (V) -> String,
    val deserialize: (String) -> V,
) : ReadWriteProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V =
        parametersProperty.get().getValid(name, default, missing, deserialize)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        val updated = buildParameters(parametersProperty.get() - name) {
            when (value) {
                null -> {}
                default -> appendAll(name, emptyList())
                else -> append(name, serialize(value))
            }
        }
        parametersProperty.set(updated)
    }
}

/**
 * Returns a property that binds to the specified [parametersProperty]
 * using this property's name, defaulting to the specified [default].
 * @see [StringValues.getValid]
 */
inline fun <reified V> binding(
    parametersProperty: KMutableProperty0<Parameters>,
    default: V,
    missing: V = default,
    noinline serialize: (V) -> String = when (V::class) {
        String::class -> ({ it as String })
        else -> ({ it.serialize() })
    },
    noinline deserialize: (String) -> V = when (V::class) {
        String::class -> ({ it as V })
        else -> ({ it.deserialize() })
    },
) = ParametersPropertyDelegateProvider(parametersProperty, default, missing, serialize, deserialize)

/** Provider for instances of [ParametersPropertyDelegate]. */
class ParametersPropertyDelegateProvider<V>(
    val parametersProperty: KMutableProperty0<Parameters>,
    val default: V,
    val missing: V,
    val serialize: (V) -> String,
    val deserialize: (String) -> V,
) : PropertyDelegateProvider<Any?, ParametersPropertyDelegate<V>> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>): ParametersPropertyDelegate<V> {
        return ParametersPropertyDelegate(parametersProperty, property.name.toKebabCasedString(), default, missing, serialize, deserialize)
    }
}

/** Returns a property that binds to `this` [Parameters] property. */
operator fun KMutableProperty0<Parameters>.provideDelegate(thisRef: Any?, property: KProperty<*>): ParametersPropertyDelegate<String?> =
    binding<String?>(this, null).provideDelegate(thisRef, property)

/** Returns a property that binds to hash/fragments [Parameters] of `this` location. */
inline operator fun Location.provideDelegate(thisRef: Any?, property: KProperty<*>): ParametersPropertyDelegate<String?> =
    binding<String?>(this::fragment, null).provideDelegate(thisRef, property)

/** Returns a property that binds to hash/fragments [Parameters] of `this` location using the specified [default]. */
inline infix fun <reified V> Location.default(default: V): ParametersPropertyDelegateProvider<V> =
    binding(this::fragment, default)

/** Returns a property that binds to hash/fragments [Parameters] of `this` location using the specified [defaults]. */
inline infix fun <reified V> Location.defaults(defaults: Pair<V, V>): ParametersPropertyDelegateProvider<V> =
    binding(this::fragment, defaults.first, defaults.second)
