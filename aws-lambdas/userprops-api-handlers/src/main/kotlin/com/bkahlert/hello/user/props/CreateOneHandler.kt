package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.requiredUserId
import com.bkahlert.aws.lambda.response
import com.bkahlert.aws.lambda.userId
import com.bkahlert.hello.user.props.DynamoTable.filterKeys
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.util.UUID

class CreateOneHandler : APIGatewayProxyRequestEventHandler() {

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("user: ${event.userId}")
        logger.debug("Inside software.amazon.awscdk.examples.lambda: getOneItem " + event.javaClass + " data:" + event)

        val id: String = when (val providedId = event.pathParameters?.get("id")) {
            null -> UUID.randomUUID().toString().also { logger.info("creating data for generated ID: $it") }
            else -> providedId.also { logger.info("creating data for provided ID: $it") }
        }

        val body = checkNotNull(event.body) { "body missing" }
        logger.debug("Body is:$body")

        createItem(event.requiredUserId, id, body)
        return response(201, "Location" to "${event.requestContext.path}/$id")
    }

    private suspend fun createItem(
        userId: String,
        id: String,
        body: String,
    ): JsonObject? {
        val key = mapOf<String, AttributeValue>(
            DynamoTable.partitionKey to S(userId),
            DynamoTable.sortKey to requireValidSortKey(id),
        )

        val putItemRequest = PutItemRequest {
            tableName = DynamoTable.tableName
            item = buildMap {
                putAll(key)
                Json.parseToJsonElement(body).jsonObject
                    .filterKeys()
                    .forEach {
                        val attributeValue = S(Json.encodeToString(it.value))
                        put(it.key, attributeValue)
                    }
            }
        }

        return DynamoTable.usingClient { ddb ->
            ddb.putItem(putItemRequest)
                .attributes
                ?.filterKeys()
                ?.toJsonObject()
        }
    }
}
