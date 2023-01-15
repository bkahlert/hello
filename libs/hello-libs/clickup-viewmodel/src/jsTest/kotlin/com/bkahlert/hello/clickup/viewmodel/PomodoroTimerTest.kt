package com.bkahlert.hello.clickup.viewmodel

import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.kommons.minus
import io.kotest.assertions.asClue
import io.kotest.matchers.string.shouldContain
import org.jetbrains.compose.web.testutils.runTest
import kotlin.js.Date
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroTimerTest {

    @Test
    fun testTimeFormat() = runTest {
        composition {
            PomodoroTimer(rememberPomodoroTimerState(ClickUpFixtures.timeEntry(start = Date() - 2.minutes - (0.5).seconds)))
        }

        root.innerHTML.takeLast(50).asClue {
            it shouldContain ">22:59<"
        }
    }
}
