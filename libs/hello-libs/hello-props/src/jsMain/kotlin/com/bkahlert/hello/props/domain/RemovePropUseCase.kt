package com.bkahlert.hello.props.domain

import com.bkahlert.kommons.js.grouping
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonElement

public class RemovePropUseCase(
    private val getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
) {

    public suspend operator fun invoke(id: String): JsonElement? {
        val removedPropFlow: suspend () -> JsonElement? = {
            getPropsRepositoryUseCase().first()?.removeProp(id)
        }
        return console.grouping(RemovePropUseCase::class.simpleName!!, block = removedPropFlow)
    }
}
