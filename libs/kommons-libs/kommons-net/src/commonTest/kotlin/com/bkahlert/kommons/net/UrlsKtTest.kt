package com.bkahlert.kommons.net

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import io.ktor.http.Url
import io.ktor.util.PlatformUtils
import kotlin.test.Test

class UrlsKtTest {

    @Test
    fun to_uri() = testAll {
        Url.completeUrl().toUri() shouldBe Uri.completeUri()
        Url.emptyUrl().toUri() shouldBe when (PlatformUtils.IS_BROWSER) {
            true -> Uri.parse("http://localhost:9876") // 🤷
            else -> Uri.parse("http://localhost")
        }
    }

    @Test
    fun to_url() = testAll {
        Uri.completeUri().toUrl() shouldBe Url.completeUrl()
        Uri.emptyUri().toUrl() shouldBe Url.emptyUrl()
    }


    @Test fun build_with_url() {
        Url.build(Url.completeUrl()) {
            port = 42
        }.toString() shouldBe "https://username:password@example.com:42/poo/par?qoo=qar&qaz#foo=far&faz"
    }

    @Test fun build_without_url() {
        Url.build {
            port = 42
        }.toString() shouldBe "http://localhost:42"
    }


    @Test
    fun div() = testAll {
        Url.completeUrl() / "path-segment" shouldBe Url("https://username:password@example.com:8080/poo/par/path-segment?qoo=qar&qaz#foo=far&faz")
        Url.emptyUrl() / "path-segment" shouldBe Url("/path-segment")
    }
}
