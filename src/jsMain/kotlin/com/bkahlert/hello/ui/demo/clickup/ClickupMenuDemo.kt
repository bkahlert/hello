package com.bkahlert.hello.ui.demo.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.Brand.colors
import com.bkahlert.hello.plugins.clickup.ActivityItems
import com.bkahlert.hello.plugins.clickup.ClickUpState.Connected.TeamSelected
import com.bkahlert.hello.plugins.clickup.ClickUpState.Connected.TeamSelecting
import com.bkahlert.hello.plugins.clickup.ClickUpState.Failed
import com.bkahlert.hello.plugins.clickup.ConnectingClickUpMenu
import com.bkahlert.hello.plugins.clickup.DisconnectedClickUpMenu
import com.bkahlert.hello.plugins.clickup.FailedItems
import com.bkahlert.hello.plugins.clickup.InitializingClickUpMenu
import com.bkahlert.hello.plugins.clickup.Selection
import com.bkahlert.hello.plugins.clickup.TeamSelectingItems
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.bkahlert.hello.ui.demo.JOHN
import com.bkahlert.hello.ui.demo.clickup.ClickupFixtures.running
import com.bkahlert.hello.ui.demo.clickupException
import com.bkahlert.hello.ui.demo.failedResponse
import com.bkahlert.hello.ui.demo.response
import com.bkahlert.kommons.time.seconds
import com.clickup.api.Folder
import com.clickup.api.Space
import com.clickup.api.SpaceID
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.TaskID
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.rest.AccessToken
import com.semanticui.compose.collection.Menu
import io.ktor.http.Url
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ClickUpMenuDemo1() {

    Demos("ClickUp Menu (Not Connected)") {
        Demo("Initializing") {
            InitializingClickUpMenu()
        }
        Demo("Disconnected") {
            val scope = rememberCoroutineScope()
            var connecting by remember { mutableStateOf(false) }
            if (connecting) {
                ConnectingClickUpMenu()
            } else {
                DisconnectedClickUpMenu(
                    onConnect = {
                        console.info("connecting with $it")
                        connecting = true
                        scope.launch {
                            delay(2.5.seconds)
                            connecting = false
                        }
                    },
                )
            }
        }
        Demo("Connecting") {
            ConnectingClickUpMenu()
        }
        Demo("Failed") {
            val scope = rememberCoroutineScope()
            val accessToken = AccessToken("pk_123_ABC")
            var failure by remember { mutableStateOf(Failed(accessToken, clickupException)) }
            Menu({ +Size.Mini + Dimmable }) {
                FailedItems(
                    state = failure,
                    onConnect = {
                        console.info("connecting with $it")
                        scope.launch {
                            failure = Failed(accessToken, IllegalStateException("only a demo"))
                        }
                    },
                )
            }
        }
    }

    Demos("ClickUp Menu (Connected / Team Selection)") {
        Demo("multiple teams") {
            Menu({ +Size.Mini }) {
                TeamSelectingItems(TeamSelecting(ClickupFixtures.User,
                    listOf(ClickupFixtures.Team, ClickupFixtures.Team.copy(name = "Other Team", color = colors.green, avatar = Url(JOHN))))) {
                    console.info("Activating $it")
                }
            }
        }
        Demo("single team") {
            Menu({ +Size.Mini }) {
                TeamSelectingItems(TeamSelecting(ClickupFixtures.User, listOf(ClickupFixtures.Team))) {
                    console.info("Activating $it")
                }
            }
        }
        Demo("no team") {
            Menu({ +Size.Mini }) {
                TeamSelectingItems(TeamSelecting(ClickupFixtures.User, emptyList())) {
                    console.info("Activating $it")
                }
            }
        }
    }
}


object ClickUpStateFixtures {
    fun partiallyLoaded(
        team: Team = ClickupFixtures.Team,
        runningTimeEntry: Result<TimeEntry?>? = response(ClickupFixtures.TimeEntry.running()),
    ) = TeamSelected(
        user = ClickupFixtures.User,
        teams = listOf(team),
        selectedTeam = team,
        selected = listOfNotNull(runningTimeEntry?.map { it?.id }?.getOrNull()),
        runningTimeEntry = runningTimeEntry,
    )

    fun fullyLoaded(
        team: Team = ClickupFixtures.Team,
        runningTimeEntry: Result<TimeEntry?>? = response(ClickupFixtures.TimeEntry.running()),
        tasks: Result<List<Task>>? = response(ClickupFixtures.Tasks),
        spaces: Result<List<Space>>? = response(ClickupFixtures.SPACES),
        folders: Map<SpaceID, Result<List<Folder>>> = mapOf(
            ClickupFixtures.SPACES[0].id to response(ClickupFixtures.Space1Folders),
            ClickupFixtures.SPACES[1].id to response(ClickupFixtures.Space2Folders),
        ),
    ): TeamSelected = TeamSelected(
        user = ClickupFixtures.User,
        teams = listOf(team),
        selectedTeam = team,
        selected = listOfNotNull(runningTimeEntry?.map { it?.id }?.getOrNull()),
        runningTimeEntry = runningTimeEntry,
        tasks = tasks,
        spaces = spaces,
        folders = folders,
        spaceLists = mapOf(
            ClickupFixtures.SPACES[0].id to response(ClickupFixtures.Space1FolderlessLists),
            ClickupFixtures.SPACES[1].id to response(ClickupFixtures.Space2FolderlessLists),
        ),
        folderLists = mapOf(
            ClickupFixtures.Space1Folders[0].id to response(ClickupFixtures.Space1FolderLists),
        ),
    )
}

@Composable
fun ClickUpMenuDemo2() {

    Demos("ClickUp Menu (Connected / Partially Loaded)") {
        Demo("Unknown time entry status") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.partiallyLoaded(
                    runningTimeEntry = null
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
        Demo("Failed time entry request") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.partiallyLoaded(
                    runningTimeEntry = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
        Demo("Running time entry") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.partiallyLoaded(
                    runningTimeEntry = response(ClickupFixtures.TimeEntry.running())
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
        Demo("No running time entry") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.partiallyLoaded(
                    runningTimeEntry = response(null)
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
    }

    Demos("ClickUp Menu (Connected / Fully Loaded)") {
        Demo("Unknown time entry status") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.fullyLoaded(
                    runningTimeEntry = null
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
        Demo("Failed time entry request") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.fullyLoaded(
                    runningTimeEntry = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
        Demo("Running time entry") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.fullyLoaded(
                    runningTimeEntry = response(ClickupFixtures.TimeEntry.running())
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
        Demo("No running time entry") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.fullyLoaded(
                    runningTimeEntry = response(null)
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
    }

    Demos("ClickUp Menu (Special Cases)") {
        Demo("Failed tasks request") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.fullyLoaded(
                    tasks = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
        Demo("Failed spaces request") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.fullyLoaded(
                    spaces = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
        Demo("Incomplete spaces") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.fullyLoaded(
                    spaces = response(ClickupFixtures.SPACES.subList(0, 1))
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
        Demo("Folders missing") {
            Menu({ +Size.Mini }) {
                ActivityItems(ClickUpStateFixtures.fullyLoaded(
                    folders = emptyMap(),
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop, onRetry)
            }
        }
    }
}

private val onSelect: (selected: Selection) -> Unit = {
    console.info("selected $it")
}

private val onTimeEntryStart: (TaskID, List<Tag>, billable: Boolean) -> Unit = { task, tags, billable ->
    console.info("starting billable=$billable pomodoro $tags for $task")
}

private val onTimeEntryStop: (TimeEntry, List<Tag>) -> Unit = { timeEntry, tags ->
    console.info("stopping $timeEntry with $tags")
}

private val onRetry: () -> Unit = {
    console.info("retrying")
}
