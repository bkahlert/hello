package com.bkahlert.kommons

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


// TODO move to Kommons
operator fun Clock.Companion.invoke(now: () -> Instant): Clock = object : Clock {
    override fun now(): Instant = now()
}

// TODO move to Kommons Test
fun Clock.Companion.fixed(now: Instant): Clock = invoke { now }
