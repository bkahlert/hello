package com.bkahlert.kommons.util

/**
 * The enum constant [E] with the [Enum.ordinal]
 * being this constant's ordinal -1.
 *
 * If this enum constant is the first constant,
 * the last one is returned.
 */
public inline val <reified E : Enum<E>> E.predecessor: E
    get() {
        val enumValues = enumValues<E>()
        return enumValues[(ordinal - 1).mod(enumValues.size)]
    }

/**
 * The enum constant [E] with the [Enum.ordinal]
 * being this constant's ordinal +1.
 *
 * If this enum constant is the last constant,
 * the first one is returned.
 */
public inline val <reified E : Enum<E>> E.successor: E
    get() {
        val enumValues = enumValues<E>()
        return enumValues[(ordinal + 1).mod(enumValues.size)]
    }
