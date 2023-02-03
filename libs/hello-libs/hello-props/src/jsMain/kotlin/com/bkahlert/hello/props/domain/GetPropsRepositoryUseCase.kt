package com.bkahlert.hello.props.domain

import com.bkahlert.hello.props.data.PropsRepository
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import com.bkahlert.kommons.auth.Session.UnauthorizedSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

public class GetPropsRepositoryUseCase(
    private val propsRepositoryFlow: Flow<PropsRepository?>,
) {
    public constructor(propsRepository: PropsRepository) : this(flowOf(propsRepository))

    public constructor(
        sessionFlow: Flow<Session>,
        propsRepository: (AuthorizedSession) -> PropsRepository,
    ) : this(sessionFlow.map { session ->
        when (session) {
            is UnauthorizedSession -> null
            is AuthorizedSession -> propsRepository(session)
        }
    })

    public operator fun invoke(): Flow<PropsRepository?> = propsRepositoryFlow
}
