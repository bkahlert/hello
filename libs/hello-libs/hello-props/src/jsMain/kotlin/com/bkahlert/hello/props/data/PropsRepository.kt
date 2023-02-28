package com.bkahlert.hello.props.data

import com.bkahlert.hello.props.domain.Props
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject

public class PropsRepository(
    private val propsDataSource: PropsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val logger by ConsoleLogging
    private val propsFlow: MutableStateFlow<Props?> = MutableStateFlow(null)

    public suspend fun refreshProps(force: Boolean = false): Props = logger.grouping(::refreshProps, force) {
        propsFlow.updateAndGet { props ->
            withContext(ioDispatcher) {
                when (props) {
                    null -> propsDataSource.getAll()
                    else -> if (force) propsDataSource.getAll() else props
                }
            }
        }!!
    }

    private suspend fun updateProp(id: String, value: JsonElement?): Props? = logger.grouping(::updateProp, id, value) {
        propsFlow.updateAndGet { props ->
            withContext(ioDispatcher) {
                when (props) {
                    null -> if (value != null) Props(buildJsonObject { put(id, value) }) else Props.EMPTY
                    else -> if (value != null) props + (id to value) else props - id
                }
            }
        }
    }

    public suspend fun getProps(): Props = logger.grouping(::getProps) {
        when (val props = propsFlow.value) {
            null -> refreshProps()
            else -> props
        }
    }

    public suspend fun getProp(id: String): JsonElement? = logger.grouping(::getProp, id) {
        when (val props = propsFlow.value) {
            null -> refreshProps().content[id]
            else -> props.content[id]
        }
    }

    public suspend fun setProp(id: String, value: JsonElement): JsonElement = logger.grouping(::setProp, id, value) {
        withContext(ioDispatcher) {
            propsDataSource.set(id, value).also { updateProp(id, it) }
        }
    }

    public suspend fun removeProp(id: String): JsonElement? = logger.grouping(::removeProp, id) {
        withContext(ioDispatcher) {
            propsDataSource.remove(id).also { updateProp(id, it) }
        }
    }

    public fun propsFlow(): SharedFlow<Props?> = propsFlow
        .onSubscription { refreshProps() }
}
