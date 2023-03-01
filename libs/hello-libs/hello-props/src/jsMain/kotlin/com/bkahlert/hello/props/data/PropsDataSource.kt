package com.bkahlert.hello.props.data

import com.bkahlert.hello.props.domain.Props
import kotlinx.serialization.json.JsonObject

public interface PropsDataSource {
    public suspend fun getAll(): Props
    public suspend fun get(id: String): JsonObject?
    public suspend fun set(id: String, value: JsonObject): JsonObject
    public suspend fun remove(id: String): JsonObject?
}
