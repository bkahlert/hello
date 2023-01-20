package com.bkahlert.kommons.dom

import com.bkahlert.kommons.net.Uri
import com.bkahlert.kommons.net.build
import com.bkahlert.kommons.net.formUrlEncode
import com.bkahlert.kommons.net.fragmentParameters
import com.bkahlert.kommons.net.queryParameters
import io.ktor.http.Parameters
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.w3c.dom.Location
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty


/** The [URL] of `this` location. */
var Location.uri: Uri
    get() = Uri.parse(href)
    set(value) {
        href = value.toString()
    }

/** The query [Parameters] of `this` location. */
var Location.query: Parameters
    get() = uri.queryParameters
    set(value) {
        search = value.formUrlEncode(keepEmptyValues = true)
    }

/** The hash/fragment [Parameters] of `this` location. */
var Location.fragment: Parameters
    get() = uri.fragmentParameters
    set(value) {
        hash = value.formUrlEncode(keepEmptyValues = true)
    }

/**
 * A property delegate that is backed by the [Parameters]
 * handled by [get] and [set], and which converts values
 * using [serialize] and [deserialize].
 */
class ParametersPropertyDelegate<V>(
    val get: () -> Parameters,
    val set: (Parameters) -> Unit,
    val serialize: (V) -> String,
    val deserialize: (String) -> V,
) : ReadWriteProperty<Nothing?, List<V>?> {
    override fun getValue(thisRef: Nothing?, property: KProperty<*>): List<V>? =
        get().getAll(property.name)?.mapNotNull { kotlin.runCatching { deserialize(it) }.getOrNull() }

    override fun setValue(thisRef: Nothing?, property: KProperty<*>, value: List<V>?) {
        set(Parameters.build(get()) {
            remove(property.name)
            if (value != null) {
                appendAll(property.name, value.mapNotNull { kotlin.runCatching { serialize(it) }.getOrNull() })
            }
        })
    }
}

inline fun <reified V : Any> ParametersPropertyDelegate(
    property: KMutableProperty0<Parameters>,
    noinline serialize: (V) -> String,
    noinline deserialize: (String) -> V,
): ParametersPropertyDelegate<V> = ParametersPropertyDelegate(
    property::get,
    property::set,
    serialize,
    deserialize,
)

/**
 * ```kotlin
 * var debug: List<Int> by window.location::fragment / Json
 * ```
 */
inline operator fun <reified V : Any> KMutableProperty0<Parameters>.div(
    stringFormat: StringFormat,
): ReadWriteProperty<Nothing?, List<V>?> = ParametersPropertyDelegate(
    this,
    { stringFormat.encodeToString(it) },
    { stringFormat.decodeFromString(it) },
)
