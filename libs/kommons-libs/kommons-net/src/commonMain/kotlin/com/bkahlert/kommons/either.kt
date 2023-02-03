package com.bkahlert.kommons

import com.bkahlert.kommons.Either.Left
import com.bkahlert.kommons.Either.Right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.jvm.JvmName

/**
 * Calls the specified function [block] and returns its encapsulated result as [Either.Left] if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution and encapsulating it as [Either.Right].
 */
public inline fun <R> either(block: () -> R): Either<R, Throwable> =
    try {
        Left(block())
    } catch (e: Throwable) {
        Right(e)
    }

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [Right] or the
 * original encapsulated [Left] value if it's [Left].
 */
public inline infix fun <R, A, B> Either<A, B>.or(transform: (B) -> R): Either<A, R> =
    mapRight(transform)

/**
 * Returns the result of [onLeft] for the encapsulated instance [A] or
 * the result of [onRight] for the encapsulated instance [B].
 */
public fun <A : Any, B : Any, R> Either<A?, B>.fold(
    onNothingLeft: () -> R,
    onLeft: (A) -> R,
    onRight: (B) -> R,
): R = when (val current: Either<A?, B> = this) {
    is Left -> when (val left = current.value) {
        null -> onNothingLeft()
        else -> onLeft(left)
    }

    is Right -> onRight(current.value)
}

/**
 * Returns the result of [onLeft] for the encapsulated instance [A] or
 * the result of [onRight] for the encapsulated instance [B].
 */
public fun <A : Any, B : Any, R> Either<A, B?>.fold(
    onLeft: (A) -> R,
    onRight: (B) -> R,
    onNothingRight: () -> R,
): R = when (val current = this) {
    is Left -> onLeft(current.value)
    is Right -> when (val right = current.value) {
        null -> onNothingRight()
        else -> onRight(right)
    }
}

/**
 * Returns the result of [onLeft] for the encapsulated instance [A] or
 * the result of [onRight] for the encapsulated instance [B].
 */
@JvmName("foldNothingLeftNothingRight")
public fun <A : Any, B : Any, R> Either<A?, B?>.fold(
    onNothingLeft: () -> R,
    onLeft: (A) -> R,
    onRight: (B) -> R,
    onNothingRight: () -> R,
): R = when (val current: Either<A?, B?> = this) {
    is Left -> when (val left = current.value) {
        null -> onNothingLeft()
        else -> onLeft(left)
    }

    is Right -> when (val right = current.value) {
        null -> onNothingRight()
        else -> onRight(right)
    }
}


/**
 * Returns the result of [onLeft] for the encapsulated instance [A] or
 * the result of [onRight] for the encapsulated instance [B].
 */
@JvmName("foldNothingLeftNothing")
public fun <A : Any, B : Any, R> Either<A?, B>?.fold(
    onNothing: () -> R,
    onNothingLeft: () -> R,
    onLeft: (A) -> R,
    onRight: (B) -> R,
): R = when (val current = this) {
    null -> onNothing()
    is Left -> when (val left = current.value) {
        null -> onNothingLeft()
        else -> onLeft(left)
    }

    is Right -> onRight(current.value)
}

/**
 * Returns the result of [onLeft] for the encapsulated instance [A] or
 * the result of [onRight] for the encapsulated instance [B].
 */
@JvmName("foldNothingRightNothing")
public fun <A : Any, B : Any, R> Either<A, B?>?.fold(
    onNothing: () -> R,
    onLeft: (A) -> R,
    onRight: (B) -> R,
    onNothingRight: () -> R,
): R = when (val current: Either<A, B?>? = this) {
    null -> onNothing()
    is Left -> onLeft(current.value)
    is Right -> when (val right = current.value) {
        null -> onNothingRight()
        else -> onRight(right)
    }
}

/**
 * Returns the result of [onLeft] for the encapsulated instance [A] or
 * the result of [onRight] for the encapsulated instance [B].
 */
@JvmName("foldNothingLeftNothingRightNothing")
public fun <A : Any, B : Any, R> Either<A?, B?>?.fold(
    onNothing: () -> R,
    onNothingLeft: () -> R,
    onLeft: (A) -> R,
    onRight: (B) -> R,
    onNothingRight: () -> R,
): R = when (val current: Either<A?, B?>? = this) {
    null -> onNothing()
    is Left -> when (val left = current.value) {
        null -> onNothingLeft()
        else -> onLeft(left)
    }

    is Right -> when (val right = current.value) {
        null -> onNothingRight()
        else -> onRight(right)
    }
}


// Flow extensions

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [Left] or the
 * original encapsulated [Right] value if it's [Right].
 */
public inline infix fun <R, A, B> Flow<Either<A, B>>.mapLeft(crossinline transform: suspend (A) -> R): Flow<Either<R, B>> = map {
    it.mapLeft { left -> transform(left) }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [Right] or the
 * original encapsulated [Left] value if it's [Left].
 */
public inline infix fun <R, A, B> Flow<Either<A, B>>.mapRight(crossinline transform: suspend (B) -> R): Flow<Either<A, R>> = map {
    it.mapRight { right -> transform(right) }
}
