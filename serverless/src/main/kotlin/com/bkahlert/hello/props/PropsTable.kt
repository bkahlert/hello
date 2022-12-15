package com.bkahlert.hello.props

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import com.bkahlert.kommons.logging.SLF4J

object PropsTable {

    private val logger by SLF4J

    val tableName by lazy { System.getenv("DYNAMODB_PROPS_TABLE") }

    val userId = "test-user"

    suspend fun <T> usingClient(block: suspend (DynamoDbClient) -> T): T =
        DynamoDbClient.fromEnvironment().use { block(it) }

    suspend fun getProp(id: String): String? {
        val request = GetItemRequest {
            key = mapOf<String, AttributeValue>(
//                "userId" to S(userId),
//                "propId" to S(id),
                "id" to S(id),
            )
            tableName = PropsTable.tableName
        }
        return usingClient { ddb ->
            val returnedItem = ddb.getItem(request)
            val attributes = returnedItem.item
            logger.info("Got attributes ${attributes?.keys}")
            attributes?.get("value")?.asSOrNull()
        }
    }
}
