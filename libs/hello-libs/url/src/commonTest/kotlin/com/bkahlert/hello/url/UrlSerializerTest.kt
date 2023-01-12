package com.bkahlert.hello.url

import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

@Suppress("unused", "SpellCheckingInspection")
class UrlSerializerTest : SerializerTest<URL>(
    UrlSerializer,
    "https://example.com".quoted to URL.parse("https://example.com"),
    "https://example.com/path?query".quoted to URL.parse("https://example.com/path?query"),
    "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7".quoted to URL.parse(
        "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
    ),
) {

    @Test
    @JsName("hostname")
    fun `should not add hostname to data URI`() {
        val dataUrl = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
        val url = URL.parse(dataUrl)
        url.toString() shouldBe dataUrl
    }
}

private val String.quoted get() = "\"$this\""
