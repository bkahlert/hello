package com.bkahlert.kommons.js

/**
 * Returns the value of the object bound to
 * the [JavaScript `this` keyword](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/this).
 */
public inline val context: dynamic get() = js("this")

/**
 * Convenience [context] variant
 * that returns the [context] cast to an instance of [T].
 */
public inline fun <reified T> context(): T = context.unsafeCast<T>()
