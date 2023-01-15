package com.bkahlert.hello.dom

/**
 * Returns a list that contains one previous element for each element the specified [of] returns `true`.
 */
public fun <T> Iterable<T>.prev(of: (T) -> Boolean): List<T> = (this + this).windowed(2).mapNotNull { (current, next) -> current.takeIf { of(next) } }

/**
 * Returns a list that contains one next element for each element the specified [of] returns `true`.
 */
public fun <T> Iterable<T>.next(of: (T) -> Boolean): List<T> = (this + this).windowed(2).mapNotNull { (current, next) -> next.takeIf { of(current) } }
