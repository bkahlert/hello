package com.bkahlert.kommons.js

import io.kotest.assertions.asClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeInstanceOf
import kotlin.test.Test

class ContextKtTest {

    @Test
    fun context() {
        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
        var result: dynamic = "initial"

        @Suppress("UNUSED_VARIABLE")
        val callback: () -> Unit = {
            result = context
        }

        js("function callWithoutContext(fn) { fn() }")
        js("callWithoutContext(callback)")
        "result === undefined".asClue {
            js("result === undefined").unsafeCast<Boolean>()
        }.shouldBeTrue()

        js("function callWithContext(fn) { fn.call(42) }")
        js("callWithContext(callback)")
        "result === 42".asClue {
            js("result === 42").unsafeCast<Boolean>()
        }.shouldBeTrue()
    }

    @Test
    fun context_and_cast() {
        @Suppress("RedundantExplicitType")
        var result: String = "initial"

        @Suppress("UNUSED_VARIABLE")
        val callback: () -> Unit = {
            result = context<String>()
        }

        js("function callWithoutContext(fn) { fn() }")
        js("callWithoutContext(callback)")
        "result === undefined".asClue {
            js("result === undefined").unsafeCast<Boolean>()
        }.shouldBeTrue()

        js("function callWithContext(fn) { fn.call(42) }")
        js("callWithContext(callback)")
        "result === 42".asClue {
            js("result === 42").unsafeCast<Boolean>()
        }.shouldBeTrue()

        result.shouldNotBeInstanceOf<String>()
        result.shouldBeInstanceOf<Int>()
    }
}
