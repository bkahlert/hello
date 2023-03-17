@file:Suppress("RedundantVisibilityModifier")

package playground.components.props

import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.props.data.PropsDataSource
import com.bkahlert.hello.props.data.SessionPropsDataSource
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import dev.fritz2.core.Handler
import dev.fritz2.core.RootStore
import dev.fritz2.core.lensOf
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import playground.components.session.SessionStore

public class PropsStore(
    private val propsDataSource: PropsDataSource,
) : RootStore<Props?>(null) {

    constructor(environment: Environment, sessionStore: SessionStore) : this(
        propsDataSource = object : PropsDataSource {
            val dataSourceStore = sessionStore.map(
                lensOf(
                    "props",
                    { session -> (session as? AuthorizedSession)?.let { SessionPropsDataSource(it, environment) } },
                    { session, _ -> session },
                )
            )

            override suspend fun get(id: String): JsonObject? = dataSourceStore.current?.get(id)?.jsonObject
            override suspend fun getAll(): Props = dataSourceStore.current?.getAll() ?: Props.EMPTY
            override suspend fun set(id: String, value: JsonObject) = dataSourceStore.current?.set(id, value) ?: Props.EMPTY.content
            override suspend fun remove(id: String) = dataSourceStore.current?.remove(id) ?: Props.EMPTY.content
        },
    )

    private val logger by ConsoleLogging

    val load: Handler<Unit> = handle { _, _ ->
        logger.grouping(PropsDataSource::getAll) {
            propsDataSource.getAll()
        }
    }

    init {
        load()
    }

    val setProp = handle<Pair<String, JsonObject>> { props, (id: String, value: JsonObject) ->
        logger.grouping("setProp", id, value) {
            when (props) {
                null -> null
                else -> props + (id to value)
            }
        }
    }

    val removeProp = handle<String> { props, id ->
        logger.grouping("removeProp", id) {
            when (props) {
                null -> null
                else -> props - id
            }
        }
    }
}
