package com.bkahlert.hello.environment.domain

import com.bkahlert.hello.data.DataRetrieval
import com.bkahlert.hello.environment.data.EnvironmentRepository
import kotlinx.coroutines.flow.Flow

public data class GetEnvironmentUseCase(private val repository: EnvironmentRepository) {
    public operator fun invoke(): Flow<DataRetrieval<Environment>> = repository.environmentFlow()
}
