package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.jsonResponse
import com.bkahlert.aws.lambda.requiredUserId
import com.bkahlert.aws.lambda.response
import com.bkahlert.aws.lambda.userId
import com.bkahlert.hello.user.props.DynamoTable.filterKeys
import kotlinx.serialization.json.JsonObject

class DeleteOneHandler : APIGatewayProxyRequestEventHandler() {

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("user: ${event.userId}")
        logger.debug("Inside software.amazon.awscdk.examples.lambda: getOneItem " + event.javaClass + " data:" + event)

        val id: String = checkNotNull(event.pathParameters?.get("id")) { "ID missing" }
        logger.info("updating data for input parameter:$id")

        return when (val output = deleteItem(event.requiredUserId, id)) {
            null -> response(404)
            else -> jsonResponse(output)
        }
    }

    private suspend fun deleteItem(
        userId: String,
        id: String,
    ): JsonObject? {

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
                ?.filterKeys()
                ?.toJsonObject()
        }
    }
}
