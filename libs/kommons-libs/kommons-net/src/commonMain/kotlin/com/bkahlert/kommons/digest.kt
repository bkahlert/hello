package com.bkahlert.kommons

import io.ktor.util.Digest
import io.ktor.utils.io.core.toByteArray

/**
 * Returns the specified [string] digested
 * using the `MD5` hash function.
 */
public expect fun md5(string: String): String

/**
 * Returns the specified [string] digested
 * using the `SHA-1` hash function.
 */
public suspend fun sha1(string: String): ByteArray =
    digest("SHA-1", string)

/**
 * Returns the specified [bytes] digested
 * using the `SHA-1` hash function.
 */
public suspend fun sha1(bytes: ByteArray): ByteArray =
    digest("SHA-1", bytes)

/**
 * Returns the specified [string] digested
 * using the `SHA-256` hash function.
 */
public suspend fun sha256(string: String): ByteArray =
    digest("SHA-256", string)

/**
 * Returns the specified [bytes] digested
 * using the `SHA-256` hash function.
 */
public suspend fun sha256(bytes: ByteArray): ByteArray =
    digest("SHA-256", bytes)

private suspend fun digest(name: String, string: String): ByteArray =
    digest(name, string.toByteArray())

private suspend fun digest(name: String, bytes: ByteArray): ByteArray {
    val digest = Digest(name)
    digest += bytes
    return digest.build()
}
