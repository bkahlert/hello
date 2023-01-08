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
import com.bkahlert.hello.user.props.SimpleSelector.Companion.parseSelector
import kotlinx.serialization.json.JsonElement

class GetOneHandler(
    val ddbTable: DynamoDbTable = DynamoDbTable(),
) : APIGatewayProxyRequestEventHandler() {

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("User: ${event.userId}")

        val selector = checkNotNull(event.pathParameters?.get("id")) { "ID missing" }.parseSelector()
        logger.info("Getting one item: $selector")

        return when (val output = getData(event.requiredUserId, selector)) {
            null -> response(204)
            else -> jsonResponse(output)
        }
    }

    private suspend fun getData(userId: String, selector: SimpleSelector): JsonElement? = ddbTable.use { ddb ->
        ddb.getItem(GetItemRequest {
            key = mapOf<String, AttributeValue>(
                ddbTable.partitionKey to S(userId),
                ddbTable.sortKey to requireValidSortKey(selector.id),
            )
            tableName = ddbTable.tableName
            projectionExpression
        }).item
            ?.filterKeys(ddbTable)
            ?.toJsonObject()
            ?.let { selector.resolve(it) }
    }
}
