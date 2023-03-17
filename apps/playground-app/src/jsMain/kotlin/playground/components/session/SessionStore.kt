@file:Suppress("RedundantVisibilityModifier")

package playground.components.session

import com.bkahlert.hello.session.data.SessionDataSource
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.Session.Undetermined
import com.bkahlert.kommons.auth.expiresIn
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.time.toMomentString
import dev.fritz2.core.Handler
import dev.fritz2.core.RootStore
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

public class SessionStore(
    private val sessionDataSource: SessionDataSource,
    private val reauthorizationThreshold: Duration = 10.minutes,
) : RootStore<Session>(Undetermined) {

    private val logger by ConsoleLogging

    private val load: Handler<Unit> = handle { _, _ ->
        logger.grouping(SessionDataSource::load) {
            sessionDataSource.load()
        }
    }

    init {
        load()
    }

    public val authorize: Handler<Unit> = handle {
        logger.grouping("authorize") {
            when (val session = sessionDataSource.load()) {
                is Session.AuthorizedSession -> session.also { logger.warn("Already ${it::class.simpleName}", it.userInfo) }
                is Session.UnauthorizedSession -> session.authorize()
            }
        }
    }

    public val reauthorize: Handler<Boolean> = handle { _, force ->
        logger.grouping("reauthorize") {
            when (val session = sessionDataSource.load()) {
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
        }
    }

    public val unauthorize: Handler<Unit> = handle {
        logger.grouping("unauthorize") {
            when (val session = sessionDataSource.load()) {
                is Session.AuthorizedSession -> session.unauthorize()
                is Session.UnauthorizedSession -> session.also { logger.warn("Already ${it::class.simpleName}") }
            }
        }
    }
}
