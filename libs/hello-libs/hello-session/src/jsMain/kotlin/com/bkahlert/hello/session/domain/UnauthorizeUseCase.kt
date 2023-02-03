package com.bkahlert.hello.session.domain

import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.kommons.js.grouping

public class UnauthorizeUseCase(private val repository: SessionRepository) {
    public suspend operator fun invoke() {
        console.grouping(UnauthorizeUseCase::class.simpleName!!, block = repository::unauthorize)
    }
}
