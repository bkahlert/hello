package com.bkahlert.hello.props.demo

import com.bkahlert.hello.props.data.PropsDataSource
import com.bkahlert.hello.props.domain.Props
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

public class InMemoryPropsDataSource(
    private var props: Props,
) : PropsDataSource {

    override suspend fun getAll(): Props = props

    override suspend fun get(id: String): JsonObject? =
        props.content[id]?.jsonObject

    override suspend fun set(id: String, value: JsonObject): JsonObject {
        props += (id to value)
        return value
    }

    override suspend fun remove(id: String): JsonObject? {
        val old = props.content[id]
        props -= id
        return old?.jsonObject
    }
}
