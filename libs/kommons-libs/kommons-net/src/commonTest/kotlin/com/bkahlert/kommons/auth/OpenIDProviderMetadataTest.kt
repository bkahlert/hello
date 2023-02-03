package com.bkahlert.kommons.auth

import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.test.testAll
import com.bkahlert.kommons.uri.Uri
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlin.test.Test

class OpenIDProviderMetadataTest {

    // language=json
    private val json = """
            {
              "authorization_endpoint": "https://provider.example.com/oauth2/authorize",
              "id_token_signing_alg_values_supported": [
                "RS256"
              ],
              "issuer": "https://provider.example.com",
              "jwks_uri": "https://provider.example.com/.well-known/jwks.json",
              "response_types_supported": [
                "code",
                "token"
              ],
              "scopes_supported": [
                "openid",
                "email",
                "phone",
                "profile"
              ],
              "subject_types_supported": [
                "public"
              ],
              "token_endpoint": "https://provider.example.com/oauth2/token",
              "token_endpoint_auth_methods_supported": [
                "client_secret_basic",
                "client_secret_post"
              ],
              "userinfo_endpoint": "https://provider.example.com/oauth2/userInfo"
            }
        """.trimIndent()

    @Test
    fun deserialize() = testAll {
        LenientJson.decodeFromString<OpenIDProviderMetadata>(json) shouldBe OpenIDProviderMetadata(
            issuer = Uri.parse("https://provider.example.com"),
            authorizationEndpoint = Uri.parse("https://provider.example.com/oauth2/authorize"),
            tokenEndpoint = Uri.parse("https://provider.example.com/oauth2/token"),
            userinfoEndpoint = Uri.parse("https://provider.example.com/oauth2/userInfo"),
            jwksUri = Uri.parse("https://provider.example.com/.well-known/jwks.json"),
            registrationEndpoint = null,
            scopesSupported = listOf("openid", "email", "phone", "profile"),
            responseTypesSupported = listOf("code", "token"),
            responseModesSupported = listOf("query", "fragment"),
            grantTypesSupported = listOf("authorization_code", "implicit"),
            acrValuesSupported = null,
            subjectTypesSupported = listOf("public"),
            idTokenSigningAlgValuesSupported = listOf("RS256"),
            idTokenEncryptionAlgValuesSupported = null,
            idTokenEncryptionEncValuesSupported = null,
            userinfoSigningAlgValuesSupported = null,
            userinfoEncryptionAlgValuesSupported = null,
            userinfoEncryptionEncValuesSupported = null,
            requestObjectSigningAlgValuesSupported = null,
            requestObjectEncryptionAlgValuesSupported = null,
            requestObjectEncryptionEncValuesSupported = null,
            tokenEndpointAuthMethodsSupported = listOf("client_secret_basic", "client_secret_post"),
            tokenEndpointAuthSigningAlgValuesSupported = null,
            revocationEndpoint = null,
            displayValuesSupported = null,
            claimTypesSupported = null,
            claimsSupported = null,
            serviceDocumentation = null,
            claimsLocalesSupported = null,
            uiLocalesSupported = null,
            claimsParameterSupported = false,
            requestParameterSupported = false,
            requestUriParameterSupported = true,
            requireRequestUriRegistration = false,
            opPolicyUri = null,
            opTosUri = null,
        )
    }
}
