package com.bkahlert.kommons.util

// TODO move to kommons-core

/**
 * Returns a list that contains the predecessor for each element the specified [predicate] returns `true`.
 */
public fun <T> List<T>.predecessor(predicate: (T) -> Boolean): List<T> {
    if (isEmpty()) return this
    return mapIndexedNotNull { index, element ->
        if (predicate(element)) {
            if (index > 0) get(index - 1) else get(size - 1)
        } else {
            null
        }
    }
}

/**
 * Returns a list that contains one successor element for each element the specified [predicate] returns `true`.
 */
public fun <T> List<T>.successor(predicate: (T) -> Boolean): List<T> {
    if (isEmpty()) return this
    val limit = size - 1
    return mapIndexedNotNull { index, element ->
        if (predicate(element)) {
            if (index < limit) get(index + 1) else get(0)
        } else {
            null
        }
    }
}
