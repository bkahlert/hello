package com.bkahlert.hello.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonObject
import java.util.UUID

class CreateOneHandler : EventHandler() {
    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): GatewayResponse {
        val logger = context.logger
        logger.log("user: ${event.userId}")
        logger.log("Inside software.amazon.awscdk.examples.lambda: getOneItem " + event.javaClass + " data:" + event)

        val body = checkNotNull(event.body) { "body missing" }
        logger.log("Body is:$body")

        val id = createItem(event.requiredUserId, body)
        return GatewayResponse("", 201, "Location" to "${event.requestContext.path}/$id")
    }

    private suspend fun createItem(
        userId: String,
        body: String,
        id: String = UUID.randomUUID().toString(),
    ): String {
        val key = mapOf<String, aws.sdk.kotlin.services.dynamodb.model.AttributeValue>(
            DynamoTable.partitionKey to S(userId),
            DynamoTable.sortKey to requireValidSortKey(id),
        )

        val putItemRequest = PutItemRequest {
            tableName = DynamoTable.tableName
            item = buildMap {
                putAll(key)
                json.parseToJsonElement(body).jsonObject
                    .filterKeys { it != DynamoTable.partitionKey }
                    .forEach {
                        val attributeValue = S(json.encodeToString(it.value))
                        put(it.key, attributeValue)
                    }
            }
        }

        return DynamoTable.usingClient { ddb ->
            ddb.putItem(putItemRequest)
            id
        }
    }
}
