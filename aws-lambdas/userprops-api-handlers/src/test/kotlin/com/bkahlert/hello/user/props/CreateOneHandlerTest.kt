package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue.S
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.tests.annotations.Event
import com.bkahlert.aws.lambda.TestContext
import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeUUID
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.params.ParameterizedTest
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class CreateOneHandlerTest {

    @Container
    private val dynamoContainer: GenericContainer<*> = dynamoDbContainer()

    @ParameterizedTest
    @Event(value = "events/CreateOne/create-primitive.json", type = APIGatewayProxyRequestEvent::class)
    fun `should respond created status and location header`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare()
        val handler = CreateOneHandler(ddbTable)

        val response = handler.handleRequest(event, context)

        response should {
            it.statusCode shouldBe 201
            it.headers shouldNotContainKey "Content-Type"
            it.headers["Location"] should { location ->
                location.shouldNotBeNull()
                location.substringAfterLast('/').shouldBeUUID()
            }
            it.body shouldBe null
        }
    }

    @ParameterizedTest
    @Event(value = "events/CreateOne/create-primitive.json", type = APIGatewayProxyRequestEvent::class)
    fun `should create primitive`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare()
        val handler = CreateOneHandler(ddbTable)

        val response = handler.handleRequest(event, context)

        ddbTable.items should {
            it.shouldContainExactly(
                mapOf(
                    "userId" to S("alice"),
                    "propId" to S(response.headers["Location"].shouldNotBeNull().substringAfterLast('/')),
                    "foo" to JsonPrimitive("bar").toAttribute(),
                    "baz" to JsonPrimitive(null).toAttribute(),
                )
            )
        }
    }

    @ParameterizedTest
    @Event(value = "events/CreateOne/create-object.json", type = APIGatewayProxyRequestEvent::class)
    fun `should create object`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare()
        val handler = CreateOneHandler(ddbTable)

        val response = handler.handleRequest(event, context)

        ddbTable.items should {
            it.shouldContainExactly(
                mapOf(
                    "userId" to S("alice"),
                    "propId" to S(response.headers["Location"].shouldNotBeNull().substringAfterLast('/')),
                    "foo" to buildJsonObject {
                        put("bar", 42)
                        put("baz", null)
                    }.toAttribute(),
                )
            )
        }
    }

    @ParameterizedTest
    @Event(value = "events/CreateOne/create-array.json", type = APIGatewayProxyRequestEvent::class)
    fun `should create array`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare()
        val handler = CreateOneHandler(ddbTable)

        val response = handler.handleRequest(event, context)

        ddbTable.items should {
            it.shouldContainExactly(
                mapOf(
                    "userId" to S("alice"),
                    "propId" to S(response.headers["Location"].shouldNotBeNull().substringAfterLast('/')),
                    "foo" to buildJsonArray {
                        add(true)
                        add(false)
                    }.toAttribute(),
                )
            )
        }
    }

    @ParameterizedTest
    @Event(value = "events/CreateOne/create-primitive.json", type = APIGatewayProxyRequestEvent::class)
    fun `should use specified id`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare()
        val handler = CreateOneHandler(ddbTable)

        val response = handler.handleRequest(event.withPathParameters(mutableMapOf("id" to "my-id")), context)

        response.headers["Location"].shouldNotBeNull().substringAfterLast('/') should {
            it shouldBe "my-id"
            it shouldBe ddbTable.items?.single()?.get("propId")?.asS()
        }
    }

    @ParameterizedTest
    @Event(value = "events/CreateOne/create-primitive.json", type = APIGatewayProxyRequestEvent::class)
    fun `should overwrite existing item`(event: APIGatewayProxyRequestEvent, context: TestContext) = testAll {
        val ddbTable = dynamoContainer.asDynamoDbClientProvider().prepare()
        val handler = CreateOneHandler(ddbTable).apply {
            handleRequest(event.clone().withPathParameters(mutableMapOf("id" to "my-id")).withBody(Json.encodeToString(mapOf("foo" to 42))), context)
        }

        handler.handleRequest(event.withPathParameters(mutableMapOf("id" to "my-id")), context) should {
            it.statusCode shouldBe 201
        }

        ddbTable.items should {
            it.shouldContainExactly(
                mapOf(
                    "userId" to S("alice"),
                    "propId" to S("my-id"),
                    "foo" to JsonPrimitive("bar").toAttribute(),
                    "baz" to JsonPrimitive(null).toAttribute(),
                )
            )
        }
    }
}
