package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.ComparisonOperator
import aws.sdk.kotlin.services.dynamodb.model.Condition
import aws.sdk.kotlin.services.dynamodb.model.QueryRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.bkahlert.aws.lambda.APIGatewayProxyRequestEventHandler
import com.bkahlert.aws.lambda.SLF4J
import com.bkahlert.aws.lambda.jsonResponse
import com.bkahlert.aws.lambda.requiredUserId
import com.bkahlert.aws.lambda.userId
import com.bkahlert.hello.user.props.DynamoTable.filterKeys
import kotlinx.serialization.json.JsonObject

class GetAllHandler : APIGatewayProxyRequestEventHandler {

    private val logger by SLF4J

    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        logger.debug("user: ${event.userId}")
        logger.info("Inside software.amazon.awscdk.examples.lambda: getAllItems ")
        val output = getData(event.requiredUserId)
        return jsonResponse(output)
    }

    private suspend fun getData(userId: String): JsonObject {

        val queryRequest = QueryRequest {
            keyConditions = mapOf(
                DynamoTable.partitionKey to Condition {
                    comparisonOperator = ComparisonOperator.Eq
                    attributeValueList = listOf(S(userId))
                },
            )
            tableName = DynamoTable.tableName
        }

        return DynamoTable.usingClient { ddb ->
            ddb.query(queryRequest)
                .items.orEmpty()
                .associateBy { it[DynamoTable.sortKey]!!.asS() }
                .mapValues { it.value.filterKeys().toJsonObject() }
                .let(::JsonObject)
        }
    }
}
