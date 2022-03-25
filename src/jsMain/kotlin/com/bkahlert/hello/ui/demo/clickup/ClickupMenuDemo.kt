package com.bkahlert.hello.ui.demo.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.Brand.colors
import com.bkahlert.hello.plugins.clickup.ClickMenuLoadingActivityItems
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
import com.bkahlert.hello.ui.demo.clickup.ClickupFixtures.running
import com.bkahlert.hello.ui.demo.clickupException
import com.bkahlert.hello.ui.demo.failedResponse
import com.bkahlert.hello.ui.demo.response
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
                ClickupMenuTeamSelectingItems(TeamSelecting(ClickupFixtures.User,
                    listOf(ClickupFixtures.Team, ClickupFixtures.Team.copy(name = "Other Team", color = colors.green, avatar = Url(JOHN))))) {
                    console.info("Activating $it")
                }
            }
        }
        Demo("single team") {
            Menu({ +Size.Mini }) {
                ClickupMenuTeamSelectingItems(TeamSelecting(ClickupFixtures.User, listOf(ClickupFixtures.Team))) {
                    console.info("Activating $it")
                }
            }
        }
        Demo("no team") {
            Menu({ +Size.Mini }) {
                ClickupMenuTeamSelectingItems(TeamSelecting(ClickupFixtures.User, emptyList())) {
                    console.info("Activating $it")
                }
            }
        }
    }
}

object ClickupMenuStateFixtures {
    fun justActivated(
        team: Team = ClickupFixtures.Team,
        runningTimeEntry: Result<TimeEntry?>? = response(ClickupFixtures.TimeEntry.running()),
    ) = TeamSelected(
        user = ClickupFixtures.User,
        teams = listOf(team),
        selectedTeam = team,
        selected = listOfNotNull(runningTimeEntry?.map { it?.id }?.getOrNull()),
        runningTimeEntry = runningTimeEntry,
    )

    fun activatedAndRefreshed(
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
fun ClickupMenuDemo2() {

    Demos("ClickUp Menu (Just Activated)") {
        Demo("Unknown time entry status") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.justActivated(
                    runningTimeEntry = null
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
        Demo("Failed time entry request") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.justActivated(
                    runningTimeEntry = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
        Demo("Running time entry") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.justActivated(
                    runningTimeEntry = response(ClickupFixtures.TimeEntry.running())
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
        Demo("No running time entry") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.justActivated(
                    runningTimeEntry = response(null)
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
    }

    Demos("ClickUp Menu (Activated & Refreshed)") {
        Demo("Unknown time entry status") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    runningTimeEntry = null
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
        Demo("Failed time entry request") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    runningTimeEntry = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
        Demo("Running time entry") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    runningTimeEntry = response(ClickupFixtures.TimeEntry.running())
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
        Demo("No running time entry") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    runningTimeEntry = response(null)
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
    }

    Demos("ClickUp Menu (Activated & Refresh Failed)") {
        Demo("Failed to request tasks") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    tasks = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
        Demo("Failed to spaces") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    spaces = failedResponse()
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
        Demo("Incomplete spaces") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    spaces = response(ClickupFixtures.SPACES.subList(0, 1))
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
            }
        }
        Demo("Folders missing") {
            Menu({ +Size.Mini }) {
                ClickMenuLoadingActivityItems(ClickupMenuStateFixtures.activatedAndRefreshed(
                    folders = emptyMap(),
                ).activityGroups, onSelect, onTimeEntryStart, onTimeEntryStop)
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
