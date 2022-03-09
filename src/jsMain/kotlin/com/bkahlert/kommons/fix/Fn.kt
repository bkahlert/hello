package com.bkahlert.kommons.fix

import com.bkahlert.kommons.Either
import com.bkahlert.kommons.Either.Left
import com.bkahlert.kommons.Either.Right

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
 * Returns either the encapsulated instance [A] mapped using [transform] or
 * the encapsulated instance [B].
 */
public inline fun <A, B, R> Either<A, B>.map(transform: (A) -> R): Either<R, B> =
    when (this) {
        is Left -> Left(transform(left))
        is Right ->
            @Suppress("UNCHECKED_CAST")
            this as Either<R, B>
    }
