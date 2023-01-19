package com.bkahlert.kommons.net

import com.bkahlert.kommons.net.DataUri.Companion.DEFAULT_MEDIA_TYPE
import io.ktor.http.ContentType
import io.ktor.http.charset
import io.ktor.http.decodeURLPart
import io.ktor.http.quote
import io.ktor.http.withCharset
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets

/**
 * Data URI
 * as described in [RFC2397](https://www.rfc-editor.org/rfc/rfc2397).
 *
 * Treated as a [CharSequence], this URI yields the string representation
 * as specified in [RFC2397 section 2](https://www.rfc-editor.org/rfc/rfc2397#section-2).
 */
public data class DataUri(
    /** The internet media type of [data]. Implicitly defaults to [DEFAULT_MEDIA_TYPE]. */
    public val mediaType: ContentType?,
    /** The decoded data. */
    public val data: ByteArray,
) : Uri by GenericUri(scheme = "data", path = buildString {
    mediaType?.also {
        append(it.contentType)
        append("/")
        append(it.contentSubtype)
        it.parameters.forEach { (name, value, escapeValue) ->
            append(";")
            append(name)
            append("=")
            append(if (escapeValue) value.quote() else value)
        }
    }
    append(";base64")
    append(",")
    append(data.encodeBase64Url())
}) {
    private val string by lazy { buildString { append(this@DataUri) } }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DataUri

        if (mediaType != other.mediaType) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaType?.hashCode() ?: 0
        result = 31 * result + data.contentHashCode()
        return result
    }

    /**
     * Returns the string representation of this URI
     * as specified in [RFC2397 section 2](https://www.rfc-editor.org/rfc/rfc2397#section-2).
     */
    override fun toString(): String = string

    public companion object {

        /**
         * Default charset for [DataUri] instances
         * as described in [RFC2397 section 2](https://www.rfc-editor.org/rfc/rfc2397#section-2).
         */
        public val DEFAULT_CHARSET: Charset = kotlin.runCatching {
            Charset.forName("US-ASCII") // Surprisingly, not supported in Ktor/JS ...
        }.getOrDefault(Charsets.ISO_8859_1)

        /**
         * Default media type for [DataUri] instances
         * as described in [RFC2397 section 2](https://www.rfc-editor.org/rfc/rfc2397#section-2).
         */
        public val DEFAULT_MEDIA_TYPE: ContentType = ContentType.Text.Plain.withCharset(DEFAULT_CHARSET)

        /**
         * Regular expression for parsing [DataUri] instances
         * as described in [RFC2397 section 3](https://www.rfc-editor.org/rfc/rfc2397#section-3).
         */
        public val REGEX: Regex = Regex("^data:(?<mediaType>[-\\w]+/[-+\\w.]+(?:;\\w+=[-\\w]+)*)?(?<base64>;base64)?,(?<data>.*)")

        /**
         * Parses the specified [text] as a [DataUri]
         * as specified in [RFC3986 Appendix B](https://www.rfc-editor.org/rfc/rfc3986#appendix-B).
         */
        public fun parse(text: String): DataUri {
            val groupValues = requireNotNull(REGEX.matchEntire(text)) { "$text is no valid data URI" }.groupValues
            val mediaType = groupValues[1].takeIf { it.isNotEmpty() }?.let { ContentType.parse(it) }
            val charset = mediaType?.charset() ?: DEFAULT_CHARSET
            return DataUri(
                mediaType = mediaType,
                data = when (groupValues[2]) {
                    "" -> groupValues[3].decodeURLPart(charset = charset).encodeToByteArray(charset)
                    else -> groupValues[3].decodeBase64Url()
                },
            )
        }

        /**
         * Parses the specified [text] as a [Uri]
         * as specified in [RFC3986 Appendix B](https://www.rfc-editor.org/rfc/rfc3986#appendix-B).
         */
        public fun parseOrNull(text: String): DataUri? = kotlin.runCatching { parse(text) }.getOrNull()

        private fun ByteArray.encodeBase64Url(): String = encodeBase64()
            .dropLastWhile { it == '=' }
            .replace("+", "%2B")
            .replace("/", "%2F")

        private fun String.decodeBase64Url(): ByteArray = replace("%2B", "+")
            .replace("%2F", "/")
            .replace("%3D", "=")
            .decodeBase64Bytes()
    }
}
