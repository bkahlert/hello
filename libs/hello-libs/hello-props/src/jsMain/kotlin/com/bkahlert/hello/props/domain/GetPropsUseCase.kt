package com.bkahlert.hello.props.domain

import com.bkahlert.kommons.js.grouping
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

public class GetPropsUseCase(
    private val getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
) {

    public operator fun invoke(): Flow<Props?> {
        val propsFlow: () -> Flow<Props?> = {
            getPropsRepositoryUseCase().flatMapLatest {
                when (val propsRepository = it) {
                    null -> flowOf(null)
                    else -> propsRepository.propsFlow()
                }
            }
        }
        return console.grouping(GetPropsUseCase::class.simpleName!!, block = propsFlow)
    }
}
