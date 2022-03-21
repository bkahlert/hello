package com.bkahlert.kommons.fix

import com.bkahlert.kommons.Either
import com.bkahlert.kommons.Either.Left
import com.bkahlert.kommons.Either.Right
import com.bkahlert.kommons.fix.EitherTest.Return.A
import com.bkahlert.kommons.fix.EitherTest.Return.B

/**
 * Returns either the encapsulated instance [A] or
 * the encapsulated instance [B] transformed to an instance of [A]
 * using the given [transform].
 */
public inline infix fun <A : R, B, R> Either<A, B>.or(transform: (B) -> R): R =
    when (this) {
        is Left -> left
        is Right -> transform(right)
    }

/**
 * Returns either the encapsulated instance [A] or `null`
 * (ignoring the encapsulated instance [B]).
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <A> Either<A, *>.orNull(): A? =
    when (this) {
        is Left -> left
        is Right -> null
    }

/**
 * Returns either the encapsulated instance [A] mapped using [transform] or
 * the encapsulated instance [B].
 */
public inline fun <A, B, R> Either<A, B>.map(transform: (A) -> R): Either<R, B> =
    when (this) {
        is Left -> Left(transform(left))
        is Right -> @Suppress("UNCHECKED_CAST") (this as Either<R, B>)
    }

/** Alias for [Left.left] */
public inline val <A, B> Left<A, B>.value: A get() = left

/** Alias for [Right.right] */
public inline val <A, B> Right<A, B>.value: B get() = right

/**
 * Converts this [Either] to a [Result].
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Either<T, Throwable>.asResult(): Result<T> =
    map { Result.success(it) } or { Result.failure(it) }

/**
 * Converts this [Result] to an [Either].
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Result<T>.asEither(): Either<T, Throwable> =
    fold({ Left(it) }, { Right(it) })
