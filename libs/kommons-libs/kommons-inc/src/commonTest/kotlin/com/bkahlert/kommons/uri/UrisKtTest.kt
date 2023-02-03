package com.bkahlert.kommons.uri

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class UrisKtTest {

    @Test
    fun factory() = testAll {
        Uri(uriString = BASE_URI_STRING) shouldBe BASE_URI
    }


    /**
     * References resolution examples as described in [RFC3986 section 5.4.1](https://www.rfc-editor.org/rfc/rfc3986#section-5.4.1)
     */
    @Test
    fun resolve_normal_examples() = testAll(
        "g:h" to "g:h",
        "g" to "http://a/b/c/g",
        "./g" to "http://a/b/c/g",
        "g/" to "http://a/b/c/g/",
        "/g" to "http://a/g",
        "//g" to "http://g",
        "?y" to "http://a/b/c/d;p?y",
        "g?y" to "http://a/b/c/g?y",
        "#s" to "http://a/b/c/d;p?q#s",
        "g#s" to "http://a/b/c/g#s",
        "g?y#s" to "http://a/b/c/g?y#s",
        ";x" to "http://a/b/c/;x",
        "g;x" to "http://a/b/c/g;x",
        "g;x?y#s" to "http://a/b/c/g;x?y#s",
        "" to "http://a/b/c/d;p?q",
        "." to "http://a/b/c/",
        "./" to "http://a/b/c/",
        ".." to "http://a/b/",
        "../" to "http://a/b/",
        "../g" to "http://a/b/g",
        "../.." to "http://a/",
        "../../" to "http://a/",
        "../../g" to "http://a/g",
    ) { (relativeReference, targetUriString) ->
        val targetUri = Uri.parse(targetUriString)
        val uriReference = Uri.parse(relativeReference)

        BASE_URI.resolve(relativeReference) shouldBe targetUri
        BASE_URI.resolve(uriReference) shouldBe targetUri
        uriReference.resolveTo(BASE_URI) shouldBe targetUri
    }

    /**
     * References resolution examples as described in [RFC3986 section 5.4.2](https://www.rfc-editor.org/rfc/rfc3986#section-5.4.2)
     */
    @Test
    fun resolve_abnormal_examples() = testAll(
        "../../../g" to "http://a/g",
        "../../../../g" to "http://a/g",

        "/./g" to "http://a/g",
        "/../g" to "http://a/g",
        "g." to "http://a/b/c/g.",
        ".g" to "http://a/b/c/.g",
        "g.." to "http://a/b/c/g..",
        "..g" to "http://a/b/c/..g",

        "./../g" to "http://a/b/g",
        "./g/." to "http://a/b/c/g/",
        "g/./h" to "http://a/b/c/g/h",
        "g/../h" to "http://a/b/c/h",
        "g;x=1/./y" to "http://a/b/c/g;x=1/y",
        "g;x=1/../y" to "http://a/b/c/y",

        "g?y/./x" to "http://a/b/c/g?y/./x",
        "g?y/../x" to "http://a/b/c/g?y/../x",
        "g#s/./x" to "http://a/b/c/g#s/./x",
        "g#s/../x" to "http://a/b/c/g#s/../x",
    ) { (relativeReference, targetUriString) ->
        BASE_URI.resolve(relativeReference) shouldBe Uri.parse(targetUriString)
    }

    /**
     * References resolution strict examples as
     * described in [RFC3986 section 5.4.2](https://www.rfc-editor.org/rfc/rfc3986#section-5.4.2)
     */
    @Test
    fun resolve_reference_with_scheme() = testAll {
        BASE_URI.resolve("http:g", strict = true) shouldBe Uri.parse("http:g")
        BASE_URI.resolve("http:g", strict = false) shouldBe Uri.parse("http://a/b/c/g")
    }

    @Test
    fun resolve_absolute_uri() = testAll {
        val baseUri = Uri.parse("http://localhost:8080/?x#y")
        baseUri.resolve("https://a/b").toString().shouldBe("https://a/b")
        baseUri.resolve("/b").toString().shouldBe("http://localhost:8080/b")
    }
}

/** Example base [Uri] string as described in [RFC3986 section 5.4](https://www.rfc-editor.org/rfc/rfc3986#section-5.4) */
const val BASE_URI_STRING = "http://a/b/c/d;p?q"

val BASE_URI = Uri(scheme = "http", authority = Authority(userInfo = null, host = "a", port = null), path = "/b/c/d;p", query = "q")
