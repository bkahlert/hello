package com.bkahlert.hello.user.domain

import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import com.bkahlert.kommons.auth.Session.UnauthorizedSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public class GetUserUseCase(
    private val repository: SessionRepository,
) {

    public operator fun invoke(): Flow<User?> = repository.sessionFlow().map { session ->
        when (session) {
            is UnauthorizedSession -> null
            is AuthorizedSession -> User(session)
        }
    }
}
