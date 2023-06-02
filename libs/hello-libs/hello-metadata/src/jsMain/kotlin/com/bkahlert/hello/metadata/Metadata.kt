package com.bkahlert.hello.metadata

import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import io.ktor.http.encodeURLQueryComponent
import kotlinx.coroutines.await

private val logger = ConsoleLogger("hello.metadata")

public external interface Metadata {
    public val title: String
    public val favicon: String?
}

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
