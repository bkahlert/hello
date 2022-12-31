package com.bkahlert.aws.cdk

import software.amazon.jsii.Builder

/**
 * Builds an instance [T] by applying [init] on this [Builder].
 */
fun <B : Builder<T>, T> B.build(init: B.() -> Unit): T = apply(init).build()
