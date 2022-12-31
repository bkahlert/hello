package com.bkahlert.hello.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent

class DeleteOneHandler : EventHandler() {
    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): GatewayResponse {
        val logger = context.logger
        logger.log("user: ${event.userId}")
        logger.log("Inside software.amazon.awscdk.examples.lambda: getOneItem " + event.javaClass + " data:" + event)

        val id: String = checkNotNull(event.pathParameters?.get("id")) { "ID missing" }
        logger.log("updating data for input parameter:$id")

        return when (val output = deleteItem(event.requiredUserId, id)) {
            null -> GatewayResponse("", 404)
            else -> GatewayResponse(output)
        }
    }

    private suspend fun deleteItem(
        userId: String,
        id: String,
    ): String? {

        val deleteItemRequest = DeleteItemRequest {
            key = mapOf<String, AttributeValue>(
                DynamoTable.partitionKey to S(userId),
                DynamoTable.sortKey to requireValidSortKey(id),
            )
            tableName = DynamoTable.tableName
        }

        return DynamoTable.usingClient { ddb ->
            ddb.deleteItem(deleteItemRequest)
                .attributes
                ?.filterKeys { it != DynamoTable.partitionKey && it != DynamoTable.sortKey }
                ?.toJsonObject()
                ?.encodeToString()
        }
    }
}
