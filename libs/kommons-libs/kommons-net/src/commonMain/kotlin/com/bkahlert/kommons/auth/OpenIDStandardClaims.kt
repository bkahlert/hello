package com.bkahlert.kommons.auth

import com.bkahlert.kommons.InstantAsEpochSeconds
import com.bkahlert.kommons.InstantAsEpochSecondsSerializer
import com.bkahlert.kommons.uri.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.collections.Map.Entry
import kotlin.jvm.JvmInline

/**
 * [OpenID Standard Claims](https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims)
 * than can be requested to be returned either in the [UserInfo] response, per
 * [Section 5.3.2](https://openid.net/specs/openid-connect-core-1_0.html#UserInfoResponse), or in the [IdToken], per
 * [Section 2](https://openid.net/specs/openid-connect-core-1_0.html#IDToken).
 */
public interface OpenIDStandardClaims {
    /**
     * Identifier for the End-User at the Issuer.
     */
    @SerialName(SUB_CLAIM_NAME)
    public val subjectIdentifier: String?

    /**
     * End-User's full name in displayable form including all name parts, possibly including titles and suffixes,
     * ordered according to the End-User's locale and preferences.
     */
    @SerialName(NAME_CLAIM_NAME)
    public val name: String?

    /**
     * Given name(s) or first name(s) of the End-User. Note that in some cultures, people can have multiple given names;
     * all can be present, with the names being separated by space characters.
     */
    @SerialName(GIVEN_NAME_CLAIM_NAME)
    public val givenName: String?

    /**
     * Surname(s) or last name(s) of the End-User. Note that in some cultures, people can have multiple family names or no family name;
     * all can be present, with the names being separated by space characters.
     */
    @SerialName(FAMILY_NAME_CLAIM_NAME)
    public val familyFame: String?

    /**
     * Middle name(s) of the End-User. Note that in some cultures, people can have multiple middle names;
     * all can be present, with the names being separated by space characters. Also, note that in some cultures, middle names are not used.
     */
    @SerialName(MIDDLE_NAME_CLAIM_NAME)
    public val middleName: String?

    /**
     * Casual name of the End-User that may or may not be the same as the given_name.
     * For instance, a nickname value of Mike might be returned alongside a given_name value of Michael.
     */
    @SerialName(NICKNAME_CLAIM_NAME)
    public val nickname: String?

    /**
     * Shorthand name by which the End-User wishes to be referred to at the RP, such as `janedoe` or `j.doe`.
     * This value MAY be any valid JSON string including special characters such as `@`, `/`, or `whitespace`.
     * The RP MUST NOT rely upon this value being unique, as discussed in [Section 5.7](https://openid.net/specs/openid-connect-core-1_0.html#ClaimStability).
     */
    @SerialName(PREFERRED_USERNAME_CLAIM_NAME)
    public val preferredUsername: String?

    /**
     * URL of the End-User's profile page. The contents of this Web page SHOULD be about the End-User.
     */
    @SerialName(PROFILE_CLAIM_NAME)
    public val profile: Uri?

    /**
     * URL of the End-User's profile picture. This URL MUST refer to an image file (for example, a PNG, JPEG, or GIF image file),
     * rather than to a Web page containing an image. Note that this URL SHOULD specifically reference a profile photo of the
     * End-User suitable for displaying when describing the End-User, rather than an arbitrary photo taken by the End-User.
     */
    @SerialName(PICTURE_CLAIM_NAME)
    public val picture: Uri?

    /**
     * URL of the End-User's Web page or blog. This Web page SHOULD contain information published by the End-User or an
     * organization that the End-User is affiliated with.
     */
    @SerialName(WEBSITE_CLAIM_NAME)
    public val website: Uri?

    /**
     * End-User's preferred email address. Its value MUST conform to the [RFC 5322](https://openid.net/specs/openid-connect-core-1_0.html#RFC5322)
     * addr-spec syntax. The RP MUST NOT rely upon this value being unique,
     * as discussed in [Section 5.7](https://openid.net/specs/openid-connect-core-1_0.html#ClaimStability).
     */
    @SerialName(EMAIL_CLAIM_NAME)
    public val email: String?

    /**
     * True, if the End-User's email address has been verified; otherwise false.
     * When this Claim Value is true, this means that the OP took affirmative steps to ensure that this email address
     * was controlled by the End-User at the time the verification was performed.
     * The means by which an email address is verified is context-specific,
     * and dependent upon the trust framework or contractual agreements within which the parties are operating.
     */
    @SerialName(EMAIL_VERIFIED_CLAIM_NAME)
    public val emailVerified: Boolean?

    /**
     * End-User's gender. The Values defined by this specification are female and male.
     * Other values MAY be used when neither of the defined values is applicable.
     */
    @SerialName(GENDER_CLAIM_NAME)
    public val gender: String?

    /**
     * End-User's birthday, represented as an [ISO 8601:2004](https://openid.net/specs/openid-connect-core-1_0.html#ISO8601-2004) `YYYY-MM-DD` format.
     * The year MAY be 0000, indicating that it's omitted.
     * To represent only the year, `YYYY` format is allowed. Note that, depending on the underlying platform's date-related function,
     * providing just a year can result in a varying month and day,
     * so the implementers need to take this factor into account to correctly process the dates.
     */
    @SerialName(BIRTHDATE_CLAIM_NAME)
    public val birthdate: String?

    /**
     * String from [zoneinfo](https://openid.net/specs/openid-connect-core-1_0.html#zoneinfo) time zone database representing the End-User's time zone.
     * For example, Europe/Paris or America/Los_Angeles.
     */
    @SerialName(ZONEINFO_CLAIM_NAME)
    public val zoneInfo: String?

    /**
     * End-User's locale, represented as a [BCP47](https://openid.net/specs/openid-connect-core-1_0.html#RFC5646) language tag.
     * This is typically an [ISO 639-1 Alpha-2](https://openid.net/specs/openid-connect-core-1_0.html#ISO639-1)
     * language code in lowercase and an [ISO 3166-1 Alpha-2](https://openid.net/specs/openid-connect-core-1_0.html#ISO3166-1)
     * country code in uppercase, separated by a dash.
     * For example, en-US or fr-CA.
     * As a compatibility note, some implementations have used an underscore as the separator rather than a dash, for example, en_US;
     * relying parties MAY choose to accept this locale syntax as well.
     */
    @SerialName(LOCALE_CLAIM_NAME)
    public val locale: String?

    /**
     * End-User's preferred telephone number. [E.164](https://openid.net/specs/openid-connect-core-1_0.html#E.164) is RECOMMENDED
     * as the format of this Claim, for example, +1 (425) 555-1212 or +56 (2) 687 2400.
     * If the phone number contains an extension, it's RECOMMENDED that the extension be represented using the
     * [RFC 3966](https://openid.net/specs/openid-connect-core-1_0.html#RFC3966) extension syntax, for example, +1 (604) 555-1234;ext=5678.
     */
    @SerialName(PHONE_NUMBER_CLAIM_NAME)
    public val phoneNumber: String?

    /**
     * True, if the End-User's phone number has been verified; otherwise false.
     * When this Claim Value is true,
     * this means that the OP took affirmative steps to ensure that this phone number was controlled by the End-User at the time the verification was performed.
     * The means by which a phone number is verified is context-specific,
     * and dependent upon the trust framework or contractual agreements within which the parties are operating.
     * When true, the phone_number Claim MUST be in [E.164](https://openid.net/specs/openid-connect-core-1_0.html#E.164)
     * format and any extensions MUST be represented in RFC 3966 format.
     */
    @SerialName(PHONE_NUMBER_VERIFIED_CLAIM_NAME)
    public val phoneNumberVerified: Boolean?

    /**
     * End-User's preferred postal address.
     * The value of the address member is a JSON [RFC4627](https://openid.net/specs/openid-connect-core-1_0.html#RFC4627)
     * structure containing some or all of the members defined in [Section 5.1.1](https://openid.net/specs/openid-connect-core-1_0.html#AddressClaim).
     */
    @SerialName(ADDRESS_CLAIM_NAME)
    public val address: JsonElement?

    /**
     * Time the End-User's information was last updated.
     * Its value is a JSON number representing the number of seconds from 1970-01-01T0:0:0Z as measured in UTC until the date/time.
     */
    @SerialName(UPDATED_AT_CLAIM_NAME)
    @Serializable(InstantAsEpochSecondsSerializer::class)
    public val updatedAt: InstantAsEpochSeconds?

    public companion object {
        /** Name for claim `sub` */
        public const val SUB_CLAIM_NAME: String = "sub"

        /** Name for claim `name` */
        public const val NAME_CLAIM_NAME: String = "name"

        /** Name for claim `given_name` */
        public const val GIVEN_NAME_CLAIM_NAME: String = "given_name"

        /** Name for claim `family_name` */
        public const val FAMILY_NAME_CLAIM_NAME: String = "family_name"

        /** Name for claim `middle_name` */
        public const val MIDDLE_NAME_CLAIM_NAME: String = "middle_name"

        /** Name for claim `nickname` */
        public const val NICKNAME_CLAIM_NAME: String = "nickname"

        /** Name for claim `preferred_username` */
        public const val PREFERRED_USERNAME_CLAIM_NAME: String = "preferred_username"

        /** Name for claim `profile` */
        public const val PROFILE_CLAIM_NAME: String = "profile"

        /** Name for claim `picture` */
        public const val PICTURE_CLAIM_NAME: String = "picture"

        /** Name for claim `website` */
        public const val WEBSITE_CLAIM_NAME: String = "website"

        /** Name for claim `email` */
        public const val EMAIL_CLAIM_NAME: String = "email"

        /** Name for claim `email_verified` */
        public const val EMAIL_VERIFIED_CLAIM_NAME: String = "email_verified"

        /** Name for claim `gender` */
        public const val GENDER_CLAIM_NAME: String = "gender"

        /** Name for claim `birthdate` */
        public const val BIRTHDATE_CLAIM_NAME: String = "birthdate"

        /** Name for claim `zoneinfo` */
        public const val ZONEINFO_CLAIM_NAME: String = "zoneinfo"

        /** Name for claim `locale` */
        public const val LOCALE_CLAIM_NAME: String = "locale"

        /** Name for claim `phone_number` */
        public const val PHONE_NUMBER_CLAIM_NAME: String = "phone_number"

        /** Name for claim `phone_number_verified` */
        public const val PHONE_NUMBER_VERIFIED_CLAIM_NAME: String = "phone_number_verified"

        /** Name for claim `address` */
        public const val ADDRESS_CLAIM_NAME: String = "address"

        /** Name for claim `updated_at` */
        public const val UPDATED_AT_CLAIM_NAME: String = "updated_at"
    }
}

public interface Claims : OpenIDStandardClaims, Map<String, JsonElement> {
    override val entries: Set<Entry<String, JsonElement>>
    override val keys: Set<String> get() = entries.map { it.key }.toSet()
    override val size: Int get() = entries.size
    override val values: Collection<JsonElement> get() = entries.map { it.value }

    @Suppress("ReplaceSizeZeroCheckWithIsEmpty")
    override fun isEmpty(): Boolean = size == 0
    override fun get(key: String): JsonElement? = entries.firstOrNull { it.key == key }?.value
    override fun containsValue(value: JsonElement): Boolean = entries.any { it.value == value }
    override fun containsKey(key: String): Boolean = entries.any { it.key == key }

    public override val subjectIdentifier: String? get() = optional(OpenIDStandardClaims.SUB_CLAIM_NAME)
    public override val name: String? get() = optional(OpenIDStandardClaims.NAME_CLAIM_NAME)
    public override val givenName: String? get() = optional(OpenIDStandardClaims.GIVEN_NAME_CLAIM_NAME)
    public override val familyFame: String? get() = optional(OpenIDStandardClaims.FAMILY_NAME_CLAIM_NAME)
    public override val middleName: String? get() = optional(OpenIDStandardClaims.MIDDLE_NAME_CLAIM_NAME)
    public override val nickname: String? get() = optional(OpenIDStandardClaims.NICKNAME_CLAIM_NAME)
    public override val preferredUsername: String? get() = optional(OpenIDStandardClaims.PREFERRED_USERNAME_CLAIM_NAME)
    public override val profile: Uri? get() = optional(OpenIDStandardClaims.PROFILE_CLAIM_NAME)
    public override val picture: Uri? get() = optional(OpenIDStandardClaims.PICTURE_CLAIM_NAME)
    public override val website: Uri? get() = optional(OpenIDStandardClaims.WEBSITE_CLAIM_NAME)
    public override val email: String? get() = optional(OpenIDStandardClaims.EMAIL_CLAIM_NAME)
    public override val emailVerified: Boolean? get() = optional(OpenIDStandardClaims.EMAIL_VERIFIED_CLAIM_NAME)
    public override val gender: String? get() = optional(OpenIDStandardClaims.GENDER_CLAIM_NAME)
    public override val birthdate: String? get() = optional(OpenIDStandardClaims.BIRTHDATE_CLAIM_NAME)
    public override val zoneInfo: String? get() = optional(OpenIDStandardClaims.ZONEINFO_CLAIM_NAME)
    public override val locale: String? get() = optional(OpenIDStandardClaims.LOCALE_CLAIM_NAME)
    public override val phoneNumber: String? get() = optional(OpenIDStandardClaims.PHONE_NUMBER_CLAIM_NAME)
    public override val phoneNumberVerified: Boolean? get() = optional(OpenIDStandardClaims.PHONE_NUMBER_VERIFIED_CLAIM_NAME)
    public override val address: JsonElement? get() = optional(OpenIDStandardClaims.ADDRESS_CLAIM_NAME)
    public override val updatedAt: InstantAsEpochSeconds? get() = optional(OpenIDStandardClaims.UPDATED_AT_CLAIM_NAME, InstantAsEpochSecondsSerializer)
}

@JvmInline
@Serializable
public value class JsonObjectClaims(
    private val jsonObject: JsonObject,
) : Claims {
    override val entries: Set<Entry<String, JsonElement>> get() = jsonObject.entries
    override val keys: Set<String> get() = jsonObject.keys
    override val size: Int get() = jsonObject.size
    override val values: Collection<JsonElement> get() = jsonObject.values
    override fun isEmpty(): Boolean = jsonObject.isEmpty()
    override fun get(key: String): JsonElement? = jsonObject[key]
    override fun containsValue(value: JsonElement): Boolean = jsonObject.containsValue(value)
    override fun containsKey(key: String): Boolean = jsonObject.containsKey(key)
}

public fun Claims(map: Map<String, JsonElement>): JsonObjectClaims =
    JsonObjectClaims(JsonObject(map))

public fun Claims(vararg pairs: Pair<String, JsonElement>): JsonObjectClaims =
    Claims(pairs.toMap())
