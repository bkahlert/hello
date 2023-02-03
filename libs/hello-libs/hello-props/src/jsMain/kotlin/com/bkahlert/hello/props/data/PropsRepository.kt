package com.bkahlert.hello.props.data

import com.bkahlert.hello.props.domain.Props
import com.bkahlert.kommons.js.debug
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
    private val loggerName = checkNotNull(PropsRepository::class.simpleName)
    private val propsFlow: MutableStateFlow<Props?> = MutableStateFlow(null)

    public suspend fun refreshProps(force: Boolean = false): Props {
        console.debug("$loggerName: ${::refreshProps.name}")
        return propsFlow.updateAndGet { props ->
            withContext(ioDispatcher) {
                when (props) {
                    null -> propsDataSource.getAll()
                    else -> if (force) propsDataSource.getAll() else props
                }
            }.also {
                console.debug("$loggerName: Updated $it")
            }
        }!!
    }

    private suspend fun updateProp(id: String, value: JsonElement?) {
        console.debug("$loggerName: Updating prop $id to $value")
        propsFlow.updateAndGet { props ->
            withContext(ioDispatcher) {
                when (props) {
                    null -> if (value != null) Props(buildJsonObject { put(id, value) }) else Props.EMPTY
                    else -> if (value != null) props + (id to value) else props - id
                }
            }.also {
                console.debug("$loggerName: Updated $it")
            }
        }
    }

    public suspend fun getProps(): Props {
        console.debug("$loggerName: ${::getProps.name}")
        return when (val props = propsFlow.value) {
            null -> refreshProps()
            else -> props
        }
    }

    public suspend fun getProp(id: String): JsonElement? {
        console.debug("$loggerName: ${::getProp.name}")
        return when (val props = propsFlow.value) {
            null -> refreshProps().content[id]
            else -> props.content[id]
        }
    }

    public suspend fun setProp(id: String, value: JsonElement): JsonElement {
        console.debug("$loggerName: ${::setProp.name}")
        return withContext(ioDispatcher) {
            propsDataSource.set(id, value).also { updateProp(id, it) }
        }
    }

    public suspend fun removeProp(id: String): JsonElement? {
        console.debug("$loggerName: ${::removeProp.name}")
        return withContext(ioDispatcher) {
            propsDataSource.remove(id).also { updateProp(id, it) }
        }
    }

    public fun propsFlow(): SharedFlow<Props?> = propsFlow
        .onSubscription { refreshProps() }
}
