package com.bkahlert.kommons

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DigestKtTest {

    @Test
    fun md5() = runTest {
        md5("string") shouldBe checksums["MD5"]
    }

    @Test
    fun sha1() = runTest {
        sha1("string").toHexadecimalString() shouldBe checksums["SHA-1"]
    }

    @Test
    fun sha256() = runTest {
        sha256("string").toHexadecimalString() shouldBe checksums["SHA-256"]
    }
}

internal fun byteArrayOf(vararg bytes: Int) =
    bytes.map { it.toByte() }.toByteArray()

internal val hashBytes = mapOf(
    "MD5" to byteArrayOf(
        0xb4, 0x5c, 0xff, 0xe0, 0x84, 0xdd, 0x3d, 0x20,
        0xd9, 0x28, 0xbe, 0xe8, 0x5e, 0x7b, 0x0f, 0x21,
    ),
    "SHA-1" to byteArrayOf(
        0xec, 0xb2, 0x52, 0x04, 0x4b, 0x5e, 0xa0, 0xf6, 0x79, 0xee,
        0x78, 0xec, 0x1a, 0x12, 0x90, 0x47, 0x39, 0xe2, 0x90, 0x4d,
    ),
    "SHA-256" to byteArrayOf(
        0x47, 0x32, 0x87, 0xf8, 0x29, 0x8d, 0xba, 0x71, 0x63, 0xa8, 0x97, 0x90, 0x89, 0x58, 0xf7, 0xc0,
        0xea, 0xe7, 0x33, 0xe2, 0x5d, 0x2e, 0x02, 0x79, 0x92, 0xea, 0x2e, 0xdc, 0x9b, 0xed, 0x2f, 0xa8,
    ),
)

@Suppress("SpellCheckingInspection")
internal val checksums = mapOf(
    "MD5" to "b45cffe084dd3d20d928bee85e7b0f21",
    "SHA-1" to "ecb252044b5ea0f679ee78ec1a12904739e2904d",
    "SHA-256" to "473287f8298dba7163a897908958f7c0eae733e25d2e027992ea2edc9bed2fa8",
)
