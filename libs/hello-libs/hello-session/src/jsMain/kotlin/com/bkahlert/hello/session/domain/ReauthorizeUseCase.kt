package com.bkahlert.hello.session.domain

import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping

public class ReauthorizeUseCase(private val repository: SessionRepository) {
    private val logger by ConsoleLogging
    public suspend operator fun invoke(force: Boolean = false) {
        logger.grouping(::invoke) {
            repository.reauthorize(force = force)
        }
    }
}
