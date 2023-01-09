package com.bkahlert.hello.client

import com.bkahlert.kommons.auth.OpenIDProvider
import kotlin.reflect.KClass

public data class HelloClientConfig(
    val openIDProvider: OpenIDProvider?,
    val clientId: String?,
    val apiClients: Map<KClass<out ApiClient>, String?>,
) {
    public constructor(
        openIDProvider: OpenIDProvider?,
        clientId: String?,
        vararg apiClients: Pair<KClass<out ApiClient>, String?>,
    ) : this(openIDProvider, clientId, mapOf(*apiClients))

    public companion object {
        public fun fromEnvironment(environment: Environment): HelloClientConfig = HelloClientConfig(
            openIDProvider = environment["USER_POOL_PROVIDER_URL"]?.let { OpenIDProvider(it) },
            clientId = environment["USER_POOL_CLIENT_ID"],
            ClickUpApiClient::class to environment["CLICK_UP_API_ENDPOINT"],
            UserInfoApiClient::class to environment["USER_INFO_API_ENDPOINT"],
            UserPropsApiClient::class to environment["USER_PROPS_API_ENDPOINT"],
        )
    }
}
