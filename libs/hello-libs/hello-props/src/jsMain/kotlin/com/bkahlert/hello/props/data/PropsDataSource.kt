package com.bkahlert.hello.props.data

import com.bkahlert.hello.props.domain.Props
import kotlinx.serialization.json.JsonElement

public interface PropsDataSource {
    public suspend fun getAll(): Props
    public suspend fun get(id: String): JsonElement?
    public suspend fun set(id: String, value: JsonElement): JsonElement
    public suspend fun remove(id: String): JsonElement?
}
