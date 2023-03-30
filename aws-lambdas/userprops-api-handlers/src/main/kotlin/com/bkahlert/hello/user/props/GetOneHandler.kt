package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.jsonResponse
import com.bkahlert.aws.lambda.requiredUserId
import com.bkahlert.aws.lambda.response
import com.bkahlert.aws.lambda.userId
import kotlinx.serialization.json.JsonElement

class GetOneHandler(
    val ddbTable: DynamoDbTable = DynamoDbTable(),
) : APIGatewayProxyRequestEventHandler() {

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("User: ${event.userId}")

        val id = checkNotNull(event.pathParameters?.get("id")) { "ID missing" }
        logger.info("Getting one item: $id")

        return when (val output = getData(event.requiredUserId, id)) {
            null -> response(204)
            else -> jsonResponse(output)
        }
    }

    private suspend fun getData(userId: String, id: String): JsonElement? =
        ddbTable.use { ddb ->
            ddb.getItem(GetItemRequest {
                key = mapOf<String, AttributeValue>(
                    ddbTable.partitionKey to S(userId),
                    ddbTable.sortKey to requireValidSortKey(id),
                )
                tableName = ddbTable.tableName
                projectionExpression
            }).item
                ?.get(ddbTable.valueKey)
                ?.toJsonElement()
        }
}
