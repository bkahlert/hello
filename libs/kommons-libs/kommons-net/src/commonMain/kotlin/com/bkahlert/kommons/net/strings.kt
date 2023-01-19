package com.bkahlert.kommons.net

import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.encodeToByteArray

/**
 * Encodes this character sequence using the specified [charset].
 */
internal fun CharSequence.encodeToByteArray(charset: Charset): ByteArray =
    charset.newEncoder().encodeToByteArray(this)
