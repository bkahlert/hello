package com.bkahlert.hello.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.QueryRequest
import aws.sdk.kotlin.services.dynamodb.model.ReturnValue
import aws.sdk.kotlin.services.dynamodb.model.UpdateItemRequest
import com.bkahlert.aws.dynamodb.requireValidSortKey
import com.bkahlert.aws.dynamodb.usingClient
import com.bkahlert.kommons.logging.SLF4J
import com.bkahlert.kommons.text.checkNotBlank

object PropsTable {

    private val logger by SLF4J
    private val tableName by lazy { System.getenv("DYNAMODB_PROPS_TABLE") }
    private val userId = "test-user"

    private val userIdAttributeName = "userId"
    private val propIdAttributeName = "propId"
    private val valueAttributeName = "value"

    private fun keyOf(propId: String): Map<String, AttributeValue> = mapOf<String, AttributeValue>(
        userIdAttributeName to S(userId),
        propIdAttributeName to requireValidSortKey(checkNotBlank(propId)),
    )

    suspend fun getProps(): Map<String, String?>? {
        val request = QueryRequest {
            expressionAttributeNames = mapOf(
                "#$userIdAttributeName" to userIdAttributeName,
            )
            expressionAttributeValues = mapOf(
                ":$userIdAttributeName" to S(userId),
            )
            keyConditionExpression = "#$userIdAttributeName = :$userIdAttributeName"
            tableName = PropsTable.tableName
        }
        return usingClient { ddb ->
            val returnedItem = ddb.query(request)
            val props = returnedItem.items?.associate {
                it.getValue(propIdAttributeName).asS() to it[valueAttributeName]?.asSOrNull()
            }
            logger.info("Got attributes ${props?.keys}")
            props
        }
    }

    suspend fun getProp(propId: String): String? {
        val request = GetItemRequest {
            key = keyOf(propId)
            tableName = PropsTable.tableName
        }
        return usingClient { ddb ->
            val returnedItem = ddb.getItem(request)
            val attributes = returnedItem.item
            logger.info("Got attributes ${attributes?.keys}")
            attributes?.get(valueAttributeName)?.asSOrNull()
        }
    }

    suspend fun updateProp(propId: String, value: String): String? {
        val request = UpdateItemRequest {
            key = keyOf(propId)
            returnValues = ReturnValue.UpdatedOld
            expressionAttributeNames = mapOf(
                "#$valueAttributeName" to valueAttributeName,
            )
            expressionAttributeValues = mapOf(
                ":$valueAttributeName" to S(value),
            )
            updateExpression = "SET #$valueAttributeName = :$valueAttributeName"
            tableName = PropsTable.tableName
        }
        return usingClient { ddb ->
            val returnedItem = ddb.updateItem(request)
            val attributes = returnedItem.attributes
            logger.info("Old attributes ${attributes?.keys}")
            attributes?.get(valueAttributeName)?.asSOrNull()
        }
    }

    suspend fun deleteProp(propId: String): String? {
        val request = DeleteItemRequest {
            key = keyOf(propId)
            returnValues = ReturnValue.AllOld
            tableName = PropsTable.tableName
        }
        return usingClient { ddb ->
            val returnedItem = ddb.deleteItem(request)
            val attributes = returnedItem.attributes
            logger.info("Old attributes ${attributes?.keys}")
            attributes?.get(valueAttributeName)?.asSOrNull()
        }
    }
}
