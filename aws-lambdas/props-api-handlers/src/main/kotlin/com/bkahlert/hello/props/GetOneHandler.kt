package com.bkahlert.hello.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.bkahlert.hello.props.DynamoTable.filterKeys

class GetOneHandler : EventHandler() {
    override suspend fun handleEvent(input: APIGatewayProxyRequestEvent, context: Context): GatewayResponse {
        val logger = context.logger
        logger.log("user: ${input.userId}")

        val id: String = checkNotNull(input.pathParameters?.get("id")) { "ID missing" }
        logger.log("Getting data for input parameter:$id")

        return when (val output = getData(input.requiredUserId, id)) {
            null -> GatewayResponse("", 404)
            else -> GatewayResponse(output)
        }
    }

    private suspend fun getData(userId: String, id: String): String? {

        val getItemRequest = GetItemRequest {
            key = mapOf<String, AttributeValue>(
                DynamoTable.partitionKey to S(userId),
                DynamoTable.sortKey to requireValidSortKey(id),
            )
            tableName = DynamoTable.tableName
        }

        return DynamoTable.usingClient { ddb ->
            ddb.getItem(getItemRequest)
                .item
                ?.filterKeys()
                ?.toJsonObject()
                ?.encodeToString()
        }
    }
}
