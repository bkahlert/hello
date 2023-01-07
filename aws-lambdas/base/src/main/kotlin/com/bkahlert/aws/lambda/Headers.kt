package com.bkahlert.aws.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty

public class CaseInsensitiveHeaders(
    private val headers: Map<String, String>?,
    private val multiValueHeaders: Map<String, List<String>>?,
) {
    public operator fun get(name: String): Set<String> = buildSet {
        headers?.let { headers ->
            headers.filterKeys { it.equals(name, ignoreCase = true) }.mapTo(this) { it.value }
        }
        multiValueHeaders?.let { multiValueHeaders ->
            multiValueHeaders.filterKeys { it.equals(name, ignoreCase = true) }.flatMapTo(this) { it.value }
        }
    }
}

public operator fun <T : Any> Pair<
    KFunction1<T, Map<String, String>?>,
    KFunction1<T, Map<String, List<String>>?>
    >.getValue(
    thisRef: T,
    property: KProperty<*>,
): CaseInsensitiveHeaders {
    val (getHeaders, getMultiValueHeaders) = this
    return CaseInsensitiveHeaders(getHeaders(thisRef), getMultiValueHeaders(thisRef))
}

public val APIGatewayProxyRequestEvent.caseInsensitiveHeaders: CaseInsensitiveHeaders by APIGatewayProxyRequestEvent::getHeaders to APIGatewayProxyRequestEvent::getMultiValueHeaders
