package com.bkahlert.kommons.collections

/** Represents a quartet of values. */
public final data class Quadruple<out A, out B, out C, out D>(
    public val first: A,
    public val second: B,
    public val third: C,
    public val fourth: D,
)

/** Represents a quintet of values. */
public final data class Quintuple<out A, out B, out C, out D, out E>(
    public val first: A,
    public val second: B,
    public val third: C,
    public val fourth: D,
    public val fifth: E,
)

/**
 * Creates a tuple of type [Quadruple] from `this` [Triple] and [that].
 */
public infix fun <A, B, C, D> Triple<A, B, C>.too(that: D): Quadruple<A, B, C, D> =
    Quadruple(first, second, third, that)

/**
 * Creates a tuple of type [Quintuple] from `this` [Quadruple] and [that].
 */
public infix fun <A, B, C, D, E> Quadruple<A, B, C, D>.too(that: E): Quintuple<A, B, C, D, E> =
    Quintuple(first, second, third, fourth, that)
