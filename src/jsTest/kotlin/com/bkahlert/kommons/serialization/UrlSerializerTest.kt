@file:UseSerializers(UrlSerializer::class)

package com.bkahlert.kommons.serialization

import com.bkahlert.kommons.dom.URL
import com.bkahlert.kommons.text.quoted
import kotlinx.serialization.UseSerializers
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("unused", "SpellCheckingInspection")
class UrlSerializerTest : SerializerTest<URL>(
    UrlSerializer,
    "https://example.com".quoted to URL.parse("https://example.com"),
    "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7".quoted to URL.parse(
        "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
    ),
) {

    @Test
    @JsName("hostname")
    fun `should not add hostname to data URI`() {
        val dataUrl = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
        val url = URL.parse(dataUrl)
        assertEquals(dataUrl, url.toString())
    }
}
