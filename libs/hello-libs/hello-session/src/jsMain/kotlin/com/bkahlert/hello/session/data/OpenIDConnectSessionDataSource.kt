package com.bkahlert.hello.session.data

import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.kommons.auth.OpenIDProvider
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.dom.uri
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.oauth.AuthorizationCodeFlowState
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.resolve
import kotlinx.browser.window

public class OpenIDConnectSessionDataSource(
    private val openIDProviderUrl: Uri,
    private val clientId: String,
) : SessionDataSource {

    public constructor(environment: Environment) : this(
        openIDProviderUrl = window.location.uri.resolve(environment.search(label = "OpenID Provider URL", keySubstring = "PROVIDER_URL")),
        clientId = environment.search(label = "Unable to find client ID", keySubstring = "CLIENT_ID"),
    )

    override suspend fun load(): Session {
        val block: suspend () -> Session = {
            AuthorizationCodeFlowState.resolve(
                openIDProvider = OpenIDProvider(openIDProviderUrl),
                clientId = clientId,
            )
        }
        return console.grouping("${OpenIDConnectSessionDataSource::class.simpleName}: ${::load.name}", block = block)
    }
}
