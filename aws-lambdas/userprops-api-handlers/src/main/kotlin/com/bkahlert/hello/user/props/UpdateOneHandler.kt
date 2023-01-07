package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeAction
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.AttributeValueUpdate
import aws.sdk.kotlin.services.dynamodb.model.ReturnValue
import aws.sdk.kotlin.services.dynamodb.model.UpdateItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.jsonResponse
import com.bkahlert.aws.lambda.requiredUserId
import com.bkahlert.aws.lambda.userId
import com.bkahlert.hello.user.props.DynamoTable.filterKeys
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class UpdateOneHandler : APIGatewayProxyRequestEventHandler() {

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("user: ${event.userId}")
        logger.debug("Inside software.amazon.awscdk.examples.lambda: getOneItem " + event.javaClass + " data:" + event)

        val id: String = checkNotNull(event.pathParameters?.get("id")) { "ID missing" }
        logger.info("updating data for input parameter:$id")

        val body = checkNotNull(event.body) { "body missing" }
        logger.debug("Body is:$body")

        val output = updateData(event.requiredUserId, id, body)

        return jsonResponse(output)
    }

    private suspend fun updateData(userId: String, id: String, body: String): JsonObject {

        val updateItemRequest = UpdateItemRequest {
            key = mapOf<String, AttributeValue>(
                DynamoTable.partitionKey to S(userId),
                DynamoTable.sortKey to requireValidSortKey(id),
            )
            tableName = DynamoTable.tableName
            attributeUpdates = buildMap {
                Json.parseToJsonElement(body).jsonObject
                    .filterKeys()
                    .forEach {
                        val attributeValue = S(Json.encodeToString(it.value))
                        put(
                            it.key, AttributeValueUpdate {
                                value = attributeValue
                                action = AttributeAction.Put
                            }
                        )
                    }
            }
            returnValues = ReturnValue.AllNew
        }

        return DynamoTable.usingClient { ddb ->
            ddb.updateItem(updateItemRequest)
                .attributes!!
                .filterKeys()
                .toJsonObject()
        }
    }
}
