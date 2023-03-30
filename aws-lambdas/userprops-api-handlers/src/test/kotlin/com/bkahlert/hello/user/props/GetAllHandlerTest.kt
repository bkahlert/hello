package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.tests.annotations.Event
import com.bkahlert.aws.lambda.TestContext
import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.params.ParameterizedTest
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class GetAllHandlerTest {

    @Container
    private val dynamoContainer: GenericContainer<*> = dynamoDbContainer()

    @ParameterizedTest
    @Event(value = "events/GetAll/get-items.json", type = APIGatewayProxyRequestEvent::class)
    fun `should respond empty array on missing items`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare()
        val handler = GetAllHandler(ddbTable)

        handler.handleRequest(event, context) should {
            it.statusCode shouldBe 200
            it.headers shouldContain ("Content-Type" to "application/json")
            it.body shouldBe "{}"
        }

        ddbTable.items should {
            it.shouldBeEmpty()
        }
    }

    @ParameterizedTest
    @Event(value = "events/GetAll/get-items.json", type = APIGatewayProxyRequestEvent::class)
    fun `should respond found items`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare {
            putItem(PutItemRequest {
                tableName = it.tableName
                item = mapOf<String, AttributeValue>(
                    it.partitionKey to S("alice"),
                    it.sortKey to S("foo"),
                    "value" to S("""{"bar":42}"""),
                )
            })
            putItem(PutItemRequest {
                tableName = it.tableName
                item = mapOf<String, AttributeValue>(
                    it.partitionKey to S("alice"),
                    it.sortKey to S("bar"),
                    it.valueKey to S("""{"baz":[true,false]}"""),
                )
            })
        }
        val handler = GetAllHandler(ddbTable)

        handler.handleRequest(event, context) should {
            it.statusCode shouldBe 200
            it.headers shouldContain ("Content-Type" to "application/json")
            Json.parseToJsonElement(it.body) shouldBe buildJsonObject {
                put("foo", buildJsonObject { put("bar", 42) })
                put("bar", buildJsonObject { put("baz", buildJsonArray { add(true);add(false) }) })
            }
        }

        ddbTable.items should {
            it.shouldContainExactlyInAnyOrder(
                mapOf(
                    "userId" to S("alice"),
                    "propId" to S("foo"),
                    "value" to S("""{"bar":42}"""),
                ),
                mapOf(
                    "userId" to S("alice"),
                    "propId" to S("bar"),
                    "value" to S("""{"baz":[true,false]}"""),
                ),
            )
        }
    }

    @ParameterizedTest
    @Event(value = "events/GetAll/get-items.json", type = APIGatewayProxyRequestEvent::class)
    fun `should ignore foreign items`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare {
            putItem(PutItemRequest {
                tableName = it.tableName
                item = mapOf<String, AttributeValue>(
                    it.partitionKey to S("bob"),
                    it.sortKey to S("foo"),
                    "value" to S("""{"bar":42}"""),
                )
            })
        }
        val handler = GetAllHandler(ddbTable)

        handler.handleRequest(event, context) should {
            it.statusCode shouldBe 200
            it.headers shouldContain ("Content-Type" to "application/json")
            it.body shouldBe "{}"
        }

        ddbTable.items should {
            it.shouldContainExactly(
                mapOf(
                    "userId" to S("bob"),
                    "propId" to S("foo"),
                    "value" to S("""{"bar":42}"""),
                )
            )
        }
    }
}
