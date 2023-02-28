package com.bkahlert.hello.environment.data

import com.bkahlert.hello.data.DataRetrieval
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.shareIn
import kotlin.time.Duration.Companion.seconds

/**
 * Repository that loads the environment only once.
 */
public data class EnvironmentRepository(
    private val environmentDataSource: EnvironmentDataSource,
    private val externalScope: CoroutineScope,
) {
    private val logger by ConsoleLogging

    private val environmentFlow: SharedFlow<DataRetrieval<Environment>> = flow<DataRetrieval<Environment>> {
        logger.grouping(EnvironmentDataSource::load) {
            environmentDataSource.load()
        }.also {
            emit(DataRetrieval.Succeeded(it))
        }
    }.retry { e ->
        if (e is ServerResponseException && e.response.status == HttpStatusCode.ServiceUnavailable) {
            val retryIn = 5.seconds
            logger.warn("Service is unavailable, retrying in $retryIn")
            delay(retryIn)
            true
        } else {
            false
        }
    }.catch { e ->
        when (e) {
            is ResponseException -> {
                when (e.response.status) {
                    HttpStatusCode.NotFound -> {
                        emit(DataRetrieval.Failed("Environment could not be found", e))
                    }

                    else -> emit(DataRetrieval.Failed("Environment could not be loaded", e))
                }
            }

            else -> emit(DataRetrieval.Failed("An unexpected problem occurred while loading environment", e))
        }
    }.shareIn(externalScope, SharingStarted.Lazily, 1)

    public fun environmentFlow(): Flow<DataRetrieval<Environment>> = environmentFlow
}
