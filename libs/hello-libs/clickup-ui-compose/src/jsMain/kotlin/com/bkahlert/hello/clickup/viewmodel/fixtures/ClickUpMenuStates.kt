package com.bkahlert.hello.clickup.viewmodel.fixtures

import com.bkahlert.hello.clickup.model.Folder
import com.bkahlert.hello.clickup.model.Identifier
import com.bkahlert.hello.clickup.model.Space
import com.bkahlert.hello.clickup.model.Task
import com.bkahlert.hello.clickup.model.TaskList
import com.bkahlert.hello.clickup.model.Team
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.User
import com.bkahlert.hello.clickup.model.fixtures.ClickUpTestClient
import com.bkahlert.hello.clickup.model.fixtures.ClickUpTestClient.Companion.filterBy
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected.Data.CoreData
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected.Data.FullData
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelecting

public fun ClickUpTestClient.toTeamSelecting(
    user: User = initialUser,
    teams: List<Team> = initialTeams,
): TeamSelecting = TeamSelecting(
    client = this,
    user = user,
    teams = teams,
)

public fun ClickUpTestClient.toPartiallyLoaded(
    user: User = initialUser,
    teams: List<Team> = initialTeams,
    runningTimeEntry: TimeEntry? = initialRunningTimeEntry,
    tasks: List<Task> = initialTasks,
    spaces: List<Space> = initialSpaces,
    select: (Int, Identifier<*>) -> Boolean = { index, _ -> index == 0 },
): TeamSelected {
    val data = CoreData(runningTimeEntry, tasks, spaces)
    return TeamSelected(
        client = this,
        user = user,
        teams = teams,
        selectedTeam = teams.first(),
        selected = listOfNotNull(data.runningTimeEntry?.id, *data.tasks.map { it.id }.toTypedArray()).filterIndexed(select),
        data = data,
    )
}

public fun ClickUpTestClient.toFullyLoaded(
    user: User = initialUser,
    teams: List<Team> = initialTeams,
    runningTimeEntry: TimeEntry? = initialRunningTimeEntry,
    tasks: List<Task> = initialTasks,
    spaces: List<Space> = initialSpaces,
    lists: List<TaskList> = initialLists,
    folders: List<Folder> = initialFolders,
    select: (Int, Identifier<*>) -> Boolean = { index, _ -> index == 0 },
): TeamSelected {
    val data = FullData(
        runningTimeEntry = runningTimeEntry,
        tasks = tasks,
        spaces = spaces,
        folders = spaces.associateBy(Space::id) { folders.filterBy(it) },
        spaceLists = spaces.associateBy(Space::id) { lists.filterBy(it) },
        folderLists = folders.associateBy(Folder::id) { lists.filterBy(it) },
    )
    return TeamSelected(
        client = this,
        user = user,
        teams = teams,
        selectedTeam = teams.first(),
        selected = listOfNotNull(data.runningTimeEntry?.id, *data.tasks.map { it.id }.toTypedArray()).filterIndexed(select),
        data = data,
    )
}
