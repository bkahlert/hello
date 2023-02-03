package com.bkahlert.hello.session.domain

import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.kommons.js.grouping

public class AuthorizeUseCase(private val repository: SessionRepository) {
    public suspend operator fun invoke() {
        console.grouping(AuthorizeUseCase::class.simpleName!!, block = repository::authorize)
    }
}
