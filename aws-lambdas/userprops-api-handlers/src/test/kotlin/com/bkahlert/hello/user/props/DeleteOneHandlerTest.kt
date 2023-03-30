package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.tests.annotations.Event
import com.bkahlert.aws.lambda.TestContext
import com.bkahlert.kommons.test.testAll
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.params.ParameterizedTest
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class DeleteOneHandlerTest {

    @Container
    private val dynamoContainer: GenericContainer<*> = dynamoDbContainer()

    @ParameterizedTest
    @Event(value = "events/DeleteOne/delete-item.json", type = APIGatewayProxyRequestEvent::class)
    fun `should respond no content on missing item`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare()
        val handler = DeleteOneHandler(ddbTable)

        handler.handleRequest(event, context) should {
            it.statusCode shouldBe 204
            it.headers shouldNotContainKey "Content-Type"
            it.body shouldBe null
        }

        ddbTable.items should {
            it.shouldBeEmpty()
        }
    }

    @ParameterizedTest
    @Event(value = "events/DeleteOne/delete-item.json", type = APIGatewayProxyRequestEvent::class)
    fun `should respond deleted item`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare {
            putItem(PutItemRequest {
                tableName = it.tableName
                item = mapOf<String, AttributeValue>(
                    it.partitionKey to S("alice"),
                    it.sortKey to S("foo"),
                    "value" to S("""{"bar":42}"""),
                )
            })
        }
        val handler = DeleteOneHandler(ddbTable)

        handler.handleRequest(event, context) should {
            it.statusCode shouldBe 200
            it.headers shouldContain ("Content-Type" to "application/json")
            it.body shouldBe Json.encodeToString(mapOf("bar" to 42))
        }

        ddbTable.items should {
            it.shouldBeEmpty()
        }
    }

    @ParameterizedTest
    @Event(value = "events/DeleteOne/delete-item.json", type = APIGatewayProxyRequestEvent::class)
    fun `should ignore foreign item`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
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
        val handler = DeleteOneHandler(ddbTable)

        handler.handleRequest(event, context) should {
            it.statusCode shouldBe 204
            it.headers shouldNotContainKey "Content-Type"
            it.body shouldBe null
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

    @ParameterizedTest
    @Event(value = "events/DeleteOne/delete-items.json", type = APIGatewayProxyRequestEvent::class)
    fun `should respond bad request on deleting all`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare {
            putItem(PutItemRequest {
                tableName = it.tableName
                item = mapOf<String, AttributeValue>(
                    it.partitionKey to S("alice"),
                    it.sortKey to S("foo"),
                    "value" to S("""{"bar":42}"""),
                )
            })
        }
        val handler = DeleteOneHandler(ddbTable)

        shouldThrow<IllegalStateException> { handler.handleRequest(event, context) }
            .message shouldBe "ID missing"

        ddbTable.items should {
            it.shouldContainExactly(
                mapOf(
                    "userId" to S("alice"),
                    "propId" to S("foo"),
                    "value" to S("""{"bar":42}"""),
                )
            )
        }
    }
}
