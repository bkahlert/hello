package com.bkahlert.hello.props.domain

import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.hello.props.data.PropsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

public class GetPropUseCase(
    private val repository: PropsRepository,
) {

    public operator fun invoke(id: String): Flow<Resource<JsonObject?>> = repository.propsFlow().mapLatest { propsResource ->
        when (propsResource) {
            is Success -> {
                when (val props = propsResource.data) {
                    null -> Success(null)
                    else -> when (val prop: JsonElement? = props.content[id]) {
                        null -> Success(null)
                        is JsonObject -> Success(prop)
                        else -> Failure("Prop $id is no ${JsonObject::class.simpleName}: $prop")
                    }
                }
            }

            is Failure -> propsResource
        }
    }
}
