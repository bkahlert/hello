package com.bkahlert.hello.debug.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.Team
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures.running
import com.bkahlert.hello.clickup.model.fixtures.ClickUpTestClient
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenu
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disconnected
import com.bkahlert.hello.clickup.viewmodel.fixtures.rememberClickUpMenuTestViewModel
import com.bkahlert.hello.clickup.viewmodel.fixtures.toFullyLoaded
import com.bkahlert.hello.clickup.viewmodel.fixtures.toPartiallyLoaded
import com.bkahlert.hello.clickup.viewmodel.fixtures.toTeamSelecting
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.hello.debug.clickupException

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
            "Multiple Teams" to ClickUpFixtures.Teams.subList(0, 2),
            "Single Team" to ClickUpFixtures.Teams.subList(0, 1),
            "No Teams" to emptyList(),
        ).forEach { (name, teams) ->
            Demo(name) {
                ClickUpMenu(rememberClickUpMenuTestViewModel(ClickUpTestClient(initialTeams = teams)) { toTeamSelecting() })
            }
        }
    }

    mapOf(
        "No Running Time Entry" to null,
        "Running Time Entry" to ClickUpFixtures.TimeEntry.running(),
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

    Demos("ClickUp Menu (Synchronized)") {
        val client = ClickUpTestClient(initialRunningTimeEntry = ClickUpFixtures.TimeEntry.running())
        Demo("Browser 1") {
            ClickUpMenu(rememberClickUpMenuTestViewModel(client) { toPartiallyLoaded(runningTimeEntry = null) })
        }
        Demo("Browser 2") {
            ClickUpMenu(rememberClickUpMenuTestViewModel(client) { toFullyLoaded() })
        }
    }
}
