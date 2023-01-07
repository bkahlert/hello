package com.bkahlert.aws.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger

/**
 * Base implementation of a [proxy integrated](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-output-format)
 * AWS Lambda function with support for coroutines and null-safety.
 */
public abstract class APIGatewayProxyRequestEventHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    protected val logger: Logger by SLF4J

    private val accessControl = AccessControl.of(
        allowCredentials = System.getenv("ACCESS_CONTROL_ALLOW_CREDENTIALS"),
        allowedHeaders = System.getenv("ACCESS_CONTROL_ALLOW_HEADERS"),
        allowedMethods = System.getenv("ACCESS_CONTROL_ALLOW_METHODS"),
        allowedOrigins = System.getenv("ACCESS_CONTROL_ALLOW_ORIGIN"),
    )

    override fun handleRequest(
        input: APIGatewayProxyRequestEvent?,
        context: Context?,
    ): APIGatewayProxyResponseEvent = runBlocking {
        logger.debug("Input: $input")
        logger.debug("context: $context")

        val event = checkNotNull(input) { "input must not be null" }
        val response = handleEvent(event = event, context = checkNotNull(context) { "context must not be null" })
        response.withHeaders(
            buildMap {
                accessControl.applyTo(this, event.caseInsensitiveHeaders["Origin"].firstOrNull())
                putAll(response.headers)
            }
        )
    }

    public abstract suspend fun handleEvent(
        event: APIGatewayProxyRequestEvent,
        context: Context,
    ): APIGatewayProxyResponseEvent
}

/**
 * Utility for handling access control headers.
 */
public class AccessControl(
    private val allowCredentials: Boolean,
    private val allowedHeaders: List<String>,
    private val allowedMethods: List<String>,
    private val allowedOrigins: List<String>,
) {

    /**
     * Adds a `Access-Control-Allow-Origin` header to the specified [destination]
     * for each [allowedOrigins] matching the specified [origin].
     */
    public fun applyTo(destination: MutableMap<String, String>, origin: String?) {
        destination["Access-Control-Allow-Credentials"] = allowCredentials.toString()

        destination["Access-Control-Allow-Headers"] = allowedHeaders.joinToString(",")

        destination["Access-Control-Allow-Methods"] = allowedMethods.joinToString(",")

        if (allowedOrigins.contains("*")) {
            destination["Access-Control-Allow-Origin"] = origin ?: "*"
        } else {
            allowedOrigins.filter { it == origin }.forEach {
                destination["Access-Control-Allow-Origin"] = it
            }
        }
    }

    public companion object {

        /** Creates an [AccessControl] instance from the specified [allowedOrigins]. */
        public fun of(
            allowCredentials: String?,
            allowedHeaders: String?,
            allowedMethods: String?,
            allowedOrigins: String?,
        ): AccessControl = AccessControl(
            allowCredentials = allowCredentials.toBoolean(),
            allowedHeaders = allowedHeaders?.split(',')?.map { it.trim() } ?: listOf("*"),
            allowedMethods = allowedMethods?.split(',')?.map { it.trim() } ?: listOf("OPTIONS", "GET", "PUT", "POST", "PATCH", "DELETE"),
            allowedOrigins = allowedOrigins?.split(',')?.map { it.trim() } ?: listOf("*"),
        )
    }
}
