package com.bkahlert.kommons.js

/**
 * Replaces the [toString] implementation with the specified [fn].
 *
 * This is useful when the applied logger does not pass
 * arguments as is to the [console] but applies [toString]
 * to them.
 */
public fun Any.toString(fn: () -> String) {
    this.asDynamic().toString = fn
}
