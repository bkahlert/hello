package com.bkahlert.hello.user.domain

import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import com.bkahlert.kommons.auth.Session.UnauthorizedSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public class GetUserUseCase(
    private val repository: SessionRepository,
) {

    public operator fun invoke(): Flow<Resource<User?>> = repository.sessionFlow().map { sessionResource ->
        when (sessionResource) {
            is Success -> when (val session = sessionResource.data) {
                is UnauthorizedSession -> Success(null)
                is AuthorizedSession -> Success(User(session))
            }

            is Failure -> Failure("Failed to load user", sessionResource.cause)
        }
    }
}
