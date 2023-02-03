package com.bkahlert.hello.session.data

import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.expiresIn
import com.bkahlert.kommons.js.debug
import com.bkahlert.kommons.toMomentString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

public class SessionRepository(
    private val sessionDataSource: SessionDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val reauthorizationThreshold: Duration = 10.minutes,
) {
    private val loggerName = checkNotNull(SessionRepository::class.simpleName)
    private val sessionFlow: MutableSharedFlow<Session> = MutableSharedFlow(replay = 1, onBufferOverflow = DROP_OLDEST)

    private suspend fun loadSession(): Session = withContext(ioDispatcher) {
        sessionDataSource.load().also {
            console.debug("$loggerName: Loaded session $it")
        }
    }

    private suspend fun updateSession(block: suspend (Session) -> Session) {
        console.debug("$loggerName: Updating session")
        val updatedSession = block(loadSession())
        console.debug("$loggerName: Emitting $updatedSession")
        sessionFlow.emit(updatedSession)
        console.debug("$loggerName: Emitted $updatedSession")
    }

    public suspend fun authorize() {
        console.debug("$loggerName: ${::authorize.name}")
        updateSession {
            when (it) {
                is Session.AuthorizedSession -> it.also { console.warn("$loggerName: Already ${it::class.simpleName}", it.userInfo) }
                is Session.UnauthorizedSession -> it.authorize()
            }
        }
    }

    public suspend fun reauthorize() {
        console.debug("$loggerName: ${::reauthorize.name}")
        updateSession { session ->
            when (session) {
                is Session.AuthorizedSession -> {
                    val expiresInMoment = session.userInfo.expiresIn.toMomentString(descriptive = false)
                    if (session.userInfo.expiresIn > reauthorizationThreshold) {
                        console.debug("$loggerName: Using cached user info because the tokens seem valid for another $expiresInMoment")
                        session
                    } else {
                        console.info("$loggerName: Reauthorizing because the tokens limited validity of $expiresInMoment")
                        session.reauthorize()
                    }
                }

                is Session.UnauthorizedSession -> {
                    console.debug("$loggerName: Already signed-out")
                    session
                }
            }
        }
    }

    public suspend fun unauthorize() {
        console.debug("$loggerName: ${::unauthorize.name}")
        updateSession {
            when (it) {
                is Session.AuthorizedSession -> it.unauthorize()
                is Session.UnauthorizedSession -> it.also { console.warn("$loggerName: Already ${it::class.simpleName}") }
            }
        }
    }

    public fun sessionFlow(): Flow<Session> = sessionFlow.asSharedFlow()
}
