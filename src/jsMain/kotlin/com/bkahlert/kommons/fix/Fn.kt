@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.kommons.fix

import com.bkahlert.kommons.fix.Either.Left
import com.bkahlert.kommons.fix.Either.Right
import com.bkahlert.kommons.fix.EitherTest.Return.A
import com.bkahlert.kommons.fix.EitherTest.Return.B


inline fun <T1, T2, R> Result<T1>.combine(other: Result<T2>, transform: (T1, T2) -> R): Result<R> {
    return map { t1: T1 -> other.map { t2: T2 -> transform(t1, t2) }.getOrThrow() }
}

inline fun <T1, T2, R> combine(result: Result<T1>, other: Result<T2>, transform: (T1, T2) -> R): Result<R> =
    result.combine(other, transform)

/**
 * Represents a container containing either an instance of type [A] ([Left])
 * or [B] ([Right]).
 */
public sealed interface Either<out A, out B> {
    public class Left<out A, out B>(public val left: A) : Either<A, B>

    public class Right<out A, out B>(public val right: B) : Either<A, B>
}

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
@Deprecated("remove (risk of loss of important information")
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

/**
 * Returns the result of [onLeft] for the encapsulated instance [A] or
 * the result of [onRight] for the encapsulated instance [B].
 */
public inline fun <A, B, R> Either<A, B>.fold(
    onLeft: (A) -> R,
    onRight: (B) -> R,
): R = when (this) {
    is Left -> onLeft(left)
    is Right -> onRight(right)
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


public inline fun <A, B, C, D, R> Either<Either<A, B>, Either<C, D>>.fold(
    onLeftLeft: (A) -> R,
    onLeftRight: (B) -> R,
    onRightLeft: (C) -> R,
    onRightRight: (D) -> R,
): R = map { ab: Either<A, B> ->
    ab.map { a -> onLeftLeft(a) }
        .or { b -> onLeftRight(b) }
}.or { cd: Either<C, D> ->
    cd.map { c -> onRightLeft(c) }
        .or { d -> onRightRight(d) }
}

public inline infix operator fun <reified A1, reified B1, reified A2, reified B2> Either<A1, B1>.times(other: Either<A2, B2>):
    Either<Either<Pair<A1, A2>, Pair<A1, B2>>, Either<Pair<B1, A2>, Pair<B1, B2>>> =
    map { a: A1 ->
        Left<Either<Pair<A1, A2>, Pair<A1, B2>>, Either<Pair<B1, A2>, Pair<B1, B2>>>(
            other
                .map<A2, B2, Left<Pair<A1, A2>, Pair<A1, B2>>> { c: A2 -> Left(a to c) }
                .or { d: B2 -> Right(a to d) })
    }.or { b: B1 ->
        Right<Either<Pair<A1, A2>, Pair<A1, B2>>, Either<Pair<B1, A2>, Pair<B1, B2>>>(
            other
                .map<A2, B2, Left<Pair<B1, A2>, Pair<B1, B2>>> { c: A2 -> Left(b to c) }
                .or { d: B2 -> Right(b to d) })
    }

public inline fun <reified A1, reified B1, reified A2, reified B2> Either<Either<Pair<A1, A2>, Pair<A1, B2>>, Either<Pair<B1, A2>, Pair<B1, B2>>>.simplify():
    Either<Pair<A1, Either<A2, B2>>, Pair<B1, Either<A2, B2>>> {
    return map { a1a2a1b2: Either<Pair<A1, A2>, Pair<A1, B2>> ->
        val left: Left<Pair<A1, Either<A2, B2>>, Pair<B1, Either<A2, B2>>> = Left<Pair<A1, Either<A2, B2>>, Pair<B1, Either<A2, B2>>>(
            a1a2a1b2.map { (a1: A1, a2: A2) -> a1 to Left<A2, B2>(a2) }
                .or { (a1: A1, b2: B2) -> a1 to Right<A2, B2>(b2) })
        left
    }.or { b1a2a1b2: Either<Pair<B1, A2>, Pair<B1, B2>> ->
        val right: Right<Pair<A1, Either<A2, B2>>, Pair<B1, Either<A2, B2>>> = Right<Pair<A1, Either<A2, B2>>, Pair<B1, Either<A2, B2>>>(
            b1a2a1b2.map { (b1: B1, a2: A2) -> b1 to Left<A2, B2>(a2) }
                .or { (b1: B1, b2: B2) -> b1 to Right<A2, B2>(b2) })
        right
    }
}
