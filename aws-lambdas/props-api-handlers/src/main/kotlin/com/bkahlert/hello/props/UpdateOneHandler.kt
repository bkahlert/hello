package com.bkahlert.hello.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeAction
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.AttributeValueUpdate
import aws.sdk.kotlin.services.dynamodb.model.ReturnValue
import aws.sdk.kotlin.services.dynamodb.model.UpdateItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.bkahlert.hello.props.DynamoTable.filterKeys
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonObject

class UpdateOneHandler : EventHandler() {
    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): GatewayResponse {
        val logger = context.logger
        logger.log("user: ${event.userId}")
        logger.log("Inside software.amazon.awscdk.examples.lambda: getOneItem " + event.javaClass + " data:" + event)

        val id: String = checkNotNull(event.pathParameters?.get("id")) { "ID missing" }
        logger.log("updating data for input parameter:$id")

        val body = checkNotNull(event.body) { "body missing" }
        logger.log("Body is:$body")

        val output = updateData(event.requiredUserId, id, body)

        return GatewayResponse(output)
    }

    private suspend fun updateData(userId: String, id: String, body: String): String {

        val updateItemRequest = UpdateItemRequest {
            key = mapOf<String, AttributeValue>(
                DynamoTable.partitionKey to S(userId),
                DynamoTable.sortKey to requireValidSortKey(id),
            )
            tableName = DynamoTable.tableName
            attributeUpdates = buildMap {
                json.parseToJsonElement(body).jsonObject
                    .filterKeys()
                    .forEach {
                        val attributeValue = S(json.encodeToString(it.value))
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
                .encodeToString()
        }
    }
}
