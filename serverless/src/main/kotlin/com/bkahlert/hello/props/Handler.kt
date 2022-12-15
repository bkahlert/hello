package com.bkahlert.hello.props

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.bkahlert.aws.lambda.EventHandler
import com.bkahlert.aws.lambda.decodedBody
import com.bkahlert.kommons.logging.SLF4J
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class Handler : EventHandler() {

    private val logger by SLF4J
    private val random = Random(20)

    override suspend fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse = when (event.routeKey) {
        "GET /props/{id}" -> {
            val propId = requireNotNull(event.pathParameters["id"])
            logger.info("Getting $propId")
            when (val value = PropsTable.getProp(propId)) {
                null -> APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(404)
                    .build()

                else -> APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(value)
                    .build()
            }
        }

        "POST /props/{id}" -> {
            val propId = requireNotNull(event.pathParameters["id"])
            logger.info("Setting $propId")

            val value = event.decodedBody

            val tables = runBlocking {

                val keyToGet = mutableMapOf<String, AttributeValue>()
                keyToGet["id"] = AttributeValue.S(propId)

                val request = GetItemRequest {
                    key = keyToGet
                    tableName = PropsTable.tableName
                }

                val request2 = PutItemRequest {
                    tableName = PropsTable.tableName
                    item = if (value != null)
                        mutableMapOf(
                            "id" to AttributeValue.S(propId),
                            "value" to AttributeValue.S(value),
                        )
                    else emptyMap()
                }

                DynamoDbClient { region = "eu-central-1" }.use { ddb ->
                    ddb.putItem(request2)
                    val returnedItem = ddb.getItem(request)
                    val numbersMap = returnedItem.item
                    numbersMap?.entries?.joinToString() { key1 ->
                        "${key1.key} -> ${key1.value}"
                    }

//                    val response = ddb.listTables(ListTablesRequest {})
//                    response.tableNames?.joinToString {
//                        "Table $it"
//                    }
                }
            }

            APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withBody("Tables: $tables")
                .build()
        }

        else -> throw IllegalStateException("route ${event.routeKey} unspecified")
    }
}
