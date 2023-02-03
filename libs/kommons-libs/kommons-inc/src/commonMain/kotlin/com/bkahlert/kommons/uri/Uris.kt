package com.bkahlert.kommons.uri

/**
 * Parses the specified [uriString] as a [Uri]
 * as specified in [RFC3986 Appendix B](https://www.rfc-editor.org/rfc/rfc3986#appendix-B).
 */
public fun Uri(uriString: CharSequence): Uri =
    Uri.parse(text = uriString)

/**
 * Whether this [Uri] is absolute as described
 * in [RFC3986 section 4.3](https://www.rfc-editor.org/rfc/rfc3986#section-4.3).
 */
public val Uri.isAbsolute: Boolean
    get() = defined(scheme)

/**
 * Returns the specified [uriReference] resolved to this base [Uri]
 * as described in [RFC3986 section 5](https://www.rfc-editor.org/rfc/rfc3986#section-5).
 */
@Suppress("FunctionName")
public fun Uri.resolve(uriReference: Uri, strict: Boolean = false): Uri {
    require(isAbsolute) { "Base URI must be absolute: $this" }
    val uriReferenceScheme = if (!strict && uriReference.scheme == scheme) null else uriReference.scheme

    val targetScheme: String?
    val targetAuthority: Authority?
    val targetPath: String
    val targetQuery: String?
    if (defined(uriReferenceScheme)) {
        targetScheme = uriReferenceScheme
        targetAuthority = uriReference.authority
        targetPath = remove_dot_segments(uriReference.path)
        targetQuery = uriReference.query
    } else {
        if (defined(uriReference.authority)) {
            targetAuthority = uriReference.authority
            targetPath = remove_dot_segments(uriReference.path)
            targetQuery = uriReference.query
        } else {
            if (uriReference.path == "") {
                targetPath = path
                targetQuery =
                    if (defined(uriReference.query)) uriReference.query
                    else query
            } else {
                targetPath =
                    if (uriReference.path.startsWith("/")) remove_dot_segments(uriReference.path)
                    else remove_dot_segments(merge(this, uriReference.path))
                targetQuery = uriReference.query
            }
            targetAuthority = authority
        }
        targetScheme = scheme
    }

    return Uri(
        scheme = targetScheme,
        authority = targetAuthority,
        path = targetPath,
        query = targetQuery,
        fragment = uriReference.fragment,
    )
}

/**
 * Returns the specified [uriReference] resolved to this base [Uri]
 * as described in [RFC3986 section 5](https://www.rfc-editor.org/rfc/rfc3986#section-5).
 */
public fun Uri.resolve(uriReference: CharSequence, strict: Boolean = false): Uri =
    resolve(Uri.parse(uriReference), strict = strict)

/**
 * Returns this [Uri] resolved to the specified [baseUri]
 * as described in [RFC3986 section 5](https://www.rfc-editor.org/rfc/rfc3986#section-5).
 */
public fun Uri.resolveTo(baseUri: Uri, strict: Boolean = false): Uri =
    baseUri.resolve(this, strict = strict)

private fun <T : Any> defined(value: T?) = value != null

/**
 * Merges a [relativePathReference] with the path of the base URI as described in
 * [RFC3986 section 5.2.3](https://www.rfc-editor.org/rfc/rfc3986#section-5.2.3).
 */
private fun merge(baseUri: Uri, relativePathReference: String): String {
    check(!relativePathReference.startsWith("/")) { "Relative path-reference is not relative: $relativePathReference" }
    return when {
        baseUri.authority != null && baseUri.path.isEmpty() -> "/$relativePathReference"
        baseUri.path.contains("/") -> baseUri.path.substringBeforeLast("/") + "/" + relativePathReference
        else -> relativePathReference
    }
}

/**
 * Removes the special `.` and `..` complete path segments from
 * the specified [path] as described in
 * [RFC3986 section 5.2.4](https://www.rfc-editor.org/rfc/rfc3986#section-5.2.4).
 */
@Suppress("FunctionName")
private fun remove_dot_segments(path: String): String {
    var inputBuffer = path
    var outputBuffer = ""
    while (inputBuffer.isNotEmpty()) {
        if (inputBuffer.startsWith("../") || inputBuffer.startsWith("./")) {
            inputBuffer = inputBuffer.substringAfter("/")
        } else {
            if (inputBuffer.startsWith("/./") || inputBuffer == "/.") {
                inputBuffer =
                    if (inputBuffer == "/.") "/"
                    else inputBuffer.removePrefix("/.")
            } else {
                if (inputBuffer.startsWith("/../") || inputBuffer == "/..") {
                    inputBuffer =
                        if (inputBuffer == "/..") "/"
                        else inputBuffer.removePrefix("/..")
                    outputBuffer = outputBuffer.substringBeforeLast("/", "")
                } else {
                    if (inputBuffer == "." || inputBuffer == "..") {
                        inputBuffer = ""
                    } else {
                        val firstPathSegment = inputBuffer.firstPathSegment()
                        inputBuffer = inputBuffer.removePrefix(firstPathSegment)
                        outputBuffer += firstPathSegment
                    }
                }
            }
        }
    }
    return outputBuffer
}

private fun String.firstPathSegment(): String {
    return if (startsWith("/")) {
        "/" + drop(1).firstPathSegment()
    } else {
        substringBefore("/")
    }
}
