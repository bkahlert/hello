package com.bkahlert.hello.props

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S

object DynamoTable {
    val tableName: String by lazy { System.getenv("TABLE_NAME") }
    val partitionKey: String by lazy { System.getenv("PARTITION_KEY") }
    val sortKey: String by lazy { System.getenv("SORT_KEY") }

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
