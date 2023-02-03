package com.bkahlert.hello.props.domain

import com.bkahlert.kommons.js.grouping
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonElement

public class SetPropUseCase(
    private val getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
) {

    public suspend operator fun invoke(id: String, value: JsonElement): JsonElement? {
        val setProp: suspend () -> JsonElement? = {
            getPropsRepositoryUseCase().first()?.setProp(id, value)
        }
        return console.grouping(SetPropUseCase::class.simpleName!!, block = setProp)
    }
}
