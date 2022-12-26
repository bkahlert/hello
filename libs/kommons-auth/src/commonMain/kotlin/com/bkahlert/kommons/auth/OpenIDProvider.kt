package com.bkahlert.kommons.auth

/**
 * OpenID Provider
 * @see <a href="https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata">OpenID Connect Core 1.0</a>
 */
public data class OpenIDProvider(
    public val url: String,
) {
    /**
     * [OpenID Provider Configuration Information](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) URI
     */
    public val openIDConfigurationUri: String = "$url$WELL_KNOWN_PATH$OPEN_ID_CONFIGURATION_URI_SUFFIX"

    public companion object {
        /**
         * [Well-Known Uniform Resource Identifiers](https://www.rfc-editor.org/rfc/rfc5785) path
         */
        public const val WELL_KNOWN_PATH: String = "/.well-known/"

        /**
         * [OpenID Provider Configuration Information](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig)
         * [Well-Known URI Suffix](https://www.iana.org/assignments/well-known-uris/well-known-uris.xhtml)
         */
        public const val OPEN_ID_CONFIGURATION_URI_SUFFIX: String = "openid-configuration"
    }
}
