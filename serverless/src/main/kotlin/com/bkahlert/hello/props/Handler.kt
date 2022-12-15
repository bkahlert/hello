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

    private val props = mutableMapOf<String, String?>()

    override fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse = when (event.routeKey) {
        "GET /props/{id}" -> {
            val id = requireNotNull(event.pathParameters["id"])
            when (val value = props[id]) {
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
            val id = requireNotNull(event.pathParameters["id"])
            val value = event.decodedBody
            props[id] = value

            logger.info("env: ${System.getenv().entries.joinToString() { (k, v) -> "$k=$v" }}")
            val tables = runBlocking {

                val keyToGet = mutableMapOf<String, AttributeValue>()
                keyToGet["id"] = AttributeValue.S(id)

                val table = System.getenv("DYNAMODB_PROPS_TABLE")
                val request = GetItemRequest {
                    key = keyToGet
                    tableName = table
                }

                val request2 = PutItemRequest {
                    tableName = table
                    item = if (value != null)
                        mutableMapOf(
                            "id" to AttributeValue.S(id),
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
