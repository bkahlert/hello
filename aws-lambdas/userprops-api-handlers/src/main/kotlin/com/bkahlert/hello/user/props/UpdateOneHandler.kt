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
import com.bkahlert.hello.user.props.SimpleSelector.Companion.parseSelector
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

class UpdateOneHandler(
    val ddbTable: DynamoDbTable = DynamoDbTable(),
) : APIGatewayProxyRequestEventHandler() {

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("User: ${event.userId}")

        val selector = checkNotNull(event.pathParameters?.get("id")) { "ID missing" }.parseSelector()
        logger.info("Updating one item: $selector")

        val body = checkNotNull(event.body) { "body missing" }
        logger.debug("Body is: $body")

        return when (val output = updateData(event.requiredUserId, selector, body)) {
            null -> response(204)
            else -> jsonResponse(output)
        }
    }

    private suspend fun updateData(userId: String, selector: SimpleSelector, body: String): JsonObject? = ddbTable.use { ddb ->
        ddb.updateItem(UpdateItemRequest {
            key = buildMap {
                put(ddbTable.partitionKey, S(userId))
                put(ddbTable.sortKey, requireValidSortKey(selector.id))
            }
            tableName = ddbTable.tableName
            attributeUpdates = buildMap {
                when (selector.path.size) {
                    // update all specified attributes
                    0 -> Json.parseToJsonElement(body).jsonObject
                        .filterKeys(ddbTable)
                        .forEach {
                            put(
                                it.key, AttributeValueUpdate {
                                    value = it.value.toAttribute()
                                    action = Put
                                }
                            )
                        }

                    // update one specified attribute
                    1 -> buildJsonObject { put(selector.path.first(), Json.parseToJsonElement(body)) }
                        .filterKeys(ddbTable)
                        .forEach {
                            val attributeValue = S(Json.encodeToString(it.value))
                            put(
                                it.key, AttributeValueUpdate {
                                    value = attributeValue
                                    action = Put
                                }
                            )
                        }

                    else -> throw UnsupportedOperationException("Only immediate children can be updated.")
                }
            }
            returnValues = AllNew
        })
            .attributes
            ?.filterKeys(ddbTable)
            ?.toJsonObject()
    }
}
