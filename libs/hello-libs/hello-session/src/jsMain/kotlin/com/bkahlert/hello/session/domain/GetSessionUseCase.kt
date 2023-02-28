package com.bkahlert.hello.session.domain

import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.kommons.auth.Session
import kotlinx.coroutines.flow.Flow

public class GetSessionUseCase(private val repository: SessionRepository) {

    public operator fun invoke(): Flow<Session> = repository.sessionFlow()
}
