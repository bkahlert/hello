package com.bkahlert.hello.props.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement

public class GetPropUseCase(
    private val getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
) {

    public operator fun invoke(id: String): Flow<JsonElement?> = getPropsRepositoryUseCase().flatMapLatest {
        when (val propsRepository = it) {
            null -> flowOf(null)
            else -> propsRepository.propsFlow().map { props -> props?.content?.get(id) }
        }
    }
}
