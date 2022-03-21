package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import com.bkahlert.Brand
import com.bkahlert.hello.Response
import com.bkahlert.hello.plugins.clickup.ActivatedClickupMenu
import com.bkahlert.hello.plugins.clickup.ActivatingClickupMenu
import com.bkahlert.hello.plugins.clickup.ClickupMenuState
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activated
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activating
import com.bkahlert.hello.plugins.clickup.DisconnectedClickupMenu
import com.bkahlert.hello.plugins.clickup.FailedClickupMenu
import com.bkahlert.hello.plugins.clickup.InitializingClickupMenu
import com.bkahlert.hello.plugins.clickup.LoadingClickupMenu
import com.clickup.api.Folder
import com.clickup.api.Space
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.rest.AccessToken
import io.ktor.http.Url

@Composable
fun ClickupMenuDemo1() {

    Demos("ClickUp Menu (Not Activated)") {
        Demo("Initializing") {
            InitializingClickupMenu()
        }
        Demo("Disconnected") {
            DisconnectedClickupMenu { details ->
                details(AccessToken("most recently used access token")) {
                    console.info("using $it")
                }
            }
        }
        Demo("Loading") {
            LoadingClickupMenu()
        }
        Demo("Failed") {
            FailedClickupMenu(ClickupMenuState.Failed(clickupException))
        }
    }

    Demos("ClickUp Menu (Activating)") {
        Demo("multiple teams") {
            ActivatingClickupMenu(Activating(ClickupFixtures.USER,
                listOf(ClickupFixtures.TEAM, ClickupFixtures.TEAM.copy(name = "Other Team", color = Brand.colors.green, avatar = Url(JOHN))))) {
                console.info("Activating $it")
            }
        }
        Demo("single team") {
            ActivatingClickupMenu(Activating(ClickupFixtures.USER, listOf(ClickupFixtures.TEAM))) {
                console.info("Activating $it")
            }
        }
        Demo("no team") {
            ActivatingClickupMenu(Activating(ClickupFixtures.USER, emptyList())) {
                console.info("Activating $it")
            }
        }
    }
}

object ClickupMenuStateFixtures {
    fun justActivated(
        team: Team = ClickupFixtures.TEAM,
        runningTimeEntry: Response<TimeEntry?>? = response(TimeEntryFixtures.running()),
    ) = Activated(
        user = ClickupFixtures.USER,
        teams = listOf(team),
        activeTeam = team,
        runningTimeEntry = runningTimeEntry,
    )

    fun activatedAndRefreshed(
        team: Team = ClickupFixtures.TEAM,
        runningTimeEntry: Response<TimeEntry?>? = response(TimeEntryFixtures.running()),
        tasks: Response<List<Task>>? = response(ClickupFixtures.TASKS),
        spaces: Response<List<Space>>? = response(ClickupFixtures.SPACES),
        folders: Map<Space.ID, Response<List<Folder>>> = mapOf(
            ClickupFixtures.SPACES[0].id to response(ClickupFixtures.SPACE1_FOLDERS),
            ClickupFixtures.SPACES[1].id to response(ClickupFixtures.SPACE2_FOLDERS),
        ),
    ): Activated = Activated(
        user = ClickupFixtures.USER,
        teams = listOf(team),
        activeTeam = team,
        runningTimeEntry = runningTimeEntry,
        tasks = tasks,
        spaces = spaces,
        folders = folders,
        spaceLists = mapOf(
            ClickupFixtures.SPACES[0].id to response(ClickupFixtures.SPACE1_FOLDERLESS_LISTS),
            ClickupFixtures.SPACES[1].id to response(ClickupFixtures.SPACE2_FOLDERLESS_LISTS),
        ),
        folderLists = mapOf(
            ClickupFixtures.SPACE1_FOLDERS[0].id to response(ClickupFixtures.SPACE1_FOLDER_LISTS),
        ),
    )
}

@Composable
fun ClickupMenuDemo2() {

    Demos("ClickUp Menu (Just Activated)") {
        Demo("Unknown time entry status") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.justActivated(
                runningTimeEntry = null
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
        Demo("Failed time entry request") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.justActivated(
                runningTimeEntry = failedResponse()
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
        Demo("Running time entry") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.justActivated(
                runningTimeEntry = response(TimeEntryFixtures.running())
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
        Demo("No running time entry") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.justActivated(
                runningTimeEntry = response(null)
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
    }

    Demos("ClickUp Menu (Activated & Refreshed)") {
        Demo("Unknown time entry status") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.activatedAndRefreshed(
                runningTimeEntry = null
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
        Demo("Failed time entry request") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.activatedAndRefreshed(
                runningTimeEntry = failedResponse()
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
        Demo("Running time entry") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.activatedAndRefreshed(
                runningTimeEntry = response(TimeEntryFixtures.running())
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
        Demo("No running time entry") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.activatedAndRefreshed(
                runningTimeEntry = response(null)
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
    }

    Demos("ClickUp Menu (Activated & Refresh Failed)") {
        Demo("Failed to request tasks") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.activatedAndRefreshed(
                tasks = failedResponse()
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
        Demo("Failed to spaces") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.activatedAndRefreshed(
                spaces = failedResponse()
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
        Demo("Incomplete spaces") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.activatedAndRefreshed(
                spaces = response(ClickupFixtures.SPACES.subList(0, 1))
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
        Demo("Folders missing") {
            ActivatedClickupMenu(ClickupMenuStateFixtures.activatedAndRefreshed(
                folders = emptyMap(),
            ), onRefresh, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
        }
    }
}

private val onRefresh: () -> Unit = {
    console.info("refreshing")
}

private val onTimeEntryStart: (Task.ID, List<Tag>, billable: Boolean) -> Unit = { task, tags, billable ->
    console.info("starting billable=$billable pomodoro $tags for $task")
}

private val onTimeEntryAbort: (TimeEntry, List<Tag>) -> Unit = { timeEntry, tags ->
    console.info("aborting $timeEntry with $tags")
}

private val onTimeEntryComplete: (TimeEntry, List<Tag>) -> Unit = { timeEntry, tags ->
    console.info("completing $timeEntry with $tags")
}
