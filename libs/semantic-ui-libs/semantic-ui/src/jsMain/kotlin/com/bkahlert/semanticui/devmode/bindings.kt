package com.bkahlert.semanticui.devmode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.bkahlert.kommons.binding.Binding
import com.bkahlert.kommons.binding.BindingValueChangeListener

/** Returns a [MutableState] that is backed by this binding. */
@Composable
public fun <T> Binding<T>.asMutableState(): MutableState<T> {
    val binding = this
    val mutableState: MutableState<T> = mutableStateOf(binding.value)
    DisposableEffect(binding) {
        val listener: BindingValueChangeListener<T> = { _, newValue -> mutableState.value = newValue }
        addValueChangeListener(listener)
        onDispose {
            removeValueChangeListener(listener)
        }
    }
    return object : MutableState<T> by mutableState {
        override var value: T
            get() = mutableState.value
            set(value) {
                // Populate changes to the binding and
                // let the bindings' value change listener
                // update the state
                binding.value = value
            }
    }
}

/** Returns a [State] that is backed by this binding. */
@Composable
public fun <T> Binding<T>.asState(): State<T> = asMutableState()
