package com.bkahlert.aws.cdk

import com.bkahlert.kommons.logging.SLF4J
import software.amazon.awscdk.Stack
import software.amazon.awscdk.Token
import software.constructs.IConstruct
import software.constructs.Node
import kotlin.reflect.KClass
import kotlin.reflect.cast

private val logger by SLF4J

/**
 * The AWS account into which this stack is deployed, or `null` if it's unresolved.
 */
val IConstruct.resolvedAccount: String?
    get() = Stack.of(this).account.takeUnless { Token.isUnresolved(it) }

/**
 * The AWS region into which this stack is deployed, or `null` if it's unresolved.
 */
val IConstruct.resolvedRegion: String?
    get() = Stack.of(this).region.takeUnless { Token.isUnresolved(it) }

/**
 * Retrieves a value with the specified [key] from tree context.
 *
 * @throws ClassCastException if the value isn't of type [T]
 * @see Node.tryGetContext
 */
fun <T : Any> IConstruct.getContextOrNull(key: String, type: KClass<T>, environmentAgnostic: Boolean = false): T? {
    if (!environmentAgnostic) {
        val account = resolvedAccount
        val region = resolvedRegion
        if (account != null && region != null) {
            val environmentSpecificKey = "$key:account=$account:region=$region"
            val environmentSpecificValue = getContextOrNull(environmentSpecificKey, type, true)
            if (environmentSpecificValue != null) return environmentSpecificValue
        }
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    val value = node.tryGetContext(key)?.let { type.cast(it) }
    logger.debug("Context for $key: {}", value)
    return value
}

/**
 * Retrieves a value with the specified [key] from tree context.
 *
 * @throws NoSuchElementException if no value is found
 * @throws ClassCastException if the value isn't of type [T]
 * @see Node.tryGetContext
 */
fun <T : Any> IConstruct.getContext(key: String, type: KClass<T>, environmentAgnostic: Boolean = false): T =
    getContextOrNull(key, type, environmentAgnostic) ?: throw NoSuchElementException("No context value found for $key")


/**
 * Retrieves a value with the specified [key] from tree context.
 *
 * @throws ClassCastException if the value isn't of type [T]
 * @see Node.tryGetContext
 */
inline fun <reified T : Any> IConstruct.getContextOrNull(key: String, environmentAgnostic: Boolean = false): T? =
    getContextOrNull(key, T::class, environmentAgnostic)

/**
 * Retrieves a value with the specified [key] from tree context.
 *
 * @throws NoSuchElementException if no value is found
 * @throws ClassCastException if the value isn't of type [T]
 * @see Node.tryGetContext
 */
inline fun <reified T : Any> IConstruct.getContext(key: String, environmentAgnostic: Boolean = false): T =
    getContext(key, T::class, environmentAgnostic)
