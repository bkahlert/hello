package com.bkahlert.hello.data

import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import kotlin.coroutines.cancellation.CancellationException
import kotlin.reflect.KClass

public sealed interface Resource<out T> {

    /** Successfully loaded [data]. */
    public data class Success<out T>(
        /** The loaded data. */
        public val data: T,
    ) : Resource<T>

    /** A failed attempt to load data. */
    public data class Failure(
        /** An optional message describing the failure. */
        override val message: String? = null,
        /** The cause of this failed state. */
        override val cause: Throwable? = null,
    ) : Resource<Nothing>, RuntimeException(message, cause)

    public companion object {

        public suspend fun <T> load(failureMessage: String?, block: suspend () -> T): Resource<T> = try {
            Success(block())
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Throwable) {
            Failure(failureMessage, ex)
        }

        public suspend fun <T : Any> load(type: KClass<T>, block: suspend () -> T): Resource<T> =
            load("An unexpected problem occurred while loading ${type.simpleName ?: "resource"}", block)

        public suspend inline fun <reified T : Any> load(noinline block: suspend () -> T): Resource<T> =
            load(T::class, block)
    }
}

public fun <T> Resource<T>.getDataOrThrow(): T = when (this) {
    is Success -> data
    is Failure -> throw this
}
