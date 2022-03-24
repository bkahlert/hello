package com.bkahlert.hello.plugins.clickup

import com.bkahlert.hello.ClickupConfig
import com.bkahlert.hello.Failure
import com.bkahlert.hello.Response
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.Success
import com.bkahlert.hello.plugins.clickup.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Initializing
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.TeamSelected
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.TeamSelecting
import com.bkahlert.hello.ui.errorMessage
import com.bkahlert.kommons.Either.Left
import com.bkahlert.kommons.Either.Right
import com.bkahlert.kommons.coroutines.flow.FlowUpdate
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.applyUpdates
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.extend
import com.bkahlert.kommons.coroutines.flow.toStringAndHash
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.fix.map
import com.bkahlert.kommons.fix.or
import com.bkahlert.kommons.fix.orNull
import com.bkahlert.kommons.fix.value
import com.clickup.api.Folder
import com.clickup.api.FolderID
import com.clickup.api.Space
import com.clickup.api.SpaceID
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.TaskID
import com.clickup.api.TaskList
import com.clickup.api.Team
import com.clickup.api.TeamID
import com.clickup.api.TimeEntry
import com.clickup.api.User
import com.clickup.api.rest.AccessToken
import com.clickup.api.rest.ClickupClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ClickupModel(
    private val config: ClickupConfig,
    storage: Storage,
) {
    private val clickup = ClickupStorage(storage)

    private val logger = simpleLogger()

    private val clickupToken = MutableStateFlow(clickup.`access-token`)

    fun configureClickUp(accessToken: AccessToken) {
        logger.debug("setting access token")
        clickupToken.update { accessToken }
    }

    // save access token in locally
    // if access token is missing use fallback if set
    // result is saved
    private val savedClickupToken = clickupToken
        .onEach { it?.also { clickup.`access-token` = it } }
        .map { it ?: config.fallbackAccessToken }

    private val clickupClient = savedClickupToken
        .map { token -> token?.let(::ClickupClient) }

    private val loadedMenuState: Flow<Pair<ClickupClient, ClickupMenuState>?> = clickupClient
        .map { client ->
            if (client == null) null
            else client to ClickupMenuState.of(client.getUser(), client.getTeams())
        }

    private val menuStateUpdate = FlowUpdate<Pair<ClickupClient, ClickupMenuState>?>()
    val menuState = menuStateUpdate.applyUpdates(loadedMenuState).map { it?.second ?: Initializing }

    private fun update(operation: suspend (ClickupClient, ClickupMenuState) -> ClickupMenuState) {
        menuStateUpdate.extend { clientAndState ->
            clientAndState?.let { (client, state) -> client to operation(client, state) }
        }
    }

    fun selectTeam(teamID: TeamID) {
        logger.info("Activating $teamID")
        update { client, state ->
            if (state is ClickupMenuState.Loaded) { // TODO for all update calls, provide requireState function
                val team = state.teams.first { it.id == teamID }
                val previousSelection = clickup.selections[team]
                logger.debug("Previous selection for team ${team.id}: $previousSelection")
                val runningTimeEntry = client.getRunningTimeEntry(team, state.user)
                val runningTimeEntryId = runningTimeEntry.map { it?.id }.orNull()
                logger.debug("Running time entry: $runningTimeEntryId")
                val selectedActivityIds = buildList {
                    runningTimeEntryId?.also { add(it) }
                    addAll(previousSelection)
                }
                logger.debug("Selecting: $selectedActivityIds")
                TeamSelected(state.user, state.teams, team, selectedActivityIds, runningTimeEntry)
            } else state.also { logger.error("Failed to activate; unexpected state $state") }
        }
    }

    fun refresh(force: Boolean = false) {
        logger.info("Refreshing ${menuState.toStringAndHash()}")
        update { client, state ->
            if (force) {
                client.clearCache()
            }
            when (state) {
                is TeamSelected -> {
                    val tasks = client.getTasks(state.selectedTeam)
                    val spaces = client.getSpaces(state.selectedTeam)
                    val folders = spaces.map { it.associate { space -> space.id to client.getFolders(space) } } or { emptyMap() }
                    val spaceLists = spaces.map { it.associate { space -> space.id to client.getLists(space) } } or { emptyMap() }
                    val folderLists = buildMap {
                        folders.values.forEach { folderRequests ->
                            putAll(folderRequests.map { folderList ->
                                folderList.associate { folder ->
                                    folder.id to client.getLists(folder)
                                }
                            } or { emptyMap() })
                        }
                    }
                    state.copy(tasks = tasks, spaces = spaces, folders = folders, spaceLists = spaceLists, folderLists = folderLists).also {
                        logger.info("Refreshed ${it.toStringAndHash()}")
                    }
                }
                else -> state.also { logger.error("Failed to refresh; unexpected state $state") }
            }
        }
    }

    fun select(selection: Selection) {
        update { _, state ->
            logger.debug("Selecting $selection")
            when (state) {
                is TeamSelected -> {
                    logger.debug("Storing new selection $selection team ${state.selectedTeam.id}")
                    clickup.selections[state.selectedTeam] = selection
                    state.copy(selected = selection)
                }
                else -> state.also { logger.error("Failed to select activity; unexpected state $state") }
            }
        }
    }

    fun startTimeEntry(
        taskID: TaskID,
        tags: List<Tag>,
        billable: Boolean,
    ) {
        update { client, state ->
            logger.log("starting time entry")
            when (state) {
                is TeamSelected -> state.copy(runningTimeEntry = client.startTimeEntry(state.selectedTeam, taskID, null, billable, *tags.toTypedArray()))
                else -> state.also { logger.error("Failed to start time entry; unexpected state $state") }
            }
        }
    }

    fun abortTimeEntry(
        tags: List<Tag>,
    ) {
        update { client, state ->
            logger.info("aborting time entry")
            when (state) {
                is TeamSelected -> {
                    when (val stopResponse = client.stopTimeEntry(state.selectedTeam)) {
                        is Success -> {
                            console.log("stopped", stopResponse.value)
                            when (client.addTagsToTimeEntries(state.selectedTeam, listOf(stopResponse.value.id), tags).also {
                                console.log("updated", it)
                            }) {
                                is Success -> {
                                    logger.info("added tags $tags to time entry ${stopResponse.value.id}")
                                    val taskId = stopResponse.value.task?.id
                                    val updatedTasks: Response<List<Task>> = when (val tasks = state.tasks) {
                                        is Success -> {
                                            if (taskId != null) {
                                                when (val updatedTaskResponse = client.getTask(taskId)) {
                                                    is Success -> {
                                                        val updatedTask = updatedTaskResponse.value
                                                        logger.info("got updated task $updatedTask")
                                                        if (updatedTask != null) {
                                                            val left = tasks.value.map { task ->
                                                                task.takeUnless { it.id == updatedTask.id } ?: updatedTask
                                                            }
                                                            val success: Left<List<Task>, Throwable> = Success<List<Task>, Throwable>(left)
                                                            success
                                                        } else {
                                                            client.getTasks(state.selectedTeam)
                                                        }
                                                    }
                                                    is Failure -> {
                                                        logger.error("failed to get updated task", updatedTaskResponse.value)
                                                        val failure: Right<List<Task>, Throwable> = Failure(updatedTaskResponse.value)
                                                        failure
                                                    }
                                                }
                                            } else {
                                                logger.warn("time entry ${stopResponse.value} has no associated task")
                                                val tasks1: Response<List<Task>> = client.getTasks(state.selectedTeam)
                                                tasks1
                                            }
                                        }
                                        else -> {
                                            logger.info("getting tasks as none were successfully loaded yet")
                                            client.getTasks(state.selectedTeam)
                                        }
                                    }
                                    state.copy(
                                        runningTimeEntry = Success(null),
                                        tasks = updatedTasks
                                    )
                                }
                                is Failure -> {
                                    logger.error("failed to add tags $tags to time entry ${stopResponse.value.id}")
                                    state.copy(runningTimeEntry = stopResponse)
                                }
                            }
                        }
                        is Failure -> {
                            console.error("failed to stop time entry", stopResponse.value.errorMessage)
                            state.copy(runningTimeEntry = stopResponse)
                        }
                    }
                }
                else -> state.also { logger.error("Failed to stop time entry; unexpected state $state") }
            }
        }
    }

    fun completeTimeEntry(
        tags: List<Tag>,
    ) {
        update { client, state ->
            logger.info("completing time entry")
            when (state) {
                is TeamSelected -> {
                    // TODO
                    when (val response = client.stopTimeEntry(state.selectedTeam)) {
                        is Success -> {
                            console.log("stopped", response.value)
                            state.copy(runningTimeEntry = response)
                        }
                        is Failure -> {
                            console.error("failed to stop", response.value.errorMessage)
                            state.copy(runningTimeEntry = response)
                        }
                    }
                }
                else -> state.also { logger.error("Failed to stop time entry; unexpected state $state") }
            }
        }
    }

    override fun toString(): String {
        return "ClickupModel(config=$config, logger=$logger, clickupToken=$clickupToken, savedClickupToken=$savedClickupToken, clickupClient=$clickupClient, loadedMenuState=$loadedMenuState, menuStateUpdate=$menuStateUpdate, menuState=$menuState)"
    }
}

sealed interface ClickupMenuState {
    object Initializing : ClickupMenuState

    object Disconnected : ClickupMenuState

    object Loading : ClickupMenuState

    sealed class Loaded(
        open val user: User,
        open val teams: List<Team>,
    ) : ClickupMenuState {

        protected val logger = simpleLogger()

        data class TeamSelecting(
            override val user: User,
            override val teams: List<Team>,
        ) : Loaded(user, teams)

        data class TeamSelected(
            override val user: User,
            override val teams: List<Team>,
            val selectedTeam: Team,
            val selected: Selection,
            val runningTimeEntry: Response<TimeEntry?>?,
            val tasks: Response<List<Task>>? = null,
            val spaces: Response<List<Space>>? = null,
            val folders: Map<SpaceID, Response<List<Folder>>> = emptyMap(),
            val spaceLists: Map<SpaceID, Response<List<TaskList>>> = emptyMap(),
            val folderLists: Map<FolderID, Response<List<TaskList>>> = emptyMap(),
        ) : Loaded(user, teams) {

            val runningActivity: RunningTaskActivity? by lazy {
                val runningTimeEntry: TimeEntry? = runningTimeEntry?.orNull()
                val tasks: List<Task> = tasks?.orNull() ?: emptyList()
                runningTimeEntry?.let { timeEntry ->
                    val task = timeEntry.task?.run { tasks.firstOrNull { it.id == id } }
                    RunningTaskActivity(
                        timeEntry = timeEntry,
                        selected = listOfNotNull(timeEntry.id, task?.id).any { selected.contains(it) },
                        task = task
                    )
                }
            }

            val activityGroups: List<ActivityGroup> by lazy {

                buildList {
                    runningActivity?.also { add(ActivityGroup.of(it)) }

                    val tasks: List<Task> = tasks?.orNull() ?: emptyList()
                    val spaces = spaces?.orNull() ?: emptyList()

                    tasks.groupBy { it.space.id }
                        .forEach { (spaceID, spaceTasks) ->
                            val space = spaces.firstOrNull { it.id == spaceID }
                            spaceTasks.groupBy { it.folder }
                                .forEach { (folderPreview, folderTasks) ->
                                    folderTasks.groupBy { it.list }
                                        .forEach { (listPreview, listTasks) ->
                                            val lists = when {
                                                folderPreview.hidden -> spaceLists[spaceID]
                                                else -> folderLists[folderPreview.id]
                                            }?.orNull() ?: emptyList()

                                            add(lists.firstOrNull { it.id == listPreview?.id }
                                                ?.let { ActivityGroup.of(space, folderPreview, it, it.status?.color, listTasks, this@TeamSelected.selected) }
                                                ?: ActivityGroup.of(space, folderPreview, listPreview, null, listTasks, this@TeamSelected.selected))
                                        }
                                }
                        }
                }
            }
        }
    }

    data class Failed(
        val exceptions: List<Throwable>,
    ) : ClickupMenuState {
        constructor(vararg exceptions: Throwable) : this(exceptions.toList())

        val message: String
            get() = exceptions.firstNotNullOfOrNull { it.message } ?: "message missing"
    }

    companion object {
        fun of(
            userResponse: Response<User>,
            teamsResponse: Response<List<Team>>,
        ): ClickupMenuState =
            userResponse.map { user ->
                teamsResponse.map { TeamSelecting(user, it) } or { Failed(it) }
            } or { ex ->
                userResponse.map { Failed(ex) } or { Failed(ex, it) }
            }
    }
}
