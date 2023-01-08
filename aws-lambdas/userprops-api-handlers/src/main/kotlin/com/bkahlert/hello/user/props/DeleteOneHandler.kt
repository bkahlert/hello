package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import aws.sdk.kotlin.services.dynamodb.model.ReturnValue.AllOld
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.jsonResponse
import com.bkahlert.aws.lambda.requiredUserId
import com.bkahlert.aws.lambda.response
import com.bkahlert.aws.lambda.userId
import kotlinx.serialization.json.JsonObject

class DeleteOneHandler(
    val ddbTable: DynamoDbTable = DynamoDbTable(),
) : APIGatewayProxyRequestEventHandler() {

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("User: ${event.userId}")

        val id: String = checkNotNull(event.pathParameters?.get("id")) { "ID missing" }
        logger.info("Deleting one item: $id")

        return when (val output = deleteItem(event.requiredUserId, id)) {
            null -> response(204)
            else -> jsonResponse(output)
        }
    }

    private suspend fun deleteItem(
        userId: String,
        id: String,
    ): JsonObject? = ddbTable.use { ddb ->
        ddb.deleteItem(DeleteItemRequest {
            key = buildMap {
                put(ddbTable.partitionKey, S(userId))
                put(ddbTable.sortKey, requireValidSortKey(id))
            }
            tableName = ddbTable.tableName
            returnValues = AllOld
        })
            .attributes
            ?.filterKeys(ddbTable)
            ?.toJsonObject()
    }
}
