package com.bkahlert.kommons.net

/**
 * URI
 * as described in [RFC3986](https://www.rfc-editor.org/rfc/rfc3986).
 */
public open class Uri(
    /** [Schema component](https://www.rfc-editor.org/rfc/rfc3986#section-3.1) */
    public val scheme: String? = null,
    /** [Authority component](https://www.rfc-editor.org/rfc/rfc3986#section-3.2) */
    public val authority: Authority? = null,
    /** [Path component](https://www.rfc-editor.org/rfc/rfc3986#section-3.3) */
    public val path: String = "",
    /** [Query component](https://www.rfc-editor.org/rfc/rfc3986#section-3.4) */
    public val query: String? = null,
    /** [Fragment component](https://www.rfc-editor.org/rfc/rfc3986#section-3.5) */
    public val fragment: String? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Uri

        if (scheme != other.scheme) return false
        if (authority != other.authority) return false
        if (path != other.path) return false
        if (query != other.query) return false
        if (fragment != other.fragment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = scheme?.hashCode() ?: 0
        result = 31 * result + (authority?.hashCode() ?: 0)
        result = 31 * result + path.hashCode()
        result = 31 * result + (query?.hashCode() ?: 0)
        result = 31 * result + (fragment?.hashCode() ?: 0)
        return result
    }

    /**
     * Returns the string representation of this URI
     * as specified in [RFC3986 section 5.3](https://www.rfc-editor.org/rfc/rfc3986#section-5.3).
     */
    override fun toString(): String = buildString {
        scheme?.also {
            append(it)
            append(":")
        }
        authority?.also {
            append("//")
            append(it)
        }
        append(path)
        query?.also {
            append("?")
            append(it)
        }
        fragment?.also {
            append("#")
            append(it)
        }
    }

    public companion object {

        /**
         * Regular expression for parsing [Uri] instances
         * as specified in [RFC3986 Appendix B](https://www.rfc-editor.org/rfc/rfc3986#appendix-B).
         */
        public val REGEX: Regex = Regex("^(?:(?<scheme>[^:/?#]+):)?(?://(?<authority>[^/?#]*))?(?<path>[^?#]*)(?:\\?(?<query>[^#]*))?(?:#(?<fragment>.*))?")

        /**
         * Parses the specified [text] as a [Uri]
         * as specified in [RFC3986 Appendix B](https://www.rfc-editor.org/rfc/rfc3986#appendix-B).
         */
        public fun parse(text: String): Uri {
            val groupValues = requireNotNull(REGEX.matchEntire(text)) { "$text is no valid URI" }.groupValues
            return when (val scheme = groupValues[1].takeIf { it.isNotEmpty() }) {
                "data" -> DataUri.parse(text)
                else -> Uri(
                    scheme = scheme,
                    authority = groupValues[2].takeIf { it.isNotEmpty() }?.let { Authority.parse(it) },
                    path = groupValues[3],
                    query = groupValues[4].takeIf { it.isNotEmpty() },
                    fragment = groupValues[5].takeIf { it.isNotEmpty() },
                )
            }
        }

        /**
         * Parses the specified [text] as a [Uri]
         * as specified in [RFC3986 Appendix B](https://www.rfc-editor.org/rfc/rfc3986#appendix-B).
         */
        public fun parseOrNull(text: String): Uri? = kotlin.runCatching { parse(text) }.getOrNull()
    }
}
