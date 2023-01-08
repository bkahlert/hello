package com.bkahlert.hello.user.props

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeDefinition
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.CreateTableRequest
import aws.sdk.kotlin.services.dynamodb.model.KeySchemaElement
import aws.sdk.kotlin.services.dynamodb.model.KeyType.Hash
import aws.sdk.kotlin.services.dynamodb.model.KeyType.Range
import aws.sdk.kotlin.services.dynamodb.model.ProvisionedThroughput
import aws.sdk.kotlin.services.dynamodb.model.ScalarAttributeType.S
import aws.sdk.kotlin.services.dynamodb.model.ScanRequest
import aws.smithy.kotlin.runtime.http.Protocol
import aws.smithy.kotlin.runtime.http.Url
import aws.smithy.kotlin.runtime.util.net.Host
import kotlinx.coroutines.runBlocking
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import kotlin.random.Random

fun dynamoDbContainer(port: Int = 8000): GenericContainer<Nothing> =
    GenericContainer<Nothing>(DockerImageName.parse("amazon/dynamodb-local:1.20.0"))
        .withExposedPorts(port)

/**
 * Returns a [DynamoDbClientProvider] that connects to this [GenericContainer].
 */
fun GenericContainer<*>.asDynamoDbClientProvider(): DynamoDbClientProvider {
    val url = Url(Protocol.HTTP, Host.parse(host), firstMappedPort)
    return { DynamoDbClient.fromEnvironment { endpointUrl = url } }
}

/**
 * Returns a [DynamoDbTable] with the
 * specified [tableName], [partitionKey], and [sortKey].
 */
fun DynamoDbClientProvider.prepare(
    tableName: String = "TestTable-${Random(System.currentTimeMillis()).nextInt(0, 2.shl(17)).toString(16).padStart(6, '0')}",
    partitionKey: String = "userId",
    sortKey: String = "propId",
    block: (suspend DynamoDbClient.(DynamoDbTable) -> Unit)? = null,
): DynamoDbTable {
    val response = runBlocking {
        use { ddb ->
            ddb.createTable(CreateTableRequest {
                this.tableName = tableName
                keySchema = listOf(
                    KeySchemaElement {
                        attributeName = partitionKey
                        keyType = Hash
                    },
                    KeySchemaElement {
                        attributeName = sortKey
                        keyType = Range
                    },
                )
                attributeDefinitions = listOf(
                    AttributeDefinition {
                        attributeName = partitionKey
                        attributeType = S
                    },
                    AttributeDefinition {
                        attributeName = sortKey
                        attributeType = S
                    },
                )
                provisionedThroughput = ProvisionedThroughput {
                    readCapacityUnits = 5
                    writeCapacityUnits = 6
                }
            })
        }
    }

    val tableDescription = checkNotNull(response.tableDescription) { "table description missing" }

    val ddbTable = DynamoDbTable(
        ddbProvider = this@prepare,
        tableName = checkNotNull(tableDescription.tableName) { "table name missing" },
        partitionKey = checkNotNull(tableDescription.keySchema?.get(0)?.attributeName) { "partition key missing" },
        sortKey = checkNotNull(tableDescription.keySchema?.get(1)?.attributeName) { "sort key missing" },
    )

    if (block != null) {
        runBlocking { ddbTable.use { it.block(ddbTable) } }
    }

    return ddbTable
}

/**
 * Returns all items stored in [DynamoDbTable.tableName].
 */
val DynamoDbTable.items: List<Map<String, AttributeValue>>?
    get() {
        val scanRequest = ScanRequest {
            this@ScanRequest.tableName = this@items.tableName
        }
        return runBlocking {
            use { it.scan(scanRequest).items }
        }
    }
