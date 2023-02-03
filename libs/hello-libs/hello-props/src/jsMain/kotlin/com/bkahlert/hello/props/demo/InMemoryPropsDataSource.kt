package com.bkahlert.hello.props.demo

import com.bkahlert.hello.props.data.PropsDataSource
import com.bkahlert.hello.props.domain.Props
import kotlinx.serialization.json.JsonElement

public class InMemoryPropsDataSource(
    private var props: Props,
) : PropsDataSource {

    override suspend fun getAll(): Props = props

    override suspend fun get(id: String): JsonElement? =
        props.content[id]

    override suspend fun set(id: String, value: JsonElement): JsonElement {
        props += (id to value)
        return value
    }

    override suspend fun remove(id: String): JsonElement? {
        val old = props.content[id]
        props -= id
        return old
    }
}
