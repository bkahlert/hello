package com.bkahlert.hello.environment.domain

import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.kommons.js.grouping

public data class RefreshEnvironmentUseCase(private val repository: EnvironmentRepository) {
    public suspend operator fun invoke() {
        console.grouping(RefreshEnvironmentUseCase::class.simpleName!!, block = repository::refreshEnvironment)
    }
}
