package com.bkahlert.hello.session.domain

import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping

public class AuthorizeUseCase(private val repository: SessionRepository) {
    private val logger by ConsoleLogging
    public suspend operator fun invoke() {
        logger.grouping(::invoke) {
            repository.authorize()
        }
    }
}
