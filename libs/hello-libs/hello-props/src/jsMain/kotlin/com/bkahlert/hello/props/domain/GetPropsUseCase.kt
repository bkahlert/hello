package com.bkahlert.hello.props.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

public class GetPropsUseCase(
    private val getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
) {

    public operator fun invoke(): Flow<Props?> = getPropsRepositoryUseCase().flatMapLatest {
        when (val propsRepository = it) {
            null -> flowOf(null)
            else -> propsRepository.propsFlow()
        }
    }
}
