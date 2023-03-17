package playground.components.app

import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.hello.session.data.OpenIDConnectSessionDataSource
import com.bkahlert.hello.session.data.SessionDataSource
import com.bkahlert.hello.session.demo.FakeSessionDataSource
import com.bkahlert.hello.user.domain.User
import com.bkahlert.kommons.auth.Session
import dev.fritz2.core.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import playground.components.props.PropsStore
import playground.components.session.SessionStore
import playground.components.user.UserStore

@Suppress("RedundantVisibilityModifier")
public class AppStore(
    environment: Environment,
    private val sessionStore: SessionStore,
    userStoreProvider: (Environment, SessionStore) -> UserStore = { _, s -> UserStore(s) },
    propsStoreProvider: (Environment, SessionStore) -> PropsStore = { e, s -> PropsStore(e, s) },
) : Store<Session> by sessionStore {
    constructor(
        environment: Environment? = null,
        sessionDataSource: SessionDataSource = if (environment != null) OpenIDConnectSessionDataSource(environment) else FakeSessionDataSource(),
    ) : this(
        environment ?: Environment("PROPS_API" to "/mock/props"),
        SessionStore(sessionDataSource),
    )

    val userStore = userStoreProvider(environment, sessionStore)

    private val propsStore = propsStoreProvider(environment, sessionStore)

    public val user: Flow<User?> = userStore.data
    public val props: Flow<Props?> = propsStore.data
    public fun getProp(id: String): Flow<JsonObject?> = props.map { it?.content?.get(id)?.jsonObject }
}
