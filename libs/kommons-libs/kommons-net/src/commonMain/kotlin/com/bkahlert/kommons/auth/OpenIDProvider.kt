package com.bkahlert.kommons.auth

import com.bkahlert.kommons.ktor.JsonHttpClient
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.div
import com.bkahlert.kommons.uri.pathSegments
import com.bkahlert.kommons.uri.toUri
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * OpenID Provider
 * @see <a href="https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata">OpenID Connect Core 1.0</a>
 */
public class OpenIDProvider(
    url: Uri,
) {
    public val url: Uri = Uri(
        scheme = url.scheme,
        authority = url.authority,
        path = url.path,
        query = null,
        fragment = null,
    )

    private val wellKnownUri = url.toString().removeSuffix("/").plus(WELL_KNOWN_PATH.removeSuffix("/")).toUri()

    /**
     * [OpenID Provider Configuration Information](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) URI
     */
    public val openIDConfigurationUri: Uri = wellKnownUri / OPEN_ID_CONFIGURATION_URI_SUFFIX

    /**
     * Loads the [OpenID Provider Configuration Information](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig)
     */
    public suspend fun loadOpenIDConfiguration(
        cache: OpenIDProviderMetadataCache? = null,
        missingTokenRevocationFallback: (OpenIDProviderMetadata) -> Uri? = DefaultMissingTokenRevocationFallback,
    ): OpenIDProviderMetadata {
        val compute: suspend () -> OpenIDProviderMetadata = {
            val metadata: OpenIDProviderMetadata = openIdClient.get(openIDConfigurationUri.toString()).body()
            when (metadata.revocationEndpoint) {
                null -> metadata.copy(revocationEndpoint = missingTokenRevocationFallback(metadata))
                else -> metadata
            }
        }
        return when (cache) {
            null -> compute()
            else -> cache.getOrCompute(this, compute)
        }
    }

    public companion object {

        /** Client used to load [OpenIDProviderMetadata]. */
        private val openIdClient by lazy { JsonHttpClient() }

        /**
         * [Well-Known Uniform Resource Identifiers](https://www.rfc-editor.org/rfc/rfc5785) path
         */
        public const val WELL_KNOWN_PATH: String = "/.well-known/"

        /**
         * [OpenID Provider Configuration Information](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig)
         * [Well-Known URI Suffix](https://www.iana.org/assignments/well-known-uris/well-known-uris.xhtml)
         */
        public const val OPEN_ID_CONFIGURATION_URI_SUFFIX: String = "openid-configuration"

        /**
         * Strategy used to compute a missing [OpenIDProviderMetadata.revocationEndpoint].
         */
        public val DefaultMissingTokenRevocationFallback: (OpenIDProviderMetadata) -> Uri? = { meta ->
            when (val tokenEndpoint = meta.tokenEndpoint) {
                null -> null
                else -> Uri(
                    scheme = tokenEndpoint.scheme,
                    authority = tokenEndpoint.authority,
                    path = tokenEndpoint.pathSegments.joinToString("/") { segment -> segment.takeUnless { it == "token" } ?: "revoke" },
                    query = tokenEndpoint.query,
                    fragment = tokenEndpoint.fragment,
                )
            }
        }
    }
}
