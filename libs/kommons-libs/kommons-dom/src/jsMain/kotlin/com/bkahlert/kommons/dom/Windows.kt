package com.bkahlert.kommons.dom

import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.formUrlEncode
import com.bkahlert.kommons.uri.fragmentParameters
import com.bkahlert.kommons.uri.toUri
import io.ktor.http.Parameters
import io.ktor.util.StringValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.w3c.dom.HashChangeEvent
import org.w3c.dom.Location
import org.w3c.dom.Window
import kotlin.collections.Map.Entry
import kotlin.reflect.KProperty

public inline fun Window.open(
    url: CharSequence,
    target: String? = null,
    features: String? = null,
): Window? {
    val urlString = url.toString()
    return if (target != null) {
        if (features != null) {
            open(urlString, target, features)
        } else {
            open(urlString, target)
        }
    } else {
        open(urlString)
    }
}

public inline fun Window.openInSameTab(
    url: CharSequence,
    features: String? = null,
    newTabFallback: Boolean = true,
): Window? =
    runCatching {
        open(url, "_top", features)
    }.onFailure {
        if (newTabFallback) openInNewTab(url, features)
        else throw it
    }.getOrThrow()

public inline fun Window.openInNewTab(
    url: CharSequence,
    features: String? = null,
): Window? = open(url, "_blank", features)

/** The [Location.href] represented as a [Uri]. */
public var Location.uri: Uri
    get() = href.toUri()
    set(value) {
        href = value.toString()
    }

/** The [Location.hash] represented as [Parameters]. */
public var Location.fragmentParameters: StringValues
    get() = uri.fragmentParameters
    set(value) {
        hash = value.formUrlEncode(keepEmptyValues = true)
    }

/** The state of [Uri.fragmentParameters]. */
public interface FragmentParameters : StringValues {

    /**
     * Returns all values of the fragment parameter with the specified [name]:
     * - each value corresponds to one parameter occurrence (for example `#name=value1&name=value2`)
     * - an empty list represents a fragment parameter [name] with no value (for example `#name`)
     * - `null` represents no fragment parameters with [name] at all
     */
    public override fun getAll(name: String): List<String>?

    /**
     * Sets the values of the fragment parameter with the specified [name] to the specified [value].
     */
    public operator fun set(name: String, value: String?) {
        setAll(name, value?.let { listOf(it) })
    }

    /**
     * Sets the specified [values] of the fragment parameter with the specified [name]:
     * - each value corresponds to one parameter occurrence (for example `#name=value1&name=value2`)
     * - an empty list represents a fragment parameter [name] with no value (for example `#name`)
     * - `null` represents no fragment parameters with [name] at all
     */
    public fun setAll(name: String, values: List<String>?)

    /**
     * Sets the values of the fragment parameter with the specified name of the delegated [property]:
     * - each value corresponds to one parameter occurrence (for example `#name=value1&name=value2`)
     * - an empty list represents a fragment parameter with no value (for example `#name`)
     * - `null` represents no fragment parameters with name at all
     */
    public operator fun setValue(thisRef: Any?, property: KProperty<*>, value: List<String>?) {
        setAll(property.name, value)
    }

    /**
     * Returns a cold [Flow] that starts emitting the fragment parameters state as [StringValues] instances
     * the moment it's collected until the flow collection is cancelled.
     */
    public fun asFlow(): Flow<StringValues>

    /**
     * Returns a cold [Flow] that starts emitting the state of the fragment parameter with the specified [name]
     * the moment it's collected until the flow collection is cancelled.
     */
    public fun asFlow(name: String): Flow<List<String>?> =
        asFlow().map { it.getAll(name) }

    /**
     * Returns a *hot* [StateFlow] that is started in the given coroutine [scope],
     * sharing the most recently emitted fragment parameters state as a [StringValues] instance.
     */
    public fun asStateFlow(
        scope: CoroutineScope,
        started: SharingStarted,
    ): StateFlow<StringValues> =
        asFlow().stateIn(scope, started, this)

    /**
     * Returns a *hot* [StateFlow] that is started in the given coroutine [scope],
     * sharing the most recently emitted state of the fragment parameter with the specified [name].
     */
    public fun asStateFlow(
        scope: CoroutineScope,
        started: SharingStarted,
        name: String,
    ): StateFlow<List<String>?> =
        asFlow(name).stateIn(scope, started, getAll(name))
}

/** The state of a [Window.location]'s [Uri.fragmentParameters]. */
public class LocationFragmentParameters(
    /** Window of which the [Location]'s [Uri.fragmentParameters] belong to. */
    private val window: Window,
) : FragmentParameters {
    private val logger by ConsoleLogging("hello.location")

    override val caseInsensitiveName: Boolean get() = window.location.fragmentParameters.caseInsensitiveName
    override fun isEmpty(): Boolean = window.location.fragmentParameters.isEmpty()
    override fun entries(): Set<Entry<String, List<String>>> = window.location.fragmentParameters.entries()
    override fun names(): Set<String> = window.location.fragmentParameters.names()
    override fun getAll(name: String): List<String>? = window.location.fragmentParameters.getAll(name)
    override fun setAll(name: String, values: List<String>?) {
        window.location.fragmentParameters = StringValues.build {
            appendAll(window.location.fragmentParameters)
            remove(name)
            if (values != null) appendAll(name, values)
        }
    }

    /**
     * Returns a cold [Flow] that starts emitting the fragment parameters state as [StringValues] instances
     * the moment it's collected until the flow collection is cancelled.
     */
    override fun asFlow(): Flow<StringValues> = logger.grouping(::asFlow, window) {
        window
            .asEventFlow<HashChangeEvent>()
            .map { it.newURL.toUri().fragmentParameters }
    }
}
