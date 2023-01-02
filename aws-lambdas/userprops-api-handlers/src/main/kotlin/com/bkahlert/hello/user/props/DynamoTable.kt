package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

object DynamoTable {
    val tableName: String by lazy { System.getenv("TABLE_NAME") }
    val partitionKey: String by lazy { System.getenv("PARTITION_KEY") }
    val sortKey: String by lazy { System.getenv("SORT_KEY") }

    fun JsonObject.filterKeys(): Map<String, JsonElement> =
        filterKeys { it != partitionKey && it != sortKey }

    fun <V> Map<String, V>.filterKeys(): Map<String, V> =
        filterKeys { it != partitionKey && it != sortKey }

    /**
     * Runs the specified [block] using a [DynamoDbClient]
     * created [DynamoDbClient.fromEnvironment].
     */
    suspend fun <T> usingClient(block: suspend (DynamoDbClient) -> T): T =
        DynamoDbClient.fromEnvironment().use { block(it) }
}

private val SortKeyRegex by lazy { Regex("[a-zA-Z0-9_.-]{1,1024}") }

/**
 * Returns the specified [key] as a [S]
 * if it's a valid sort key, or throws an [IllegalArgumentException] otherwise.
 *
 * @see <a href="https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/HowItWorks.NamingRulesDataTypes.html#HowItWorks.DataTypes.String">Amazon DynamoDB Developer Guide</a>
 */
fun requireValidSortKey(key: String): S {
    require(SortKeyRegex.matches(key)) { "$key is no valid sort key" }
    return S(key)
}

fun AttributeValue.toJsonElement(): JsonElement =
    Json.parseToJsonElement(asS())

fun Map<String, AttributeValue>.toJsonObject(): JsonObject = buildJsonObject {
    forEach { (key: String, attr: AttributeValue) ->
        put(key, attr.toJsonElement())
    }
}
