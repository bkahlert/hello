package com.bkahlert.semanticui.devmode

import com.bkahlert.kommons.binding.Binding
import com.bkahlert.kommons.binding.BindingValueChangeListener
import com.bkahlert.kommons.net.Uri
import com.bkahlert.kommons.net.build
import com.bkahlert.kommons.net.formUrlEncode
import com.bkahlert.kommons.net.fragmentParameters
import com.bkahlert.kommons.net.toUri
import io.ktor.http.Parameters
import org.w3c.dom.HashChangeEvent
import org.w3c.dom.Location
import org.w3c.dom.Window
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.properties.PropertyDelegateProvider

// TODO move to kommons-dom
// This file has nothing to do in this module but can't be moved to
// kommons-dom due to it's Uri dependency.
// Uri should be moved to a new kommons-uri.
// Both is not possible due to Kotlin Multiplatforms inexistent support
// for Gradle's includeBuild feature.

/**
 * A binding to the [window]'s [Location.href]
 * fragment parameter with the specified [name].
 */
public class LocationFragmentParameterBinding(
    /** Name of the fragment parameters. */
    public val name: String,
    /** Bound window. */
    public val window: Window = kotlinx.browser.window,
) : Binding<List<String>?> {

    /**
     * The values of the fragment parameter [name] with
     * - each value corresponding to one parameter occurrence (for example `#name=value1&name=value2`),
     * - an empty list representing a fragment parameter [name] with no value (for example `#name`), and
     * - `null` representing no fragment parameters with [name] at all.
     */
    override var value: List<String>?
        get() = fragmentParameterValuesOfUri(window.location.href, name)
        set(value) {
            val oldParameters = fragmentParametersOfUri(window.location.href)
            val newParameters = Parameters.build(oldParameters) {
                remove(name)
                if (value != null) appendAll(name, value)
            }
            window.location.hash = newParameters.formUrlEncode(keepEmptyValues = true)
        }

    private val listeners: MutableSet<BindingValueChangeListener<List<String>?>> = mutableSetOf()

    private val hashChangeEventListener: EventListener = object : EventListener {
        override fun handleEvent(event: Event) {
            val hashChangeEvent = event as HashChangeEvent
            val oldValues = fragmentParameterValuesOfUri(hashChangeEvent.oldURL, name)
            val newValues = fragmentParameterValuesOfUri(hashChangeEvent.newURL, name)
            if (oldValues != newValues) {
                listeners.forEach { it(oldValues, newValues) }
            }
        }
    }

    override fun addValueChangeListener(listener: BindingValueChangeListener<List<String>?>) {
        if (listeners.isEmpty()) {
            window.addEventListener(type = "hashchange", callback = hashChangeEventListener)
        }
        listeners.add(listener)
    }

    override fun removeValueChangeListener(listener: BindingValueChangeListener<List<String>?>) {
        listeners.remove(listener)
        if (listeners.isEmpty()) {
            window.removeEventListener(type = "hashchange", callback = hashChangeEventListener)
        }
    }

    public companion object {
        /** Returns the [Uri.fragmentParameters] of the specified [uri]. */
        public fun fragmentParametersOfUri(uri: String): Parameters = uri.toUri().fragmentParameters

        /** Returns the values for the fragment parameter [name] of the specified [uri]. */
        public fun fragmentParameterValuesOfUri(uri: String, name: String): List<String>? = fragmentParametersOfUri(uri).getAll(name)
    }
}

/**
 * Returns a [LocationFragmentParameterBinding] bound
 * to this window and the fragment parameter with the specified [name].
 */
public fun Window.bindFragmentParameter(
    name: String,
    onValueChange: BindingValueChangeListener<List<String>?>? = null,
): LocationFragmentParameterBinding = LocationFragmentParameterBinding(name, this).apply {
    if (onValueChange != null) addValueChangeListener(onValueChange)
}

/**
 * Returns a property provider delegate that provides
 * [LocationFragmentParameterBinding] instances bound
 * to this window and the fragment parameter with the name of the property.
 */
public fun Window.bindFragmentParameter(
    onValueChange: BindingValueChangeListener<List<String>?>? = null,
): PropertyDelegateProvider<Any?, LocationFragmentParameterBinding> = PropertyDelegateProvider { _, property ->
    bindFragmentParameter(property.name, onValueChange)
}
