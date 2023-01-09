package com.bkahlert.hello.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.contentType
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

public abstract class ApiClient(
    public val endpoint: String,
    public val httpClient: HttpClient,
) {
    public val url: String = endpointToUrl(endpoint)

    public companion object {
        public fun endpointToUrl(endpoint: String): String =
            endpoint.takeUnless { it.startsWith("/") } ?: "${window.location.protocol}//${window.location.hostname}$endpoint"
    }
}


public class ClickUpApiClient(
    endpoint: String,
    httpClient: HttpClient,
) : ApiClient(endpoint, httpClient) {
    public suspend fun clickUp(): String {
        val response = httpClient.get(url)
        return response.bodyAsText()
    }
}


public class UserInfoApiClient(
    endpoint: String,
    httpClient: HttpClient,
) : ApiClient(endpoint, httpClient) {
    public suspend fun info(): JsonObject = httpClient.get(url).body()
}


public class UserPropsApiClient(
    endpoint: String,
    httpClient: HttpClient,
) : ApiClient(endpoint, httpClient) {

    public suspend fun getProps(): String {
        val response = httpClient.get(url)
        return response.bodyAsText()
    }

    public suspend inline fun <reified T> getProp(id: String): T =
        httpClient.get("$url/$id").body<JsonElement>().let { Json.decodeFromJsonElement(it) }

    public suspend fun setProp(id: String, value: JsonObject): String {
        val response = httpClient.patch("$url/$id") {
            contentType(Application.Json)
            setBody(value)
        }
        return response.bodyAsText()
    }
}
