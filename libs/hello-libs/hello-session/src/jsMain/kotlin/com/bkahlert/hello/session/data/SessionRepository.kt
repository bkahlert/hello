package com.bkahlert.hello.session.data

import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.data.getDataOrThrow
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.expiresIn
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.time.toMomentString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

public data class SessionRepository(
    private val sessionDataSource: SessionDataSource,
    private val externalScope: CoroutineScope,
    private val reauthorizationThreshold: Duration = 10.minutes,
) {
    private val logger by ConsoleLogging
    private val session: MutableSharedFlow<Resource<Session>> = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private fun updateSession(block: suspend (Session) -> Session): Deferred<Session> = externalScope.async {
        val updatedSessionResource = Resource.load { block(sessionDataSource.load()) }
        logger.debug("Session updated", updatedSessionResource)

        session.tryEmit(updatedSessionResource)
        updatedSessionResource.getDataOrThrow()
    }

    init {
        updateSession { session -> session }.invokeOnCompletion {
            logger.info("Initial session state successfully resolved")
        }
    }

    public suspend fun authorize(): Session = logger.grouping(::authorize) {
        updateSession { session ->
            when (session) {
                is Session.AuthorizedSession -> session.also { logger.warn("Already ${it::class.simpleName}", it.userInfo) }
                is Session.UnauthorizedSession -> session.authorize()
            }
        }.await()
    }

    public suspend fun reauthorize(force: Boolean = false): Session = logger.grouping(::reauthorize) {
        updateSession { session ->
            when (session) {
                is Session.AuthorizedSession -> {
                    if (force) {
                        logger.info("Reauthorizing due to forced request")
                        session.reauthorize()
                    } else {
                        val expiresInMoment = session.userInfo.expiresIn.toMomentString(descriptive = false)
                        if (session.userInfo.expiresIn > reauthorizationThreshold) {
                            logger.debug("Using cached user info because the tokens seem valid for another $expiresInMoment")
                            session
                        } else {
                            logger.info("Reauthorizing because the tokens limited validity of $expiresInMoment")
                            session.reauthorize()
                        }
                    }
                }

                is Session.UnauthorizedSession -> {
                    logger.debug("Already signed-out")
                    session
                }
            }
        }.await()
    }

    public suspend fun unauthorize(): Session = logger.grouping(::unauthorize) {
        updateSession { session ->
            when (session) {
                is Session.AuthorizedSession -> session.unauthorize()
                is Session.UnauthorizedSession -> session.also { logger.warn("Already ${it::class.simpleName}") }
            }
        }.await()
    }

    public fun sessionFlow(): Flow<Resource<Session>> = session.asSharedFlow()
}
