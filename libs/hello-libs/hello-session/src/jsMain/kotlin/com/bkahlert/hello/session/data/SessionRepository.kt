package com.bkahlert.hello.session.data

import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.expiresIn
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
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
    private val logger by ConsoleLogging
    private val sessionFlow: MutableSharedFlow<Session> = MutableSharedFlow(replay = 1, onBufferOverflow = DROP_OLDEST)

    private suspend fun loadSession(): Session = logger.grouping(::loadSession) {
        withContext(ioDispatcher) {
            sessionDataSource.load()
        }
    }

    private suspend fun updateSession(block: suspend (Session) -> Session) {
        logger.debug("Updating session")
        val updatedSession = block(loadSession())
        logger.debug("Emitting $updatedSession")
        sessionFlow.emit(updatedSession)
        logger.debug("Emitted $updatedSession")
    }

    public suspend fun authorize() {
        logger.grouping(::authorize) {
            updateSession {
                when (it) {
                    is Session.AuthorizedSession -> it.also { logger.warn("Already ${it::class.simpleName}", it.userInfo) }
                    is Session.UnauthorizedSession -> it.authorize()
                }
            }
        }
    }

    public suspend fun reauthorize() {
        logger.grouping(::reauthorize) {
            updateSession { session ->
                when (session) {
                    is Session.AuthorizedSession -> {
                        val expiresInMoment = session.userInfo.expiresIn.toMomentString(descriptive = false)
                        if (session.userInfo.expiresIn > reauthorizationThreshold) {
                            logger.debug("Using cached user info because the tokens seem valid for another $expiresInMoment")
                            session
                        } else {
                            logger.info("Reauthorizing because the tokens limited validity of $expiresInMoment")
                            session.reauthorize()
                        }
                    }

                    is Session.UnauthorizedSession -> {
                        logger.debug("Already signed-out")
                        session
                    }
                }
            }
        }
    }

    public suspend fun unauthorize() {
        logger.grouping(::unauthorize) {
            updateSession {
                when (it) {
                    is Session.AuthorizedSession -> it.unauthorize()
                    is Session.UnauthorizedSession -> it.also { logger.warn("Already ${it::class.simpleName}") }
                }
            }
        }
    }

    public fun sessionFlow(): Flow<Session> = sessionFlow.asSharedFlow()
}
