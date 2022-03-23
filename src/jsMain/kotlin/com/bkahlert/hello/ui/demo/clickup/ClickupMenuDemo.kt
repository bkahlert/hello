package com.bkahlert.hello.ui.demo.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.Brand.colors
import com.bkahlert.hello.Response
import com.bkahlert.hello.plugins.clickup.ClickupMenuActivityItems
import com.bkahlert.hello.plugins.clickup.ClickupMenuFailedItems
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Failed
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.TeamSelected
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.TeamSelecting
import com.bkahlert.hello.plugins.clickup.ClickupMenuTeamSelectingItems
import com.bkahlert.hello.plugins.clickup.DisconnectedClickupMenu
import com.bkahlert.hello.plugins.clickup.InitializingClickupMenu
import com.bkahlert.hello.plugins.clickup.LoadingClickupMenu
import com.bkahlert.hello.plugins.clickup.Selection
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.bkahlert.hello.ui.demo.JOHN
import com.bkahlert.hello.ui.demo.clickupException
import com.bkahlert.hello.ui.demo.failedResponse
import com.bkahlert.hello.ui.demo.response
import com.bkahlert.kommons.fix.map
import com.bkahlert.kommons.fix.orNull
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
            Menu({ +Size.Mini }) {
                ClickupMenuFailedItems(Failed(clickupException))
            }
        }
    }

    Demos("ClickUp Menu (Activating)") {
        Demo("multiple teams") {
            Menu({ +Size.Mini }) {
                ClickupMenuTeamSelectingItems(TeamSelecting(ClickupFixtures.USER,
                    listOf(ClickupFixtures.TEAM, ClickupFixtures.TEAM.copy(name = "Other Team", color = colors.green, avatar = Url(JOHN))))) {
                    console.info("Activating $it")
                }
            }
        }
        Demo("single team") {
            Menu({ +Size.Mini }) {
                ClickupMenuTeamSelectingItems(TeamSelecting(ClickupFixtures.USER, listOf(ClickupFixtures.TEAM))) {
                    console.info("Activating $it")
                }
            }
        }
        Demo("no team") {
            Menu({ +Size.Mini }) {
                ClickupMenuTeamSelectingItems(TeamSelecting(ClickupFixtures.USER, emptyList())) {
                    console.info("Activating $it")
                }
            }
        }
    }
}

object ClickupMenuStateFixtures {
    fun justActivated(
        team: Team = ClickupFixtures.TEAM,
        runningTimeEntry: Response<TimeEntry?>? = response(TimeEntryFixtures.running()),
    ) = TeamSelected(
        user = ClickupFixtures.USER,
        teams = listOf(team),
        selectedTeam = team,
        selected = listOfNotNull(runningTimeEntry?.map { it?.id }?.orNull()),
        runningTimeEntry = runningTimeEntry,
    )

    fun activatedAndRefreshed(
        team: Team = ClickupFixtures.TEAM,
        runningTimeEntry: Response<TimeEntry?>? = response(TimeEntryFixtures.running()),
        tasks: Response<List<Task>>? = response(ClickupFixtures.TASKS),
        spaces: Response<List<Space>>? = response(ClickupFixtures.SPACES),
        folders: Map<SpaceID, Response<List<Folder>>> = mapOf(
            ClickupFixtures.SPACES[0].id to response(ClickupFixtures.SPACE1_FOLDERS),
            ClickupFixtures.SPACES[1].id to response(ClickupFixtures.SPACE2_FOLDERS),
        ),
    ): TeamSelected = TeamSelected(
        user = ClickupFixtures.USER,
        teams = listOf(team),
        selectedTeam = team,
        selected = listOfNotNull(runningTimeEntry?.map { it?.id }?.orNull()),
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
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.justActivated(
                    runningTimeEntry = null
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
        Demo("Failed time entry request") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.justActivated(
                    runningTimeEntry = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
        Demo("Running time entry") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.justActivated(
                    runningTimeEntry = response(TimeEntryFixtures.running())
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
        Demo("No running time entry") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.justActivated(
                    runningTimeEntry = response(null)
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
    }

    Demos("ClickUp Menu (Activated & Refreshed)") {
        Demo("Unknown time entry status") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    runningTimeEntry = null
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
        Demo("Failed time entry request") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    runningTimeEntry = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
        Demo("Running time entry") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    runningTimeEntry = response(TimeEntryFixtures.running())
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
        Demo("No running time entry") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    runningTimeEntry = response(null)
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
    }

    Demos("ClickUp Menu (Activated & Refresh Failed)") {
        Demo("Failed to request tasks") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    tasks = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
        Demo("Failed to spaces") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    spaces = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
        Demo("Incomplete spaces") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    spaces = response(ClickupFixtures.SPACES.subList(0, 1))
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
            }
        }
        Demo("Folders missing") {
            Menu({ +Size.Mini }) {
                ClickupMenuActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    folders = emptyMap(),
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryAbort, onTimeEntryComplete)
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

private val onTimeEntryAbort: (TimeEntry, List<Tag>) -> Unit = { timeEntry, tags ->
    console.info("aborting $timeEntry with $tags")
}

private val onTimeEntryComplete: (TimeEntry, List<Tag>) -> Unit = { timeEntry, tags ->
    console.info("completing $timeEntry with $tags")
}
