package com.bkahlert.hello.fritz2.components.metadata

import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import io.ktor.client.fetch.AbortController
import io.ktor.http.encodeURLQueryComponent
import kotlinx.coroutines.await
import org.w3c.fetch.RequestCache
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestMode
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response

/**
 * Fetches the [Uri] and returns the [Response].
 *
 * The operation is canceled automatically if the coroutine is canceled.
 */
public suspend fun Uri.fetch(
    method: String? = undefined,
    headers: dynamic = undefined,
    body: dynamic = undefined,
    referrer: String? = undefined,
    referrerPolicy: dynamic = undefined,
    mode: RequestMode? = undefined,
    credentials: RequestCredentials? = undefined,
    cache: RequestCache? = undefined,
    redirect: RequestRedirect? = undefined,
    integrity: String? = undefined,
    keepalive: Boolean? = undefined,
    window: Any? = undefined,
): Response {
    val abortController: AbortController = js("new AbortController()").unsafeCast<AbortController>()
    val fetchInit = RequestInit(
        method = method,
        headers = headers,
        body = body,
        referrer = referrer,
        referrerPolicy = referrerPolicy,
        mode = mode,
        credentials = credentials,
        cache = cache,
        redirect = redirect,
        integrity = integrity,
        keepalive = keepalive,
        window = window,
    ).apply { asDynamic().signal = abortController.signal }
    return kotlinx.browser.window.fetch(toString(), fetchInit)
        .runCatching { await() }
        .getOrElse {
            abortController.abort()
            throw it
        }
}

public external interface Metadata {
    public val title: String
    public val favicon: String?
}

private val logger = ConsoleLogger("hello.metadata")

/**
 * Fetches the [Metadata] data of the website the [Uri] points to.
 */
public suspend fun Uri.fetchMetadata(): Metadata? {
    val endpoint = Uri("https://4ecj3qm42uam74iijdyslbxzj40fvtht.lambda-url.us-east-1.on.aws/?domain=:domain")
    val domain = toString().encodeURLQueryComponent()
    logger.info("Fetching metadata for $domain")
    val uri = endpoint.replace(Regex(":domain"), domain).toUriOrNull() ?: return null
    return uri.fetch().takeIf { it.ok }?.json()?.await()?.unsafeCast<Metadata>()
}
