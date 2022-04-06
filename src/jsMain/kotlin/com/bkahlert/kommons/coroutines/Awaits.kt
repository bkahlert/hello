package com.bkahlert.kommons.coroutines

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll

/**
 * Awaits for completion of the given deferred value while catching an eventually thrown exception.
 * @see [Deferred.await]
 * @see [runCatching]
 */
suspend inline fun <T> Deferred<T>.awaitCatching(): Result<T> =
    runCatching { Result.success(await()) }.getOrElse { Result.failure(it) }

/**
 * Awaits for completion of the given deferred values while catching an eventually thrown exception.
 * @see [Deferred.await]
 * @see [runCatching]
 */
suspend inline fun <T> Collection<Deferred<T>>.awaitAllCatching(): Result<List<T>> =
    runCatching { Result.success(awaitAll()) }.getOrElse { Result.failure(it) }
