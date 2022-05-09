package com.bkahlert.kommons.dom

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.http.Parameters
import io.ktor.http.URLParserException
import kotlin.test.Test

class URLTest {

    @Test fun parse_url() {
        URL.parse("http://example.com") shouldBe URL("http", "example.com")
        URL.parse("https://example.com/") shouldBe URL("https", "example.com", path = listOf(""))
        URL.parse("http://localhost:8080/#debug") shouldBe URL("http", "localhost", 8080, fragment = Parameters.build { append("debug") })
        URL.parse("ftp://example.com/path") shouldBe URL("ftp", "example.com", null, listOf("path"))
    }

    @Test fun parse_dataUrl() {
        URL.parse("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7") shouldBe URL(
            schema = "data",
            path = listOf("image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"),
        )
    }

    @Test fun fragment() {
        URL.parse("http://example.com").fragment shouldBe Parameters.Empty
        URL.parse("https://example.com/#foo=bar").fragment shouldBe Parameters.build { append("foo", "bar") }
        URL.parse("https://example.com/#foo=bar&foo=baz").fragment shouldBe Parameters.build { appendAll("foo", listOf("bar", "baz")) }
        URL.parse("https://example.com/#foo[]=bar&foo[]=baz").fragment shouldBe Parameters.build { appendAll("foo[]", listOf("bar", "baz")) }
        URL.parse("https://example.com/#foo=bar&baz").fragment shouldBe Parameters.build { append("foo", "bar");appendAll("baz", emptyList()) }
    }

    @Test fun throw_on_nonsense() {
        shouldThrow<URLParserException> {
            URL.parse(":nonsense:/@nonsense@").also {
                println(it.host)
                println(it.schema)
                println(it.path)
            }
        }
    }

    @Test fun serialize() {
        URL.parse("http://example.com").toString() shouldBe "http://example.com"
        URL.parse("https://example.com/").toString() shouldBe "https://example.com/"
        URL.parse("http://localhost:8080/#debug").toString() shouldBe "http://localhost:8080/#debug"
        URL.parse("ftp://example.com/path").toString() shouldBe "ftp://example.com/path"
        URL.parse("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7")
            .toString() shouldBe "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
    }

    @Test fun compare() {
        URL.parse("http://example.com") shouldBe URL.parse("http://example.com")
        URL.parse("https://example.com/") shouldBe URL.parse("https://example.com/")
        URL.parse("ftp://example.com/path") shouldBe URL.parse("ftp://example.com/path")
        URL.parse("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7") shouldBe URL.parse("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7")
    }
}
