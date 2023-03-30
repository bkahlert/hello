package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Provides [DynamoDbClient] instances.
 */
typealias DynamoDbClientProvider = suspend () -> DynamoDbClient

/**
 * Runs the specified [block] using a [DynamoDbClient]
 * provided using this [DynamoDbClientProvider].
 */
suspend fun <T> DynamoDbClientProvider.use(block: suspend (DynamoDbClient) -> T): T =
    invoke().use { block(it) }

/**
 * An [DynamoDbClientProvider] that is intended to
 * be used with the specified [tableName], [partitionKey], and [sortKey].
 */
class DynamoDbTable(
    ddbProvider: DynamoDbClientProvider = { DynamoDbClient.fromEnvironment() },
    val tableName: String = System.getenv("TABLE_NAME"),
    val partitionKey: String = System.getenv("PARTITION_KEY"),
    val sortKey: String = System.getenv("SORT_KEY"),
    val valueKey: String = System.getenv("VALUE_KEY") ?: "value",
) : DynamoDbClientProvider by ddbProvider


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

/**
 * Returns a [JsonElement] decoded
 * from the string represented by
 * this [AttributeValue].
 */
fun AttributeValue.toJsonElement(): JsonElement =
    Json.parseToJsonElement(asS())
