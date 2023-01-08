package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.ComparisonOperator.Eq
import aws.sdk.kotlin.services.dynamodb.model.Condition
import aws.sdk.kotlin.services.dynamodb.model.QueryRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.jsonResponse
import com.bkahlert.aws.lambda.requiredUserId
import com.bkahlert.aws.lambda.userId
import kotlinx.serialization.json.JsonObject

class GetAllHandler(
    val ddbTable: DynamoDbTable = DynamoDbTable(),
) : APIGatewayProxyRequestEventHandler() {

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("User: ${event.userId}")
        logger.info("Getting all items")
        val output = getData(event.requiredUserId)
        return jsonResponse(output)
    }

    private suspend fun getData(userId: String): JsonObject = ddbTable.use { ddb ->
        ddb.query(QueryRequest {
            keyConditions = mapOf(
                ddbTable.partitionKey to Condition {
                    comparisonOperator = Eq
                    attributeValueList = listOf(S(userId))
                },
            )
            tableName = ddbTable.tableName
        })
            .items.orEmpty()
            .associateBy { it[ddbTable.sortKey]!!.asS() }
            .mapValues { it.value.filterKeys(ddbTable).toJsonObject() }
            .let(::JsonObject)
    }
}
