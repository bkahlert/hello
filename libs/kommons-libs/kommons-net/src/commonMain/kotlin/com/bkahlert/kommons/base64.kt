package com.bkahlert.kommons

import io.ktor.util.encodeBase64

/** URL-encodes the [ByteArray] in Base64 format. */
public fun ByteArray.encodeBase64Url(): String = encodeBase64()
    .replace('+', '-')
    .replace('/', '_')
    .dropLastWhile { it == '=' }
