package com.bkahlert.kommons.coroutines.flow

import com.bkahlert.hello.SimpleLogger
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.applyUpdates
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.extend
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.reset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/**
 * This helper allows conditionally mapping a [Flow].
 *
 * It's intended to be used together with [applyUpdates], [extend] and [reset].
 */
sealed interface FlowUpdate<T> {

    /**
     * Returns this flow update applied to the specified [value].
     */
    suspend fun apply(value: T): T

    /**
     * Returns a flow update that applies the given [transform] to the
     * transformation of the original flow update.
     */
    fun <R : T> map(transform: suspend (T) -> R): FlowUpdate<R>

    /**
     * A flow update that applies the specified [transform] to its input.
     */
    data class Update<T>(private val transform: suspend (T) -> T) : FlowUpdate<T> {
        override suspend fun apply(value: T): T {
            logger { trace("APPLY TRANSFORM\n    BY: $it\n    VALUE: $value") }
            return transform(value)
                .logger { trace("APPLY TRANSFORM COMPLETED\n    BY: ${this@Update}\n    RESULT: $it") }
        }

        override fun <R : T> map(transform: suspend (T) -> R): FlowUpdate<R> {
            return Update { state -> transform(this.transform(state)) }
        }
    }

    /**
     * A flow update that return the specified [value] to [apply] invocations.
     */
    data class Updated<T>(private val value: T) : FlowUpdate<T> {
        override suspend fun apply(value: T): T {
            logger { trace("APPLY TRANSFORM\n    BY: $it\n    VALUE: $value\n    STORED VALUE: ${it.value}") }
            return this.value
                .logger { trace("APPLY TRANSFORM COMPLETED\n    BY: ${this@Updated}\n    RESULT: $it") }
        }

        override fun <R : T> map(transform: suspend (T) -> R): FlowUpdate<R> =
            Update { transform(value) }
    }

    companion object {
        private val logger: SimpleLogger = simpleLogger()
        private val LoggingEnabled: Boolean = false
        private fun <T> T.logger(block: SimpleLogger.(T) -> Unit): T = also { if (LoggingEnabled) logger.block(it) }

        private val INIT = Update<Any?> { it }

        /**
         * Instantiates a new [MutableStateFlow] that initially applies to transformation
         * when combines with another [Flow].
         */
        operator fun <T> invoke(): MutableStateFlow<FlowUpdate<T>> =
            MutableStateFlow(init())

        /**
         * Returns a flow update that applies no transformation.
         */
        fun <T> init(): Update<T> = INIT.unsafeCast<Update<T>>()
            .logger { trace("INIT UPDATE\n    INSTANCE: $it") }

        /**
         * Applies the (accumulated) transformation (see [extend] and [reset]) to the elements
         * of the given [flow]. The result itself is stored in `this` [MutableStateFlow]
         * so that:
         * - consecutive invocations are applied to the result instead of the elements of
         *   the specified [flow], and
         * - each transformation is only applied once.
         */
        fun <T, R : T> MutableStateFlow<FlowUpdate<T>>.applyUpdates(flow: Flow<R>): Flow<T> {
            logger { trace("APPLY UPDATES\n    BY: $it\n    FLOW UPDATE: $value\n    FLOW: $flow") }
            return combine(flow, FlowUpdate<T>::apply)
                .onEach { updated ->
                    logger { trace("APPLY UPDATES\n    BY: $it\n    RESULT: $updated") }
                    update {
                        Updated(updated)
                            .logger { trace("APPLY UPDATES COMPLETED\n    BY: ${this@applyUpdates}\n    STORED: $it") }
                    }
                }
        }

        /**
         * Updates the stored [FlowUpdate] with a flow update that applies the specified [transform]
         * to the result of the original flow update.
         */
        fun <T, R : T> MutableStateFlow<FlowUpdate<T>>.extend(transform: suspend (T) -> R) {
            logger { trace("EXTENDING\n    BY: $it\n    FLOW UPDATE: $value\n    TRANSFORM: $transform") }
            update {
                logger { trace("EXTENDING\n    BY: ${this@extend}\n    OLD: $it") }
                val mapped: FlowUpdate<T> = it.map(transform)
                logger { trace("EXTENDING COMPLETED\n    BY: ${this@extend}\n    NEW: $mapped") }
                mapped
            }
        }

        /**
         * Updates the stored [FlowUpdate] with a flow update that does not apply
         * any transformation.
         */
        fun <T> MutableStateFlow<FlowUpdate<T>>.reset() {
            logger { trace("RESETTING\n    BY: ${this@reset}\n    FLOW UPDATE: $value") }
            update { init() }
        }
    }
}
