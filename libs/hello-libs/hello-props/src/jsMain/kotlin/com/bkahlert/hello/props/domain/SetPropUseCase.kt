package com.bkahlert.hello.props.domain

import com.bkahlert.hello.props.data.PropsRepository
import kotlinx.serialization.json.JsonObject

public class SetPropUseCase(
    private val repository: PropsRepository,
) {
    public operator fun invoke(id: String, value: JsonObject) {
        repository.setProp(id, value)
    }
}
