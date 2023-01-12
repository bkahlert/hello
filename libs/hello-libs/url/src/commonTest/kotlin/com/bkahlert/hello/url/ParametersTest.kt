package com.bkahlert.hello.url

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ParametersTest {

    @Test fun build_with_parameters() {
        buildParameters(buildParameters { appendAll("foo", emptyList()) }) { append("bar", "baz") } shouldBe Fixture
    }

    @Test fun build_without_parameters() {
        buildParameters { append("bar", "baz") } shouldBe buildParameters(EmptyParameters) { append("bar", "baz") }
    }

    @Test fun append() {
        buildParameters { append("foo") } shouldBe buildParameters { appendAll("foo", emptyList()) }
    }

    @Test fun minus_operator() {
        Fixture - "foo" shouldBe buildParameters { appendAll("bar", listOf("baz")) }
        Fixture - "bar" shouldBe buildParameters { appendAll("foo", emptyList()) }
        Fixture - "baz" shouldBe Fixture
    }


    @Test fun get_valid_default_deserializer() {
        Fixture.getValid("foo", "default", "missing") shouldBe "default"
        Fixture.getValid("bar", "default", "missing") shouldBe "baz"
        Fixture.getValid("baz", "default", "missing") shouldBe "missing"
    }

    @Test fun get_valid_custom_deserializer() {
        Fixture.getValid("foo", '+', '-') { it.first() } shouldBe '+'
        Fixture.getValid("bar", '+', '-') { it.first() } shouldBe 'b'
        Fixture.getValid("baz", '+', '-') { it.first() } shouldBe '-'
    }

    @Test fun get_valid_failing_deserializer() {
        Fixture.getValid("foo", '+', '-') { error("") } shouldBe '+'
        Fixture.getValid("bar", '+', '-') { error("") } shouldBe '+'
        Fixture.getValid("baz", '+', '-') { error("") } shouldBe '-'
    }

    @Test fun form_url_encode() {
        Fixture.formUrlEncode() shouldBe "foo&bar=baz"
    }

    var parameters = EmptyParameters
    @Test fun binding() {
        var raw by ::parameters
        var nullableExplicit: Int? by binding(::parameters, -1, null)
        var nonNullableExplicit: Int by binding(::parameters, -1, -2)

        raw shouldBe null
        nullableExplicit shouldBe null
        nonNullableExplicit shouldBe -2

        @Suppress("UNUSED_VALUE")
        raw = "value"
        @Suppress("UNUSED_VALUE")
        nullableExplicit = 42
        @Suppress("UNUSED_VALUE")
        nonNullableExplicit = -1

        parameters shouldBe buildParameters {
            appendAll("raw", listOf("value"))
            appendAll("nullableExplicit", listOf("42"))
            appendAll("nonNullableExplicit", emptyList())
        }
    }

    companion object {
        val Fixture: Parameters = buildParameters {
            appendAll("foo", emptyList())
            appendAll("bar", listOf("baz"))
        }
    }
}
