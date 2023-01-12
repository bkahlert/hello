package com.bkahlert.hello

import com.bkahlert.hello.clickup.model.ClickUpFixtures
import com.bkahlert.hello.clickup.ui.PomodoroTimer
import com.bkahlert.hello.clickup.ui.rememberPomodoroTimerState
import com.bkahlert.kommons.minus
import io.kotest.assertions.asClue
import io.kotest.matchers.string.shouldContain
import org.jetbrains.compose.web.testutils.runTest
import kotlin.js.Date
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes

class PomodoroTimerTest {

    @Test
    fun testTimeFormat() = runTest {
        composition {
            PomodoroTimer(rememberPomodoroTimerState(ClickUpFixtures.timeEntry(start = Date() - 2.minutes)))
        }

        root.innerHTML.takeLast(50).asClue {
            it shouldContain ">22:59<"
        }
    }
}
