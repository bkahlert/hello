package com.bkahlert.hello.app.ui

import com.bkahlert.hello.user.domain.User

public sealed interface UiState {

    public data class Loading(
        public val models: List<String>,
    ) : UiState {
        public constructor(vararg models: String) : this(models.asList())
    }

    public data class Loaded(
        public val user: User?,
    ) : UiState

    public data class Failed(
        /** The name of the failed operation. */
        val operation: String,
        /** The cause of this failed state. */
        val cause: Throwable,
    ) : UiState
}
