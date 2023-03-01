package com.bkahlert.hello.environment.data

import com.bkahlert.hello.data.Resource
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
    private val environmentFlow: SharedFlow<Resource<Environment>> = flow<Resource<Environment>> {
        logger.grouping(EnvironmentDataSource::load) {
            environmentDataSource.load()
        }.also {
            emit(Resource.Success(it))
        }
    }.retry { ex ->
        if (ex is ServerResponseException && ex.response.status == HttpStatusCode.ServiceUnavailable) {
            val retryIn = 5.seconds
            logger.warn("Service is unavailable, retrying in $retryIn")
            delay(retryIn)
            true
        } else {
            false
        }
    }.catch { ex ->
        when (ex) {
            is ResponseException -> {
                when (ex.response.status) {
                    HttpStatusCode.NotFound -> {
                        emit(Resource.Failure("Environment could not be found", ex))
                    }

                    else -> emit(Resource.Failure("Environment could not be loaded", ex))
                }
            }

            else -> emit(Resource.Failure("An unexpected problem occurred while loading the environment", ex))
        }
    }.shareIn(externalScope, SharingStarted.Eagerly, replay = 1)

    public fun environmentFlow(): Flow<Resource<Environment>> = environmentFlow
}
