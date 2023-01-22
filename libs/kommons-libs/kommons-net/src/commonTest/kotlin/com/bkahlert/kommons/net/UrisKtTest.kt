package com.bkahlert.kommons.net

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.http.Parameters
import io.ktor.util.toMap
import kotlin.test.Test

class UrisKtTest {

    @Test
    fun user_info() = testAll {
        Uri.completeUri().userInfo shouldBe "username:password"
        Uri.emptyUri().userInfo shouldBe null
    }

    @Test
    fun host() = testAll {
        Uri.completeUri().host shouldBe "example.com"
        Uri.emptyUri().host shouldBe null
    }

    @Test
    fun port() = testAll {
        Uri.completeUri().port shouldBe 8080
        Uri.emptyUri().port shouldBe null
    }


    @Test
    fun path_segments() = testAll {
        Uri.completeUri().pathSegments.shouldContainExactly("", "poo", "par")
        Uri.emptyUri().pathSegments.shouldContainExactly("")
    }

    @Test
    fun query_parameters() = testAll {
        Uri.completeUri().queryParameters.toMap().shouldContainExactly(mapOf("qoo" to listOf("qar"), "qaz" to emptyList()))
        Uri.emptyUri().queryParameters shouldBe Parameters.Empty
    }

    @Test
    fun fragment_parameters() = testAll {
        Uri.completeUri().fragmentParameters.toMap().shouldContainExactly(mapOf("foo" to listOf("far"), "faz" to emptyList()))
        Uri.emptyUri().fragmentParameters shouldBe Parameters.Empty
    }


    @Test
    fun div() = testAll {
        Uri.completeUri() / "path-segment" shouldBe Uri.parse("https://username:password@example.com:8080/poo/par/path-segment?qoo=qar&qaz#foo=far&faz")
        Uri.emptyUri() / "path-segment" shouldBe Uri.parse("/path-segment")
    }
}
