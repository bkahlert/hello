package com.bkahlert.hello.environment.domain

import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.kommons.js.grouping
import kotlinx.coroutines.flow.Flow

public data class GetEnvironmentUseCase(private val repository: EnvironmentRepository) {
    public operator fun invoke(): Flow<Environment> =
        console.grouping(GetEnvironmentUseCase::class.simpleName!!, block = repository::getEnvironmentFlow)
}
