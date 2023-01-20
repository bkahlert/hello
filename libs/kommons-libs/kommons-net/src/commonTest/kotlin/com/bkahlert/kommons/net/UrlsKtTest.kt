package com.bkahlert.kommons.net

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import io.ktor.http.Url
import kotlin.test.Test

class UrlsKtTest {

    @Test
    fun to_url() = testAll {
        Uri.parse("https://username:password@example.com:8080/poo/par?qoo=qar&qaz#foo=far&faz").toUrl()
            .shouldBe(Url("https://username:password@example.com:8080/poo/par?qoo=qar&qaz#foo=far&faz"))
    }
}
