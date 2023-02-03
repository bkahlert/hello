package com.bkahlert.kommons.ktor

import com.bkahlert.kommons.auth.Token
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

public val TokenAuthPlugin: ClientPlugin<TokenAuthPluginConfig> =
    createClientPlugin("TokenAuthPlugin", ::TokenAuthPluginConfig) {
        val token = pluginConfig.token
        onRequest { request, _ ->
            request.header(HttpHeaders.Authorization, token?.token)
        }
    }

public class TokenAuthPluginConfig {
    public var token: Token? = null
}
