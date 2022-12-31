package com.bkahlert.hello.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.ComparisonOperator
import aws.sdk.kotlin.services.dynamodb.model.Condition
import aws.sdk.kotlin.services.dynamodb.model.QueryRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import kotlinx.serialization.encodeToString

class GetAllHandler : EventHandler() {
    override suspend fun handleEvent(event: APIGatewayProxyRequestEvent, context: Context): GatewayResponse {
        val logger = context.logger
        logger.log("user: ${event.userId}")
        logger.log("Inside software.amazon.awscdk.examples.lambda: getAllItems ")
        val output = getData(event.requiredUserId)
        return GatewayResponse(output)
    }

    private suspend fun getData(userId: String): String {

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
                .associateBy { it[DynamoTable.sortKey]?.asSOrNull() }
                .mapValues {
                    it.value
                        .filterKeys { key ->
                            key != DynamoTable.partitionKey && key != DynamoTable.sortKey
                        }.toJsonObject()
                }.let {
                    json.encodeToString(it)
                }
        }
    }
}
