package com.bkahlert.hello.url

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

/**
 * Builds [Parameters] with the optional [parameters]
 * as a starting point and the specified [init].
 */
public fun buildParameters(parameters: Parameters = EmptyParameters, init: ParametersBuilder.() -> Unit): Parameters =
    buildParameters(block = {
        parameters.forEach { key, values -> appendAll(key, values) }
        init()
    })

internal expect fun buildParameters(block: ParametersBuilder.() -> Unit): Parameters

public expect interface ParametersBuilder {
    public fun append(name: String, value: String)
    public fun appendAll(name: String, values: Iterable<String>)
}


/**
 * Appends the specified [name] without any values.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun ParametersBuilder.append(name: String): Unit = appendAll(name, emptyList())

/**
 * Minus operator function that creates a new parameters instance from the original without the specified [name].
 */
@Deprecated("really needed?")
public operator fun Parameters.minus(name: String): Parameters =
    buildParameters {
        this@minus.names().filter { it != name }.forEach { appendAll(it, this@minus.getAll(it) ?: emptyList()) }
    }

/**
 * Returns the first valid value for the specified [name],
 * or returns the specified [default] if no value could be deserialized,
 * or returns the specified [missing] if [name] is not present at all.
 */
@Deprecated("really needed?")
public fun <T> Parameters.getValid(
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
@Deprecated("really needed?")
public inline fun <reified T> Parameters.getValid(
    name: String,
    default: T,
    missing: T = default,
): T = getValid(name, default, missing) { Json.Default.decodeFromJsonElement(JsonPrimitive(it)) }


/**
 * Property delegate that delegates to the specified [parametersProperty]
 * using the specified [name] to access the [Parameters] instance's values.
 * @see [StringValues.getValid]
 */
@Deprecated("really needed?")
public class ParametersPropertyDelegate<V>(
    public val parametersProperty: KMutableProperty0<Parameters>,
    public val name: String,
    public val default: V,
    public val missing: V = default,
    public val serialize: (V) -> String,
    public val deserialize: (String) -> V,
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
@Deprecated("really needed?")
public inline fun <reified V> binding(
    parametersProperty: KMutableProperty0<Parameters>,
    default: V,
    missing: V = default,
    noinline serialize: (V) -> String = when (V::class) {
        String::class -> ({ it as String })
        else -> ({ Json.Default.encodeToString(it) })
    },
    noinline deserialize: (String) -> V = when (V::class) {
        String::class -> ({ it as V })
        else -> ({ Json.Default.decodeFromString(it) })
    },
): ParametersPropertyDelegateProvider<V> = ParametersPropertyDelegateProvider(parametersProperty, default, missing, serialize, deserialize)

/** Provider for instances of [ParametersPropertyDelegate]. */
public class ParametersPropertyDelegateProvider<V>(
    public val parametersProperty: KMutableProperty0<Parameters>,
    public val default: V,
    public val missing: V,
    public val serialize: (V) -> String,
    public val deserialize: (String) -> V,
) : PropertyDelegateProvider<Any?, ParametersPropertyDelegate<V>> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>): ParametersPropertyDelegate<V> {
        return ParametersPropertyDelegate(parametersProperty, property.name, default, missing, serialize, deserialize)
    }
}

/** Returns a property that binds to `this` [Parameters] property. */
@Deprecated("really needed?")
public operator fun KMutableProperty0<Parameters>.provideDelegate(thisRef: Any?, property: KProperty<*>): ParametersPropertyDelegate<String?> =
    binding<String?>(this, null).provideDelegate(thisRef, property)


/**
 * Returns the parameters URL encoded, that is,
 * entries with no value become `key` (which isn't the case for Ktor's formUrlEncode),
 * and entries with value become `key=value`.
 */
public fun Parameters.formUrlEncode(): String = entries()
    .flatMap { (key, values) -> if (values.isNotEmpty()) values.map { key to it } else listOf(key to null) }
    .formUrlEncode()

public expect interface Parameters {

    /** Gets all values associated with the [name], or null if the name is not present */
    public fun getAll(name: String): List<String>?

    /** Gets all names from the map */
    public fun names(): Set<String>

    /** Gets all entries from the map */
    public fun entries(): Set<Map.Entry<String, List<String>>>

    /** Checks if this map is empty */
    public fun isEmpty(): Boolean
}

/** Gets first value from the list of values associated with a [name], or null if the name is not present */
public operator fun Parameters.get(name: String): String? = getAll(name)?.firstOrNull()

/** Iterates over all entries in this map and calls [block] for each pair */
public fun Parameters.forEach(block: (String, List<String>) -> Unit): Unit = entries().forEach { (k, v) -> block(k, v) }

internal expect fun List<Pair<String, String?>>.formUrlEncode(): String

internal expect val EmptyParameters: Parameters

internal expect fun parseQueryString(query: String, startIndex: Int = 0, limit: Int = 1000, decode: Boolean = true): Parameters
