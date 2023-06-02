package com.bkahlert.hello.app

import com.bkahlert.hello.app.env.Environment
import com.bkahlert.hello.app.props.PropsStore
import com.bkahlert.hello.app.session.SessionStore
import com.bkahlert.hello.app.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement

public sealed interface AppState {

    public object Loading : AppState

    public sealed interface Loaded : AppState {
        public val environment: Environment
        public val session: SessionStore
        public val props: PropsStore
    }

    public data class Authorized(
        public override val environment: Environment,
        public override val session: SessionStore,
        public val user: User,
        public override val props: PropsStore,
    ) : Loaded

    public data class Unauthorized(
        public override val environment: Environment,
        public override val session: SessionStore,
        public override val props: PropsStore,
    ) : Loaded
}

public val Flow<AppState>.environment: Flow<Environment?> get() = map { (it as? AppState.Loaded)?.environment }
public val Flow<AppState>.session: Flow<SessionStore?> get() = map { (it as? AppState.Loaded)?.session }
public val Flow<AppState>.user: Flow<User?> get() = map { (it as? AppState.Authorized)?.user }
public val Flow<AppState>.props: Flow<PropsStore?> get() = map { (it as? AppState.Loaded)?.props }

public val AppStore.environment: Flow<Environment?> get() = data.environment
public val AppStore.session: Flow<SessionStore?> get() = data.session
public val AppStore.user: Flow<User?> get() = data.user
public val AppStore.props: Flow<PropsStore?> get() = data.props

public operator fun Flow<PropsStore?>.get(id: String): Flow<JsonElement?> = flatMapLatest { store -> store?.data?.map { it[id] } ?: flowOf(null) }
