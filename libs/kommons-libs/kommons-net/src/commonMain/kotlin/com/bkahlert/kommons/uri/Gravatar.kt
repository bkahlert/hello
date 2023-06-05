package com.bkahlert.kommons.uri

import com.bkahlert.kommons.md5

/**
 * Returns the specified [email] (as is or already MD5 hashed) converted to
 * a [Gravatar profile request URL](https://en.gravatar.com/site/implement/profiles/).
 */
public fun GravatarProfileUri(
    email: String,
    format: GravatarProfileDataFormat? = null,
): Uri = Uri(
    scheme = "https",
    authority = Authority(null, "www.gravatar.com", null),
    path = "/${hash(email)}${format?.suffix ?: ""}",
    query = null,
    fragment = null
)

// https://en.gravatar.com/67231e1d9fd1940036d14cf37c796a2c.json
public enum class GravatarProfileDataFormat(
    /** The suffix to append to the email hash. */
    public val suffix: String,
) {
    /** [JSON Profile Data](https://en.gravatar.com/site/implement/profiles/json/) */
    JSON(".json"),

    /** [XML Profile Data](https://en.gravatar.com/site/implement/profiles/xml/) */
    XML(".xml"),

    /** [PHP Profile Data](https://en.gravatar.com/site/implement/profiles/php/) */
    PHP(".php"),

    /** [VCF/vCard Profile Data](https://en.gravatar.com/site/implement/profiles/vcf/) */
    VCF(".vcf"),

    /** [QR Codes](https://en.gravatar.com/site/implement/profiles/qr/) */
    QR(".qr"),
    ;
}

/**
 * Returns the specified [email] (as is or already MD5 hashed) converted to
 * a [Gravatar image request URL](https://en.gravatar.com/site/implement/images/)
 * with the optional [size].
 */
public fun GravatarImageUri(
    email: String,
    size: Int? = null,
): Uri = Uri(
    scheme = "https",
    authority = Authority(null, "www.gravatar.com", null),
    path = "/avatar/${hash(email)}",
    query = size?.let { "s=$it" },
    fragment = null
)

private fun hash(email: String) = if (email.length == 32 && !email.contains('@')) email else md5(email)
