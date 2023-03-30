package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.tests.annotations.Event
import com.bkahlert.aws.lambda.TestContext
import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.params.ParameterizedTest
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class UpdateOneHandlerTest {

    @Container
    private val dynamoContainer: GenericContainer<*> = dynamoDbContainer()

    @ParameterizedTest
    @Event(value = "events/UpdateOne/update-item.json", type = APIGatewayProxyRequestEvent::class)
    fun `should respond updated item`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare {
            putItem(PutItemRequest {
                tableName = it.tableName
                item = mapOf<String, AttributeValue>(
                    it.partitionKey to S("alice"),
                    it.sortKey to S("foo"),
                    "value" to S("""{ "other": 42 }"""),
                )
            })
        }
        val handler = UpdateOneHandler(ddbTable)

        handler.handleRequest(event, context) should {
            it.statusCode shouldBe 200
            it.headers shouldContain ("Content-Type" to "application/json")
            Json.parseToJsonElement(it.body) shouldBe buildJsonObject {
                put("bar", "baz")
            }
        }

        ddbTable.items should {
            it.shouldContainExactly(
                mapOf(
                    "userId" to S("alice"),
                    "propId" to S("foo"),
                    "value" to S("""{ "bar": "baz" }"""),
                )
            )
        }
    }

    @ParameterizedTest
    @Event(value = "events/UpdateOne/update-item.json", type = APIGatewayProxyRequestEvent::class)
    fun `should replace existing attribute`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare {
            putItem(PutItemRequest {
                tableName = it.tableName
                item = mapOf<String, AttributeValue>(
                    it.partitionKey to S("alice"),
                    it.sortKey to S("foo"),
                    "value" to S("""{ "bar": 42 }"""),
                )
            })
        }
        val handler = UpdateOneHandler(ddbTable)

        handler.handleRequest(event, context) should {
            it.statusCode shouldBe 200
            it.headers shouldContain ("Content-Type" to "application/json")
            Json.parseToJsonElement(it.body) shouldBe buildJsonObject {
                put("bar", "baz")
            }
        }

        ddbTable.items should {
            it.shouldContainExactly(
                mapOf(
                    "userId" to S("alice"),
                    "propId" to S("foo"),
                    "value" to S("""{ "bar": "baz" }"""),
                )
            )
        }
    }

    @ParameterizedTest
    @Event(value = "events/UpdateOne/update-item.json", type = APIGatewayProxyRequestEvent::class)
    fun `should create missing item`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare()
        val handler = UpdateOneHandler(ddbTable)

        handler.handleRequest(event, context) should {
            it.statusCode shouldBe 200
            it.headers shouldContain ("Content-Type" to "application/json")
            Json.parseToJsonElement(it.body) shouldBe buildJsonObject {
                put("bar", "baz")
            }
        }

        ddbTable.items should {
            it.shouldContainExactly(
                mapOf(
                    "userId" to S("alice"),
                    "propId" to S("foo"),
                    "value" to S("""{ "bar": "baz" }"""),
                )
            )
        }
    }
}
