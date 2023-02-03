package com.bkahlert.hello.environment.data

import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.kommons.js.debug
import com.bkahlert.kommons.js.table
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.withContext

/**
 * Repository that loads the environment only once.
 */
public data class EnvironmentRepository(
    private val environmentDataSource: EnvironmentDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    public constructor(environment: Environment) : this(EnvironmentDataSource { error("DataSource.load() invoked") }) {
        environmentFlow.value = environment
    }

    private val environmentFlow: MutableStateFlow<Environment?> = MutableStateFlow(null)

    public suspend fun refreshEnvironment() {
        if (environmentFlow.value == null) {
            console.debug("EnvironmentRepository: Refresh")
            withContext(ioDispatcher) {
                console.debug("EnvironmentRepository: Load")
                val environment = environmentDataSource.load()
                environmentFlow.updateAndGet { environment }?.also {
                    console.table(it, name = "EnvironmentRepository: Update")
                }
            }
        } else {
            console.debug("EnvironmentRepository: Refresh skipped because environment was already loaded")
        }
    }

    public fun getEnvironmentFlow(): Flow<Environment> = environmentFlow.asSharedFlow().filterIsInstance()
}
