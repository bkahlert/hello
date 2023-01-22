package com.bkahlert.kommons.binding

import com.bkahlert.kommons.binding.DevModeState.Started
import com.bkahlert.kommons.binding.DevModeState.Stopped
import com.bkahlert.kommons.binding.DevModeState.Stopped.session

/**
 * Development mode that handles
 * the lifecycle of a [DevSession].
 */
public interface DevMode<out T : DevSession> {

    /** Current state */
    public val state: DevModeState<T>

    /** Whether a [DevSession] is running */
    public val started: Boolean get() = state is Started

    /** Starts a new [DevSession] if none is running yet. */
    public fun start(): Started<out T>

    /** Stops an eventually running [DevSession]. */
    public fun stop(): Stopped
}

/** State of a [DevMode] */
public sealed interface DevModeState<out T : DevSession> {
    /** The running session, if any. */
    public val session: T?

    /** Started state with [session]. */
    public interface Started<T : DevSession> : DevModeState<T> {
        override val session: T
    }

    /** Stopped state no [session]. */
    public object Stopped : DevModeState<Nothing> {
        override val session: Nothing? = null
    }
}


/** [DevMode] that delegates the session creation to [createSession]. */
public class DelegatingDevMode<T : DevSession>(
    private val createSession: () -> T,
) : DevMode<T> {
    override var state: DevModeState<T> = Stopped
        private set

    public override fun start(): Started<T> = when (val currentSession: DevModeState<T> = state) {
        is Started -> currentSession
        is Stopped -> {
            object : Started<T> {
                override val session: T = createSession()
            }.also { state = it }
        }
    }

    public override fun stop(): Stopped = when (val currentState: DevModeState<T> = state) {
        is Started -> {
            currentState.session.dispose()
            Stopped.also { state = it }
        }

        is Stopped -> currentState
    }
}
