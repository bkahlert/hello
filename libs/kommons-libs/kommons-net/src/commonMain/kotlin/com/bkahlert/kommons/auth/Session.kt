package com.bkahlert.kommons.auth

import com.bkahlert.kommons.oauth.OAuth2ResourceServer
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.HttpClientPlugin

public sealed interface Session {

    public interface UnauthorizedSession : Session {
        public suspend fun authorize(): Session
    }

    public interface AuthorizedSession : Session {

        /** User information contained in the cached [IdToken] */
        public val userInfo: UserInfo
        public val diagnostics: Map<String, String?>

        public suspend fun reauthorize(httpClient: HttpClient? = null): Session
        public suspend fun unauthorize(httpClient: HttpClient? = null): UnauthorizedSession

        /**
         * Installs an authentication [HttpClientPlugin]
         * on the provided [config] for that
         * can be used for requests that need authorization.
         *
         * By default, authorization headers
         * if the server did respond with `401 Unauthorized`.
         *
         * Requests to the specified [resources] always
         * contain authorization headers upfront.
         */
        public fun installAuth(
            config: HttpClientConfig<HttpClientEngineConfig>,
            vararg resources: OAuth2ResourceServer,
        )
    }
}
