package com.bkahlert.kommons.auth

import com.bkahlert.kommons.InstantAsEpochSeconds
import com.bkahlert.kommons.InstantAsEpochSecondsSerializer
import com.bkahlert.kommons.Now
import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.json.SingleElementUnwrappingJsonArraySerializer
import com.bkahlert.kommons.toMomentString
import com.bkahlert.kommons.uri.Uri
import io.ktor.util.decodeBase64String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.collections.Map.Entry
import kotlin.jvm.JvmInline
import kotlin.time.Duration

public typealias JWT = JsonWebToken

public interface JsonWebToken : Token {

    /** Decoded token header */
    public val header: JsonWebTokenHeader
        get() = usePart(0) { LenientJson.decodeFromString(it) }

    /** Decoded token payload */
    public val payload: JsonWebTokenPayload
        get() = usePart(1) { LenientJson.decodeFromString(it) }

    /** Token signature */
    public val signature: String
        get() = usePart(2) { LenientJson.decodeFromString(it) }

    public companion object {
        private fun String.decodeBase64Url(): String = replace("%2B", "+")
            .replace("%2F", "/")
            .replace("%3D", "=")
            .decodeBase64String()

        public fun <R> JsonWebToken.usePart(part: Int, block: (String) -> R): R {
            val parts = token.split('.').also { check(it.size == 3) { "Malformed token: $token" } }
            return block(parts[part].decodeBase64Url())
        }
    }
}

@JvmInline
@Serializable
public value class JsonWebTokenHeader(
    private val json: JsonObject,
) {
    public val keyId: String get() = json.required("kid")
    public val algorithm: String get() = json.required("alg")
}

@JsonClassDiscriminator("token_use")
public interface JsonWebTokenPayload {

    /**
     * Issuer Identifier for the Issuer of the response.
     * The iss value is a case-sensitive URL using the https scheme that contains scheme, host, and optionally,
     * port number and path components and no query or fragment components.
     */
    @SerialName("iss") public val issuerIdentifier: Uri

    /**
     * A locally unique and never reassigned identifier within the Issuer for the End-User, which is intended to be consumed by the Client,
     * for example `24400320` or `AItOawmwtWwcT0k51BayewNvutrJUqsvl6qs7A4`.
     *
     * It MUST NOT exceed 255 ASCII characters in length. The sub value is a case-sensitive string.
     */
    @SerialName("sub") public val subjectIdentifier: String

    /**
     * Expiration time on or after which the ID Token MUST NOT be accepted for processing.
     *
     * The processing of this parameter requires that the current date/time MUST be before the expiration date/time listed in the value.
     *
     * Implementers MAY provide for some small leeway, usually no more than a few minutes, to account for clock skew.
     * Its value is a JSON number representing the number of seconds from 1970-01-01T0:0:0Z as measured in UTC until the date/time.
     * See [RFC 3339](https://openid.net/specs/openid-connect-core-1_0.html#RFC3339) for details regarding date/times in general and UTC in particular.
     */
    @SerialName("exp") public val expiresAt: InstantAsEpochSeconds

    /**
     * Time at which the JWT was issued. Its value is a JSON number representing the number of
     * seconds from 1970-01-01T0:0:0Z as measured in UTC until the date/time.
     */
    @SerialName("iat") public val issuedAt: InstantAsEpochSeconds

    @SerialName("token_use") public val tokenUse: String

    /**
     * Time when the End-User authentication occurred.
     * Its value is a JSON number representing the number of seconds from 1970-01-01T0:0:0Z as
     * measured in UTC until the date/time.
     * - When a max_age request is made or when auth_time is requested as an Essential Claim,
     * then this Claim is REQUIRED;
     * - otherwise, its inclusion is OPTIONAL.
     *
     * (The auth_time Claim semantically corresponds to the [OpenID 2.0 PAPE](https://openid.net/specs/openid-connect-core-1_0.html#OpenID.PAPE) auth_time response parameter.)
     */
    @SerialName("auth_time") @Serializable(InstantAsEpochSecondsSerializer::class) public val authenticatedAt: InstantAsEpochSeconds

    @SerialName("origin_jti") public val origin_jti: String

    /**
     * Decoded [ID Token](https://openid.net/specs/openid-connect-core-1_0.html#IDToken)
     */
    @JvmInline
    @Serializable
    @SerialName("access")
    public value class IdTokenPayload(
        private val json: JsonObject,
    ) : JsonWebTokenPayload, Claims {
        override val entries: Set<Entry<String, JsonElement>> get() = json.entries

        @SerialName("iss") public override val issuerIdentifier: Uri get() = json.required("iss")
        @SerialName("sub") public override val subjectIdentifier: String get() = json.required("sub")

        /**
         * Audience(s) that this ID Token is intended for.
         * - It MUST contain the OAuth 2.0 `client_id` of the Relying Party as an audience value.
         * - It MAY also contain identifiers for other audiences.
         *
         * In the general case, the `aud` value is an array of case-sensitive strings.
         *
         * In the common special case when there is one audience, the aud value MAY be a single case-sensitive string.
         */
        @SerialName("aud") public val audiences: List<String> get() = json.required("aud", SingleElementUnwrappingJsonArraySerializer(String.serializer()))
        @SerialName("exp") public override val expiresAt: InstantAsEpochSeconds get() = json.required("exp", InstantAsEpochSecondsSerializer)
        @SerialName("iat") public override val issuedAt: InstantAsEpochSeconds get() = json.required("iat", InstantAsEpochSecondsSerializer)
        @SerialName("auth_time") public override val authenticatedAt: InstantAsEpochSeconds
            get() = json.required("auth_time", InstantAsEpochSecondsSerializer)

        /**
         * String value used to associate a Client session with an ID Token, and to mitigate replay attacks.
         * The value is passed through unmodified from the Authentication Request to the ID Token.
         * - If present in the ID Token, Clients MUST verify that the nonce Claim Value is equal to the value of
         * the nonce parameter sent in the Authentication Request.
         * - If present in the Authentication Request, Authorization Servers MUST include a nonce Claim in the ID Token with the
         * Claim Value being the nonce value sent in the Authentication Request.
         * Authorization Servers SHOULD perform no other processing on nonce values used. The nonce value is a case-sensitive string.
         */
        @SerialName("nonce") public val nonce: String? get() = json.optional("nonce")

        /**
         * String specifying an Authentication Context Class Reference value that identifies the
         * Authentication Context Class that the authentication performed satisfied.
         * The value "0" indicates the End-User authentication didn't meet the requirements of
         * [ISO/IEC 29115](https://openid.net/specs/openid-connect-core-1_0.html#ISO29115) level 1.
         *
         * Authentication using a long-lived browser cookie, for instance, is one example where the use of "level 0" is appropriate.
         * Authentications with level 0 SHOULD NOT be used to authorize access to any resource of any monetary value.
         * (This corresponds to the [OpenID 2.0 PAPE](https://openid.net/specs/openid-connect-core-1_0.html#OpenID.PAPE) nist_auth_level 0.)
         * An absolute URI or an [RFC 6711](https://openid.net/specs/openid-connect-core-1_0.html#RFC6711) registered name SHOULD be used as the acr value;
         * registered names MUST NOT be used with a different meaning than that which is registered.
         * Parties using this claim will need to agree upon the meanings of the values used, which may be context-specific.
         * The acr value is a case-sensitive string.
         */
        @SerialName("acr") public val authenticationContextClassReference: String? get() = json.optional("acr")

        /**
         * 	JSON array of strings that are identifiers for authentication methods used in the authentication.
         * 	For instance, values might indicate that both password and OTP authentication methods were used.
         * 	The definition of particular values to be used in the amr Claim is beyond the scope of this specification.
         * 	Parties using this claim will need to agree upon the meanings of the values used, which may be context-specific.
         * 	The amr value is an array of case-sensitive strings.
         */
        @SerialName("amr") public val authenticationMethodsReferences: List<String>? get() = json.optional("amr")

        /**
         * The party to which the ID Token was issued.
         * If present, it MUST contain the OAuth 2.0 Client ID of this party.
         * This Claim is only needed when the ID Token has a single audience value and that
         * the audience is different than the authorized party.
         *
         * It MAY be included even when the authorized party is the same as the sole audience.
         * The azp value is a case-sensitive string containing a StringOrURI value.
         */
        @SerialName("azp") public val authorizedParty: String? get() = json.optional("azp")


        // TODO check which fields below are not part of OpenID Connect Core spec but only belong to Cognito (UserInfo endpoint)
        @SerialName("token_use") public override val tokenUse: String get() = json.required("token_use")
        @SerialName("cognito:groups") public val cognitoGroups: List<String>? get() = json.optional("cognito:groups")
        @SerialName("cognito:username") public val cognitoUsername: String? get() = json.optional("cognito:username")
        @SerialName("identities") public val identities: List<Identity>? get() = json.optional("identities")
        @SerialName("jti") public val id: String get() = json.required("jti")
        @SerialName("origin_jti") public override val origin_jti: String get() = json.required("origin_jti")
    }

    @JvmInline
    @Serializable
    @SerialName("access")
    public value class AccessTokenPayload(
        private val json: JsonObject,
    ) : JsonWebTokenPayload {
        @SerialName("iss") public override val issuerIdentifier: Uri get() = json.required("iss")
        @SerialName("sub") public override val subjectIdentifier: String get() = json.required("sub")
        @SerialName("exp") public override val expiresAt: InstantAsEpochSeconds get() = json.required("exp", InstantAsEpochSecondsSerializer)
        @SerialName("iat") public override val issuedAt: InstantAsEpochSeconds get() = json.required("iat", InstantAsEpochSecondsSerializer)
        @SerialName("auth_time") public override val authenticatedAt: InstantAsEpochSeconds get() = json.required("auth_time", InstantAsEpochSecondsSerializer)

        @SerialName("client_id") public val client_id: String get() = json.required("client_id")
        @SerialName("scope") public val scope: String get() = json.required("scope")

        @SerialName("token_use") public override val tokenUse: String get() = json.required("token_use")
        @SerialName("username") public val username: String get() = json.required("username")
        @SerialName("version") public val version: Int get() = json.required("version")
        @SerialName("cognito:groups") public val cognitoGroups: List<String>? get() = json.optional("cognito:groups")
        @SerialName("jti") public val id: String get() = json.required("jti")
        @SerialName("origin_jti") public override val origin_jti: String get() = json.required("origin_jti")
    }
}

public val JsonWebTokenPayload.expiresIn: Duration
    get() = expiresAt - Now

public val JsonWebTokenPayload.expiresInDescription: String
    get() = when (expiresIn) {
        in Duration.ZERO..Duration.INFINITE -> "expires ${expiresIn.toMomentString()}" // "expires in 5 minutes"
        else -> "expired ${expiresIn.toMomentString()}" // "expired 5 minutes ago"
    }

public val JsonWebTokenPayload.issuedAgo: Duration
    get() = issuedAt - Now

public val JsonWebTokenPayload.issuedAgoDescription: String
    get() = when (issuedAgo) {
        in Duration.ZERO..Duration.INFINITE -> "issued ${issuedAgo.toMomentString()}" // "issued in 5 minutes"
        else -> "issued ${issuedAgo.toMomentString()}" // "expired 5 minutes ago"
    }

public val JsonWebTokenPayload.authenticatedAgo: Duration
    get() = authenticatedAt - Now

public val JsonWebTokenPayload.authenticatedAgoDescription: String
    get() = when (authenticatedAgo) {
        in Duration.ZERO..Duration.INFINITE -> "authenticated ${authenticatedAgo.toMomentString()}" // "authenticated in 5 minutes"
        else -> "authenticated ${authenticatedAgo.toMomentString()}" // "expired 5 minutes ago"
    }

@Serializable
public data class Identity(
    @SerialName("userId") public val userId: String,
    @SerialName("providerName") public val providerName: String,
    @SerialName("providerType") public val providerType: String,
    @SerialName("issuer") public val issuer: String?,
    @SerialName("primary") public val primary: Boolean,
    @SerialName("dateCreated") @Serializable(InstantAsEpochSecondsSerializer::class) public val dateCreated: InstantAsEpochSeconds,
)
