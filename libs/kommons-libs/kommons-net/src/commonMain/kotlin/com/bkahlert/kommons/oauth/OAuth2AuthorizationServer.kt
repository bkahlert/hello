package com.bkahlert.kommons.oauth

import com.bkahlert.kommons.auth.OpenIDProvider
import com.bkahlert.kommons.auth.OpenIDProviderMetadata
import com.bkahlert.kommons.uri.Uri

/**
 * OAuth 2.0 authorization server
 */
public data class OAuth2AuthorizationServer(
    /** @see [OpenIDProviderMetadata.issuer] */
    val issuer: OpenIDProvider,
    /** @see [OpenIDProviderMetadata.authorizationEndpoint] */
    val authorizationEndpoint: Uri,
    /** @see [OpenIDProviderMetadata.tokenEndpoint] */
    val tokenEndpoint: Uri,
    /**
     * Optional token revocation endpoint.
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7009">OAuth 2.0 Token Revocation</a>
     */
    val revocationEndpoint: Uri?,
) {
    public companion object {

        /**
         * Creates a [OAuth2AuthorizationServer] from the specified [metadata].
         */
        public fun from(
            openIDProvider: OpenIDProvider,
            metadata: OpenIDProviderMetadata,
        ): OAuth2AuthorizationServer = OAuth2AuthorizationServer(
            issuer = openIDProvider,
            authorizationEndpoint = metadata.authorizationEndpoint,
            tokenEndpoint = requireNotNull(metadata.tokenEndpoint),
            revocationEndpoint = metadata.revocationEndpoint,
        )
    }
}
