package com.bkahlert.aws.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

/**
 * Returns a [APIGatewayProxyResponseEvent]
 * with the body being the string-encoded [JsonArray] built using the specified [block], the specified [statusCode], and [headers].
 */
public inline fun jsonArrayResponse(
    statusCode: Int = 200,
    vararg headers: Pair<String, String>,
    block: JsonArrayBuilder.() -> Unit,
): APIGatewayProxyResponseEvent =
    jsonResponse(buildJsonArray(block), statusCode, *headers)

/**
 * Returns a [APIGatewayProxyResponseEvent]
 * with the body being the string-encoded [JsonObject] built using the specified [block], the specified [statusCode], and [headers].
 */
public inline fun jsonObjectResponse(
    statusCode: Int = 200,
    vararg headers: Pair<String, String>,
    block: JsonObjectBuilder.() -> Unit,
): APIGatewayProxyResponseEvent =
    jsonResponse(buildJsonObject(block), statusCode, *headers)

/**
 * Returns a [APIGatewayProxyResponseEvent]
 * with the body being the string-encoded [value], the specified [statusCode], and [headers].
 */
public fun jsonResponse(
    value: Number?,
    statusCode: Int = 200,
    vararg headers: Pair<String, String>,
): APIGatewayProxyResponseEvent =
    jsonResponse(JsonPrimitive(value), statusCode, *headers)

/**
 * Returns a [APIGatewayProxyResponseEvent]
 * with the body being the string-encoded [value], the specified [statusCode], and [headers].
 */
public fun jsonResponse(
    value: String?,
    statusCode: Int = 200,
    vararg headers: Pair<String, String>,
): APIGatewayProxyResponseEvent =
    jsonResponse(JsonPrimitive(value), statusCode, *headers)

/**
 * Returns a [APIGatewayProxyResponseEvent]
 * with the body being the string-encoded [value], the specified [statusCode], and [headers].
 */
public fun jsonResponse(
    value: Boolean?,
    statusCode: Int = 200,
    vararg headers: Pair<String, String>,
): APIGatewayProxyResponseEvent =
    jsonResponse(JsonPrimitive(value), statusCode, *headers)

/**
 * Returns a [APIGatewayProxyResponseEvent]
 * with the specified [statusCode], and [headers].
 */
public fun jsonResponse(
    @Suppress("UNUSED_PARAMETER") value: Nothing?,
    statusCode: Int = 200,
    vararg headers: Pair<String, String>,
): APIGatewayProxyResponseEvent =
    jsonResponse(JsonNull, statusCode, *headers)

/**
 * Returns a [APIGatewayProxyResponseEvent]
 * with the specified [statusCode], and [headers].
 */
public fun jsonResponse(
    statusCode: Int = 200,
    vararg headers: Pair<String, String>,
): APIGatewayProxyResponseEvent =
    jsonResponse(JsonNull, statusCode, *headers)

/**
 * Returns a [APIGatewayProxyResponseEvent]
 * with the body being the string-encoded [value], the specified [statusCode], and [headers].
 */
public fun jsonResponse(
    value: JsonElement,
    statusCode: Int = 200,
    vararg headers: Pair<String, String>,
): APIGatewayProxyResponseEvent =
    response(Json.encodeToString(value), statusCode, "Content-Type" to "application/json", *headers)

/**
 * Returns a [APIGatewayProxyResponseEvent]
 * with the specified [body], the specified [statusCode], and [headers].
 */
public fun response(
    statusCode: Int = 200,
    vararg headers: Pair<String, String>,
): APIGatewayProxyResponseEvent = APIGatewayProxyResponseEvent()
    .withStatusCode(statusCode)
    .withHeaders(headers.toMap())

/**
 * Returns a [APIGatewayProxyResponseEvent]
 * with the specified [body], the specified [statusCode], and [headers].
 */
public fun response(
    body: String?,
    statusCode: Int = 200,
    vararg headers: Pair<String, String>,
): APIGatewayProxyResponseEvent = APIGatewayProxyResponseEvent()
    .withStatusCode(statusCode)
    .withHeaders(headers.toMap())
    .withBody(body)
