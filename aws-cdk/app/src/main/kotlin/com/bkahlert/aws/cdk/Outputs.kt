package com.bkahlert.aws.cdk

import software.amazon.awscdk.CfnOutput
import software.amazon.awscdk.Stack

/**
 * Exports this object using the specified [stack], [name], optional [description],
 * and its value derived using the specified [transform].
 */
fun <T> T.export(
    stack: Stack,
    name: String,
    description: String? = null,
    transform: (T) -> String = { it.toString() },
): T = also {
    CfnOutput.Builder.create(stack, name).run {
        if (description != null) description(description)
        value(transform(it))
        build()
    }
}

/**
 * Exports this object using the current [Stack], the specified [name], optional [description],
 * and its value derived using the specified [transform].
 */
context(Stack)
fun <T> T.export(
    name: String,
    description: String? = null,
    transform: (T) -> String = { it.toString() },
): T = export(this@Stack, name, description, transform)
