package com.bkahlert.kommons.dom

import io.kotest.matchers.shouldBe
import io.ktor.http.Parameters
import io.ktor.http.parametersOf
import kotlin.test.Test

class ParametersTest {

    @Test fun build_with_parameters() {
        buildParameters(parametersOf("foo", emptyList())) { append("bar", "baz") } shouldBe Fixture
    }

    @Test fun build_without_parameters() {
        buildParameters { append("bar", "baz") } shouldBe parametersOf("bar" to listOf("baz"))
    }

    @Test fun append() {
        buildParameters { append("foo") } shouldBe parametersOf("foo" to emptyList())
    }

    @Test fun minus_operator() {
        Fixture - "foo" shouldBe parametersOf("bar" to listOf("baz"))
        Fixture - "bar" shouldBe parametersOf("foo" to emptyList())
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

    var parameters = Parameters.Empty
    @Test fun binding() {
        var raw by ::parameters
        var nullableExplicit: Int? by binding(::parameters, -1, null)
        var nonNullableExplicit: Int by binding(::parameters, -1, -2)

        raw shouldBe null
        nullableExplicit shouldBe null
        nonNullableExplicit shouldBe -2

        raw = "value"
        nullableExplicit = 42
        nonNullableExplicit = -1

        parameters shouldBe parametersOf(
            "raw" to listOf("value"),
            "nullable-explicit" to listOf("42"),
            "non-nullable-explicit" to emptyList(),
        )
    }

    companion object {
        val Fixture: Parameters = parametersOf("foo" to emptyList(), "bar" to listOf("baz"))
    }
}
