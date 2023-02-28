package com.bkahlert.hello.app.ui

import com.bkahlert.kommons.text.simpleKebabCasedName

/** The state of a UI element. */
public sealed class UiState<out T> {

    public data class Loading(
        public val name: String,
    ) : UiState<Nothing>() {
        public companion object {
            public inline operator fun <reified T : Any> invoke(): Loading =
                Loading(T::class.simpleKebabCasedName?.replace('-', ' ') ?: "unknown")
        }
    }

    public data class Loaded<out T>(
        /** The successfully loaded data. */
        public val model: T,
    ) : UiState<T>()

    public data class Failed(
        /** The cause of this failed state. */
        val cause: Throwable,
    ) : UiState<Nothing>()
}
