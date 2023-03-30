package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeAction.Put
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.AttributeValueUpdate
import aws.sdk.kotlin.services.dynamodb.model.ReturnValue.AllNew
import aws.sdk.kotlin.services.dynamodb.model.UpdateItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.jsonResponse
import com.bkahlert.aws.lambda.requiredUserId
import com.bkahlert.aws.lambda.response
import com.bkahlert.aws.lambda.userId
import kotlinx.serialization.json.JsonElement

class UpdateOneHandler(
    val ddbTable: DynamoDbTable = DynamoDbTable(),
) : APIGatewayProxyRequestEventHandler() {

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("User: ${event.userId}")

        val id = checkNotNull(event.pathParameters?.get("id")) { "ID missing" }
        logger.info("Updating one item: $id")

        val body = checkNotNull(event.body) { "body missing" }
        logger.debug("Body is: $body")

        return when (val output = updateData(event.requiredUserId, id, body)) {
            null -> response(204)
            else -> jsonResponse(output)
        }
    }

    private suspend fun updateData(userId: String, id: String, body: String): JsonElement? =
        ddbTable.use { ddb ->
            ddb.updateItem(UpdateItemRequest {
                key = buildMap {
                    put(ddbTable.partitionKey, S(userId))
                    put(ddbTable.sortKey, requireValidSortKey(id))
                }
                tableName = ddbTable.tableName
                attributeUpdates = buildMap {
                    put(ddbTable.valueKey, AttributeValueUpdate {
                        value = S(body)
                        action = Put
                    }
                    )
                }
                returnValues = AllNew
            })
                .attributes
                ?.get(ddbTable.valueKey)
                ?.toJsonElement()
        }
}
