package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.requiredUserId
import com.bkahlert.aws.lambda.response
import com.bkahlert.aws.lambda.userId
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.util.UUID

class CreateOneHandler(
    val ddbTable: DynamoDbTable = DynamoDbTable(),
) : APIGatewayProxyRequestEventHandler() {

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("User: ${event.userId}")

        val id: String = when (val providedId = event.pathParameters?.get("id")) {
            null -> UUID.randomUUID().toString().also { logger.info("Creating one item with generated ID: $it") }
            else -> providedId.also { logger.info("Creating one item with provided ID: $it") }
        }

        val body = checkNotNull(event.body) { "body missing" }
        logger.debug("Body is: $body")

        createItem(event.requiredUserId, id, body)
        return response(201, "Location" to "${event.requestContext.path}/$id")
    }

    private suspend fun createItem(
        userId: String,
        id: String,
        body: String,
    ): JsonObject? = ddbTable.use { ddb ->
        ddb.putItem(PutItemRequest {
            tableName = ddbTable.tableName
            item = buildMap {
                put(ddbTable.partitionKey, S(userId))
                put(ddbTable.sortKey, requireValidSortKey(id))
                Json.parseToJsonElement(body).jsonObject
                    .filterKeys(ddbTable)
                    .forEach {
                        put(it.key, it.value.toAttribute())
                    }
            }
        })
            .attributes
            ?.filterKeys(ddbTable)
            ?.toJsonObject()
    }
}
