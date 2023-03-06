package com.bkahlert.kommons

/**
 * Returns the specified [string] digested
 * using the `MD5` hash function.
 */
public actual fun md5(string: String): String =
    string.encodeToByteArray().inputStream().md5Checksum()
