package com.bkahlert.hello.app

import com.bkahlert.hello.app.env.Environment
import com.bkahlert.hello.app.props.PropsDataSource
import com.bkahlert.hello.app.props.PropsStore
import com.bkahlert.hello.app.props.RemotePropsDataSource
import com.bkahlert.hello.app.props.StoragePropsDataSource
import com.bkahlert.hello.app.session.SessionStore
import com.bkahlert.hello.app.user.User
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.uri
import com.bkahlert.kommons.js.trace
import com.bkahlert.kommons.md5
import com.bkahlert.kommons.oauth.AuthorizationCodeFlowState
import com.bkahlert.kommons.uri.resolve
import dev.fritz2.core.Handler
import dev.fritz2.core.Id
import dev.fritz2.core.RootStore
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

public class AppStore(
    initialData: AppState = AppState.Loading,
    private val environmentProvider: suspend () -> Environment = { Environment.load() },
    private val sessionResolver: suspend (Environment) -> (suspend () -> Session) = { environment ->
        {
            AuthorizationCodeFlowState.resolve(
                openIDProviderUrl = window.location.uri.resolve(environment.search(label = "OpenID Provider URL", keySubstring = "PROVIDER_URL")),
                clientId = environment.search(label = "Unable to find client ID", keySubstring = "CLIENT_ID"),
            )
        }
    },
    private val propsProvider: suspend (Environment, Session) -> PropsDataSource = { environment, session ->
        when (session) {
            is Session.AuthorizedSession -> RemotePropsDataSource.from(environment, session)
            else -> StoragePropsDataSource(localStorage)
        }
    },
    id: String = Id.next(),
) : RootStore<AppState>(initialData, id) {

    private suspend fun PropsDataSource.restore(user: User): Map<String, JsonElement> = AppCache.from(user).props.trace("RESTORED CACHED") ?: get()
    private fun PropsStore.cache(user: User) {
        AppCache.from(user).also { synced.filterNotNull().handledBy { props -> it.props = props } }
    }

    public val updateSession: Handler<Session> = handle { state, session ->
        check(state is AppState.Loaded) { "Session can only be updated in loaded state." }
        when (session) {
            is Session.AuthorizedSession -> {
                val user = User(session)
                val propsDataSource = propsProvider(state.environment, session)
                val propsStore = PropsStore(propsDataSource.restore(user), propsDataSource).apply { cache(user) }
                AppState.Authorized(state.environment, state.session, user, propsStore)
            }

            is Session.UnauthorizedSession -> {
                val propsDataSource = propsProvider(state.environment, session)
                val propsStore = PropsStore(propsDataSource.get(), propsDataSource)
                AppState.Unauthorized(state.environment, state.session, propsStore)
            }
        }
    }

    public val updateEnvironment: Handler<Unit> = handle { state, _ ->
        val environment = environmentProvider()

        val sessionResolver = sessionResolver(environment)
        val session = sessionResolver()
        val sessionStore = SessionStore(session, sessionResolver).apply {
            authorize handledBy updateSession
            reauthorize handledBy updateSession
            unauthorize handledBy updateSession
        }

        when (session) {
            is Session.AuthorizedSession -> {
                val user = User(session)
                val propsDataSource = propsProvider(environment, session)
                val propsStore = PropsStore(propsDataSource.restore(user), propsDataSource).apply { cache(user) }
                AppState.Authorized(environment, sessionStore, user, propsStore)
            }

            is Session.UnauthorizedSession -> {
                val propsDataSource = propsProvider(environment, session)
                val propsStore = PropsStore(propsDataSource.get(), propsDataSource)
                AppState.Unauthorized(environment, sessionStore, propsStore)
            }
        }
    }

    init {
        updateEnvironment()
    }
}


// TODO convert to WebWorker
public class AppCache(
    private val storage: Storage,
    private val encrypt: (String) -> String,
    private val decrypt: (String) -> String,
    private val stringFormat: StringFormat = Json,
) {

    private inline fun <reified T> get(key: String): T? =
        storage[key]?.let(decrypt)?.let { stringFormat.runCatching { decodeFromString<T>(it) }.getOrNull() }

    private inline fun <reified T> set(key: String, value: T) {
        storage["props"] = value?.let { stringFormat.runCatching { encodeToString(it) }.getOrNull() }?.let(encrypt)
    }

    public var props: Map<String, JsonElement>?
        get() = get("props")
        set(value) {
            set("props", value)
        }

    public companion object {
        public fun from(user: User): AppCache {
            val es = EncryptStorage(user.id)
            return AppCache(localStorage.scoped("@cache:" + md5(user.id)), es::encryptString, es::decryptString)
        }
    }
}
