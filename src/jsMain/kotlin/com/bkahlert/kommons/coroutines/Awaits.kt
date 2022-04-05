package com.bkahlert.kommons.coroutines

import kotlinx.coroutines.Deferred

/**
 * Awaits for completion of this value while catching an eventually thrown exception.
 * @see [Deferred.await]
 * @see [runCatching]
 */
suspend inline fun <T> Deferred<T>.awaitCatching(): Result<T> =
    runCatching { Result.success(await()) }.getOrElse { Result.failure(it) }
