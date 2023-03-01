package com.bkahlert.hello.props.domain

import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.props.data.PropsRepository
import kotlinx.coroutines.flow.Flow

public class GetPropsUseCase(
    private val repository: PropsRepository,
) {
    public operator fun invoke(): Flow<Resource<Props?>> = repository.propsFlow()
}
