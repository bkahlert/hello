package com.bkahlert.hello.client

import com.bkahlert.kommons.auth.OAuth2AuthorizationServer
import com.bkahlert.kommons.auth.OAuth2AuthorizationState
import com.bkahlert.kommons.auth.OAuth2AuthorizationState.Authorized
import com.bkahlert.kommons.auth.OAuth2AuthorizationState.Authorizing
import com.bkahlert.kommons.auth.OAuth2AuthorizationState.Unauthorized
import com.bkahlert.kommons.auth.OAuth2Resource
import com.bkahlert.kommons.auth.loadOpenIDConfiguration
import com.bkahlert.kommons.ktor.JsonHttpClient
import com.bkahlert.kommons.logging.InlineLogging
import io.ktor.client.HttpClient
import io.ktor.http.Url
import kotlin.reflect.KClass

public sealed class HelloClient(
    protected val apiClients: Map<KClass<out ApiClient>, String?>,
    protected val httpClient: HttpClient = JsonHttpClient(),
) {
    public val userInfo: UserInfoApiClient? = apiClients[UserInfoApiClient::class]?.let { UserInfoApiClient(it, httpClient) }

    public class Failed(
        private val reason: String,
        apiClients: Map<KClass<out ApiClient>, String?>,
    ) : HelloClient(apiClients) {
        override fun toString(): String = "Hello! client in failed state. Reason: $reason"
    }

    public class LoggedOut(
        apiClients: Map<KClass<out ApiClient>, String?>,
        private val authorizationState: Unauthorized,
    ) : HelloClient(apiClients) {
        override fun toString(): String = "Hello! client in logged-out state"
        public suspend fun logIn(): OAuth2AuthorizationState =
            authorizationState.authorize()
    }

    public class LoggedIn(
        apiClients: Map<KClass<out ApiClient>, String?>,
        private val authorizationState: Authorized,
    ) : HelloClient(
        apiClients = apiClients,
        httpClient = JsonHttpClient {
            authorizationState.installAuth(this, object : OAuth2Resource {
                override val name: String get() = "api"
                private val endpointUrls = apiClients.values.filterNotNull().map { ApiClient.endpointToUrl(it) }
                override fun matches(url: Url): Boolean = url.toString().let { endpointUrls.any { endpointUrl -> it.startsWith(endpointUrl) } }
            })
        },
    ) {
        override fun toString(): String = "Hello! client in logged-in state"

        public val userProps: UserPropsApiClient? = apiClients[UserPropsApiClient::class]?.let { UserPropsApiClient(it, httpClient) }

        public suspend fun logOut(): HelloClient =
            load(authorizationState.revokeTokens(httpClient), apiClients)
    }


    public companion object {

        private val logger by InlineLogging(HelloClient::class.simpleName)

        public suspend fun load(
            config: HelloClientConfig,
        ): HelloClient {
            logger.info("Loading with config: $config")
            val openIDProvider = config.openIDProvider ?: return Failed("OpenID provider configuration missing", config.apiClients)
            val clientId = config.clientId ?: return Failed("Client ID missing", config.apiClients)

            val metadata = openIDProvider.loadOpenIDConfiguration()
            val authServer = OAuth2AuthorizationServer.from(metadata)
            return load(authServer, clientId, config.apiClients)
        }

        public suspend fun load(
            authServer: OAuth2AuthorizationServer,
            clientId: String,
            apiClients: Map<KClass<out ApiClient>, String?>,
        ): HelloClient {
            logger.info("Loading with authorization server: $authServer")
            val authorizationState = OAuth2AuthorizationState.load(authServer, clientId)
            return load(authorizationState, apiClients)
        }

        private suspend fun load(
            authorizationState: OAuth2AuthorizationState,
            apiClients: Map<KClass<out ApiClient>, String?>,
        ): HelloClient {
            logger.info("Loading with authorization state: $authorizationState")

            val helloClient = when (authorizationState) {
                is Unauthorized -> LoggedOut(apiClients, authorizationState)
                is Authorizing -> LoggedIn(apiClients, authorizationState.getTokens())
                is Authorized -> LoggedIn(apiClients, authorizationState)
            }

            logger.info("Finished loading $helloClient")
            return helloClient
        }
    }
}
