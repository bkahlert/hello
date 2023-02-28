package com.bkahlert.hello.app.ui

import com.bkahlert.hello.user.domain.User

public sealed interface AppViewModelState {
    public data class Loading(
        public val models: List<String>,
    ) : AppViewModelState {
        public constructor(vararg models: String) : this(models.asList())
    }

    public data class Loaded(
        public val user: User?,
    ) : AppViewModelState

    public data class Failed(
        /** The name of the failed operation. */
        val operation: String,
        /** The cause of this failed state. */
        val cause: Throwable,
    ) : AppViewModelState
}
