package com.bkahlert.hello.environment.data

import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.kommons.ktor.JsonHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

public data class DynamicEnvironmentDataSource(
    public val uri: String = "environment.json",
) : EnvironmentDataSource {

    public suspend fun load(httpClient: HttpClient): Environment {
        val values = httpClient.get(uri) { expectSuccess = true }.body<JsonObject>().mapValues {
            when (val value = it.value) {
                is JsonPrimitive -> value.content
                else -> value.toString()
            }
        }
        return Environment(values)
    }

    override suspend fun load(): Environment = load(JsonHttpClient())
}
