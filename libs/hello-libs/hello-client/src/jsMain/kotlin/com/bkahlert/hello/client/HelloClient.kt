package com.bkahlert.hello.client

import com.bkahlert.kommons.auth.OAuth2AuthorizationServer
import com.bkahlert.kommons.auth.OAuth2AuthorizationState
import com.bkahlert.kommons.auth.OAuth2AuthorizationState.Authorized
import com.bkahlert.kommons.auth.OAuth2AuthorizationState.Authorizing
import com.bkahlert.kommons.auth.OAuth2AuthorizationState.Unauthorized
import com.bkahlert.kommons.auth.OAuth2Resource
import com.bkahlert.kommons.auth.loadOpenIDConfiguration
import com.bkahlert.kommons.ktor.AuthorizationToken
import com.bkahlert.kommons.ktor.JsonHttpClient
import com.bkahlert.kommons.ktor.Token
import com.bkahlert.kommons.ktor.installTokenAuth
import com.bkahlert.kommons.logging.InlineLogging
import io.ktor.client.HttpClient
import io.ktor.http.Url
import kotlin.properties.Delegates
import kotlin.reflect.KClass

public sealed class HelloClient(
    protected val apiClients: Map<KClass<out ApiClient>, String?>,
    protected val httpClient: HttpClient = JsonHttpClient(),
) {
    public val userInfo: UserInfoApiClient? = apiClients[UserInfoApiClient::class]?.let { UserInfoApiClient(it, httpClient) }

    override fun toString(): String = this::class.simpleName.toString()


    public class Anonymous(
        apiClients: Map<KClass<out ApiClient>, String?>,
    ) : HelloClient(apiClients)


    public class LoggedOut(
        apiClients: Map<KClass<out ApiClient>, String?>,
        private val authorizationState: Unauthorized,
    ) : HelloClient(apiClients) {

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

        public val userProps: UserPropsApiClient? = apiClients[UserPropsApiClient::class]?.let { UserPropsApiClient(it, httpClient) }

        public var clickUpToken: Token? by Delegates.observable(null) { _, _, token ->
            clickUp = when (token) {
                null -> null
                else -> apiClients[ClickUpApiClient::class]?.let {
                    logger.info("Initializing ClickUp client with $token")
                    ClickUpApiClient(it, JsonHttpClient { installTokenAuth(token) })
//                    null
                }
            }
        }

        public var clickUp: ClickUpApiClient? = null
            private set

        public suspend fun logOut(): HelloClient =
            load(authorizationState.revokeTokens(httpClient), apiClients)
    }


    public companion object {

        private val logger by InlineLogging(HelloClient::class.simpleName)

        public suspend fun load(
            config: HelloClientConfig,
        ): HelloClient {
            logger.info("Config: $config")
            val openIDProvider = config.openIDProvider ?: return Anonymous(config.apiClients)
            val clientId = config.clientId ?: return Anonymous(config.apiClients)

            val metadata = openIDProvider.loadOpenIDConfiguration()
            val authServer = OAuth2AuthorizationServer.from(metadata)
            return load(authServer, clientId, config.apiClients)
        }

        public suspend fun load(
            authServer: OAuth2AuthorizationServer,
            clientId: String,
            apiClients: Map<KClass<out ApiClient>, String?>,
        ): HelloClient {
            val authorizationState = OAuth2AuthorizationState.compute(authServer, clientId)
            return load(authorizationState, apiClients)
        }

        private suspend fun load(
            authorizationState: OAuth2AuthorizationState,
            apiClients: Map<KClass<out ApiClient>, String?>,
        ): HelloClient {
            val helloClient = when (authorizationState) {
                is Unauthorized -> LoggedOut(apiClients, authorizationState)
                is Authorizing -> LoggedIn(apiClients, authorizationState.getTokens())
                is Authorized -> LoggedIn(apiClients, authorizationState)
            }
            // TODO do asynchronously
            if (helloClient is LoggedIn) {
                logger.info("Getting ClickUp API token")
                val clickUpApiToken = helloClient.userProps?.getProp<String?>("clickup.api-token")
                logger.info("ClickUp API token: $clickUpApiToken")
                helloClient.clickUpToken = clickUpApiToken?.let { AuthorizationToken(it) }
                logger.info("Set: $clickUpApiToken")
            }
            logger.debug("$helloClient")
            return helloClient
        }
    }
}
