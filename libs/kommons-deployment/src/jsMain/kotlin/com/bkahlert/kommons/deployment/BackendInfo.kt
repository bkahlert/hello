package com.bkahlert.kommons.deployment

import com.bkahlert.kommons.debug.entries
import com.bkahlert.kommons.debug.parseJson
import com.bkahlert.kommons.deployment.gen.info

private val config by lazy {
    info.parseJson().entries.map { it[1] }.associate {
        with(it.asDynamic()) {
            key.unsafeCast<String>() to value.unsafeCast<String>()
        }
    }
}

private val apiHost: String by lazy { "${config["sls-hello-dev-DomainNameHttp"]}" }
private val apiUrl: String by lazy { "https://$apiHost" }
private val hostedUiUrl: String by lazy { config["sls-hello-dev-HostedUiUrl"]!! }
private val userinfoEndpoint by lazy { "$hostedUiUrl/oauth2/userInfo" }

public object BackendInfo {
    public val apiHost: String = "${config["sls-hello-dev-DomainNameHttp"]}"
    public val apiUrl: String = "https://$apiHost"
    public val hostedUiUrl: String = config["sls-hello-dev-HostedUiUrl"]!!
    public val userinfoEndpoint: String = "$hostedUiUrl/oauth2/userInfo"
    public val clientId: String = config["sls-hello-dev-WebAppClientID"]!!
}
