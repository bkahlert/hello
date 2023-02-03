package com.bkahlert.hello.session.domain

import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.kommons.js.grouping

public class ReauthorizeUseCase(private val repository: SessionRepository) {
    public suspend operator fun invoke() {
        console.grouping(ReauthorizeUseCase::class.simpleName!!, block = repository::reauthorize)
    }
}
