package com.bkahlert.kommons.fix

import com.bkahlert.kommons.Either
import com.bkahlert.kommons.Either.Left
import com.bkahlert.kommons.Either.Right
import com.bkahlert.kommons.fix.EitherTest.Return.A
import com.bkahlert.kommons.fix.EitherTest.Return.B
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract


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
        is Right -> @Suppress("UNCHECKED_CAST") (this as Either<R, B>)
    }

/** Alias for [Left.left] */
public inline val <A, B> Left<A, B>.value: A get() = left

/** Alias for [Right.right] */
public inline val <A, B> Right<A, B>.value: B get() = right

/**
 * Calls the specified function [block] with the value of [Left] as its receiver and returns its result.
 */
public inline fun <A, B, R> Left<A, B>.runValue(block: A.() -> R): R {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    return left.block()
}

/**
 * Calls the specified function [block] with the value of [Right] as its receiver and returns its result.
 */
public inline fun <A, B, R> Right<A, B>.runValue(block: B.() -> R): R {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    return right.block()
}

/**
 * Calls the specified function [block] with the value of [Left] as its receiver and returns `this` value.
 */
public inline fun <A, B> Left<A, B>.applyToValue(block: A.() -> Unit): A {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    left.block()
    return left
}

/**
 * Calls the specified function [block] with the value of [Right] as its receiver and returns `this` value.
 */
public inline fun <A, B> Right<A, B>.applyToValue(block: B.() -> Unit): B {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    right.block()
    return right
}

/**
 * Calls the specified function [block] with the value of [Left] as its argument and returns `this` value.
 */
public inline fun <A, B> Left<A, B>.alsoValue(block: (A) -> Unit): A {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    block(left)
    return left
}

/**
 * Calls the specified function [block] with the value of [Right] as its argument and returns `this` value.
 */
public inline fun <A, B> Right<A, B>.alsoValue(block: (B) -> Unit): B {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    block(right)
    return right
}

/**
 * Calls the specified function [block] with the value of [Left] as its argument and returns its result.
 */
public inline fun <A, B, R> Left<A, B>.letValue(block: (A) -> R): R {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    return block(left)
}

/**
 * Calls the specified function [block] with the value of [Right] as its argument and returns its result.
 */
public inline fun <A, B, R> Right<A, B>.letValue(block: (B) -> R): R {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    return block(right)
}

/**
 * Returns the value of [Left] if it satisfies the given [predicate] or `null`, if it doesn't.
 */
public inline fun <A, B> Left<A, B>.takeValueIf(predicate: (A) -> Boolean): A? {
    contract {
        callsInPlace(predicate, EXACTLY_ONCE)
    }
    return if (predicate(left)) left else null
}

/**
 * Returns the value of [Right] if it satisfies the given [predicate] or `null`, if it doesn't.
 */
public inline fun <A, B> Right<A, B>.takeValueIf(predicate: (B) -> Boolean): B? {
    contract {
        callsInPlace(predicate, EXACTLY_ONCE)
    }
    return if (predicate(right)) right else null
}

/**
 * Returns the value of [Left] if it _does not_ satisfy the given [predicate] or `null`, if it does.
 */
public inline fun <A, B> Left<A, B>.takeValueUnless(predicate: (A) -> Boolean): A? {
    contract {
        callsInPlace(predicate, EXACTLY_ONCE)
    }
    return if (!predicate(left)) left else null
}

/**
 * Returns the value of [Left] if it _does not_ satisfy the given [predicate] or `null`, if it does.
 */
public inline fun <A, B> Right<A, B>.takeValueUnless(predicate: (B) -> Boolean): B? {
    contract {
        callsInPlace(predicate, EXACTLY_ONCE)
    }
    return if (!predicate(right)) right else null
}
