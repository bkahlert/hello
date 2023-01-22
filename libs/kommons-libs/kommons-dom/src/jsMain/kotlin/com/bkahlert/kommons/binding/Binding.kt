package com.bkahlert.kommons.binding

import kotlin.reflect.KProperty

/**
 * A binding that reads and writes it's [value]
 * from some source and notifies listeners about value changes.
 */
public interface Binding<T> {
    /** The value of this binding. */
    public var value: T

    /** Adds the specified listener to the listeners informed about value changes. */
    public fun addValueChangeListener(listener: BindingValueChangeListener<T>)

    /** Removed the specified listener from the listeners informed about value changes. */
    public fun removeValueChangeListener(listener: BindingValueChangeListener<T>)

    /** Permits property delegation of `val`s using `by` for this binding. */
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    /** Permits property delegation of `var`s using `by` for this binding. */
    public operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

/**
 * An adapter that adapts the [binding] with [T]-typed values to
 * a binding with [R]-typed values using [to], and the optional [checkEquality].
 */
public data class BindingAdapter<T, R>(
    /** The adapted binding */
    public val binding: Binding<T>,
    /** Adapts a [T]-typed value to a [R]-typed value. */
    public val from: (T) -> R,
    /** Adapts back a [R]-typed value to a [T]-typed value. */
    public val to: (R) -> T,
    /** Whether to trigger [addValueChangeListener] if the adapted values aren't equals. */
    public val checkEquality: Boolean = true,
) : Binding<R> {
    override var value: R
        get() = from(binding.value)
        set(value) {
            binding.value = to(value)
        }

    private val listenerAdapters: MutableMap<BindingValueChangeListener<R>, BindingValueChangeListener<T>> = mutableMapOf()

    override fun addValueChangeListener(listener: BindingValueChangeListener<R>) {
        val listenerAdapter: BindingValueChangeListener<T> = { oldValue, newValue ->
            val oldAdaptedValue = from(oldValue)
            val newAdaptedValue = from(newValue)
            if (!checkEquality || oldAdaptedValue != newAdaptedValue) {
                listener(oldAdaptedValue, newAdaptedValue)
            }
        }
        listenerAdapters[listener] = listenerAdapter
        binding.addValueChangeListener(listenerAdapter)
    }

    override fun removeValueChangeListener(listener: BindingValueChangeListener<R>) {
        when (val listenerAdapter = listenerAdapters.remove(listener)) {
            null -> console.warn("Unknown listener $listener")
            else -> binding.removeValueChangeListener(listenerAdapter)
        }
    }
}

/**
 * Returns a binding with [R]-typed values using the
 * specified [from] and [to] to adapt [Binding.value].
 */
public fun <T, R> Binding<T>.adapt(
    from: (T) -> R,
    to: (R) -> T,
    checkEquality: Boolean = true
): Binding<R> = BindingAdapter(this, from, to, checkEquality)

/** A listener that is notified about value changes. */
public typealias BindingValueChangeListener<T> = (oldValue: T, newValue: T) -> Unit
