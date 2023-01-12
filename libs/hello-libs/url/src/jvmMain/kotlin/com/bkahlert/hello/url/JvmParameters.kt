package com.bkahlert.hello.url

import io.ktor.http.ParametersBuilderImpl
import io.ktor.util.StringValuesBuilder
import io.ktor.http.formUrlEncode as ktorFormUrlEncode

internal actual fun buildParameters(block: ParametersBuilder.() -> Unit): Parameters {
    val parametersBuilderImpl = ParametersBuilderImpl()
    val builder: ParametersBuilder = object : ParametersBuilder, StringValuesBuilder by parametersBuilderImpl {}
    builder.apply(block)
    return parametersBuilderImpl.build()
}

public actual interface ParametersBuilder : StringValuesBuilder

public actual typealias Parameters = io.ktor.http.Parameters

internal actual fun List<Pair<String, String?>>.formUrlEncode(): String = ktorFormUrlEncode()

internal actual val EmptyParameters: io.ktor.http.Parameters = Parameters.Empty

internal actual fun parseQueryString(query: String, startIndex: Int, limit: Int, decode: Boolean): io.ktor.http.Parameters =
    io.ktor.http.parseQueryString(query, startIndex, limit, decode)
