package com.bkahlert.kommons.coroutines.flow

import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.applyUpdates
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.extend
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.reset
import com.bkahlert.kommons.text.truncateEnd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

fun Any?.toStringAndHash() = "${this?.let { it::class.simpleName }}@${this.hashCode()}: ${toString().truncateEnd(150)}"

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
            logger.warn("APPLY TRANSFORM\n    BY: ${this.toStringAndHash()}\n    VALUE: ${value.toStringAndHash()}")
            return transform(value).also {
                logger.warn("APPLY TRANSFORM COMPLETED\n    BY: ${this.toStringAndHash()}\n    RESULT: ${it.toStringAndHash()}")
            }
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
            logger.warn("APPLY TRANSFORM\n    BY: ${this.toStringAndHash()}\n    VALUE: ${value.toStringAndHash()}\n    STORED VALUE: ${this.value.toStringAndHash()}")
            return this.value.also {
                logger.warn("APPLY TRANSFORM COMPLETED\n    BY: ${this.toStringAndHash()}\n    RESULT: ${it.toStringAndHash()}")
            }
        }

        override fun <R : T> map(transform: suspend (T) -> R): FlowUpdate<R> =
            Update { transform(value) }
    }

    companion object {
        private val logger = simpleLogger()

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
        fun <T> init(): Update<T> = INIT.unsafeCast<Update<T>>().also {
            logger.warn("INIT UPDATE\n    INSTANCE: ${it.toStringAndHash()}")
        }

        /**
         * Applies the (accumulated) transformation (see [extend] and [reset]) to the elements
         * of the given [flow]. The result itself is stored in `this` [MutableStateFlow]
         * so that:
         * - consecutive invocations are applied to the result instead of the elements of
         *   the specified [flow], and
         * - each transformation is only applied once.
         */
        fun <T, R : T> MutableStateFlow<FlowUpdate<T>>.applyUpdates(flow: Flow<R>): Flow<T> {
            logger.warn("APPLY UPDATES\n    BY: ${this.toStringAndHash()}\n    FLOW UPDATE: ${value.toStringAndHash()}\n    FLOW: ${flow.toStringAndHash()}")
            return combine(flow, FlowUpdate<T>::apply)
                .onEach { updated ->
                    logger.warn("APPLY UPDATES\n    BY: ${this.toStringAndHash()}\n    RESULT: ${updated.toStringAndHash()}")
                    update { Updated(updated).also { logger.warn("APPLY UPDATES COMPLETED\n    BY: ${this.toStringAndHash()}\n    STORED: ${it.toStringAndHash()}") } }
                }
        }

        /**
         * Updates the stored [FlowUpdate] with a flow update that applies the specified [transform]
         * to the result of the original flow update.
         */
        fun <T, R : T> MutableStateFlow<FlowUpdate<T>>.extend(transform: suspend (T) -> R) {
            logger.warn("EXTENDING\n    BY: ${this.toStringAndHash()}\n    FLOW UPDATE: ${value.toStringAndHash()}\n    TRANSFORM: ${transform.toStringAndHash()}")
            update {
                logger.warn("EXTENDING\n    BY: ${this.toStringAndHash()}\n    OLD: ${it.toStringAndHash()}")
                val map: FlowUpdate<T> = it.map(transform)
                logger.warn("EXTENDING COMPLETED\n    BY: ${this.toStringAndHash()}\n    NEW: ${map.toStringAndHash()}")
                map
            }
        }

        /**
         * Updates the stored [FlowUpdate] with a flow update that does not apply
         * any transformation.
         */
        fun <T> MutableStateFlow<FlowUpdate<T>>.reset() {
            logger.warn("RESETTING\n    BY: ${this.toStringAndHash()}\n    FLOW UPDATE: ${value.toStringAndHash()}")
            update { init() }
        }
    }
}
