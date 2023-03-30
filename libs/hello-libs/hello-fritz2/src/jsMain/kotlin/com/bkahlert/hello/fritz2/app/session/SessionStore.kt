@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.app.session

import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.reauthorizeIfNecessary
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.RootStore

public class SessionStore(
    initialData: Session,
    private val resolve: suspend () -> Session = { initialData },
) : RootStore<Session>(initialData) {

    override suspend fun enqueue(update: suspend (Session) -> Session) {
        super.enqueue { update(resolve()) }
    }

    public val authorize: EmittingHandler<Unit, Session> = handleAndEmit { session ->
        when (session) {
            is Session.AuthorizedSession -> session
            is Session.UnauthorizedSession -> session.authorize()
        }.also { emit(it) }
    }

    public val reauthorize: EmittingHandler<Boolean, Session> = handleAndEmit { session, force ->
        when (session) {
            is Session.AuthorizedSession -> if (force) session.reauthorize() else session.reauthorizeIfNecessary()
            is Session.UnauthorizedSession -> session
        }.also { emit(it) }
    }

    public val unauthorize: EmittingHandler<Unit, Session> = handleAndEmit { session ->
        when (session) {
            is Session.AuthorizedSession -> session.unauthorize()
            is Session.UnauthorizedSession -> session
        }.also { emit(it) }
    }

    init {
        handle { resolve() }
    }
}
