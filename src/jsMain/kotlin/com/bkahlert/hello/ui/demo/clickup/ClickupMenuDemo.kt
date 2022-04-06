package com.bkahlert.hello.ui.demo.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.Brand.colors
import com.bkahlert.hello.plugins.clickup.ClickUpMenu
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Disconnected
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.bkahlert.hello.ui.demo.JOHN
import com.bkahlert.hello.ui.demo.clickup.ClickupFixtures.running
import com.bkahlert.hello.ui.demo.clickupException
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import io.ktor.http.Url

@Composable
fun ClickUpMenuDemo() {
    Demos("ClickUp Menu (Not Connected)") {
        Demo("Disabled") {
            ClickUpMenu(rememberClickUpMenuTestViewModel { Disabled })
        }
        Demo("Disconnected") {
            ClickUpMenu(rememberClickUpMenuTestViewModel { Disconnected })
        }
    }
    Demos("ClickUp Menu (Team Selecting)") {
        mapOf(
            "Multiple Teams" to listOf(ClickupFixtures.Team, ClickupFixtures.Team.copy(name = "Other Team", color = colors.green, avatar = Url(JOHN))),
            "Single Team" to listOf(ClickupFixtures.Team),
            "No Teams" to emptyList(),
        ).forEach { (name, teams) ->
            Demo(name) {
                ClickUpMenu(rememberClickUpMenuTestViewModel(ClickUpTestClient(initialTeams = teams)) { toTeamSelecting() })
            }
        }
    }

    mapOf(
        "No Running Time Entry" to null,
        "Running Time Entry" to ClickupFixtures.TimeEntry.running(),
    ).forEach { (name, runningTimeEntry) ->
        fun client() = ClickUpTestClient(
            initialRunningTimeEntry = runningTimeEntry,
        )
        Demos("ClickUp Menu ($name)") {
            Demo("Connected — Partially Loaded") {
                ClickUpMenu(rememberClickUpMenuTestViewModel(client()) { toPartiallyLoaded() })
            }
            Demo("Connected — Fully Loaded") {
                ClickUpMenu(rememberClickUpMenuTestViewModel(client()) { toFullyLoaded() })
            }
            Demo("Connected — Fully Loaded — No Selection") {
                ClickUpMenu(rememberClickUpMenuTestViewModel(client()) {
                    toFullyLoaded(
                        select = { _, _ -> false }
                    )
                })
            }
            Demo("Connected — Fully Loaded — Task Selected") {
                ClickUpMenu(rememberClickUpMenuTestViewModel(client()) {
                    toFullyLoaded(
                        select = { index, _ -> index == 3 }
                    )
                })
            }
            Demo("Connected — Fully Loaded — Multiple Selected") {
                ClickUpMenu(rememberClickUpMenuTestViewModel(client()) {
                    toFullyLoaded(
                        select = { index, _ -> index in 10..15 }
                    )
                })
            }
            Demo("Failing (First Stop Attempt)") {
                val failingClient = object : ClickUpTestClient(
                    initialRunningTimeEntry = runningTimeEntry,
                ) {
                    private var failing: Int = 1
                    override suspend fun stopTimeEntry(team: Team): TimeEntry? {
                        console.warn("stopTimeEntry invocation...")
                        if (failing > 0) {
                            failing--
                            console.warn("...will fail")
                            throw clickupException
                        }
                        console.warn("...will succeed")
                        return super.stopTimeEntry(team)
                    }
                }
                ClickUpMenu(rememberClickUpMenuTestViewModel(failingClient) { toFullyLoaded() })
            }
        }
    }
}
