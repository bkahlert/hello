package com.bkahlert.hello.plugins.clickup

import com.bkahlert.hello.ClickupConfig
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.plugins.clickup.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Failed
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Initializing
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.TeamSelected
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.TeamSelecting
import com.bkahlert.kommons.coroutines.flow.FlowUpdate
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.applyUpdates
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.extend
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.fix.combine
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
import kotlin.time.measureTime

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
            else client to combine(client.getUser(), client.getTeams(), ::TeamSelecting).getOrElse { Failed(it) }
        }

    private val menuStateUpdate = FlowUpdate<Pair<ClickupClient, ClickupMenuState>?>()
    val menuState = menuStateUpdate.applyUpdates(loadedMenuState).map { it?.second ?: Initializing }

    private fun update(name: String, operation: suspend (ClickupClient, ClickupMenuState) -> ClickupMenuState) {
        menuStateUpdate.extend { clientAndState ->
            clientAndState?.let { (client, state) ->
                logger.debug("ClickUp menu is $name\nSTATE:\n$state")
                val newState: ClickupMenuState
                val duration = measureTime { newState = operation(client, state) }
                logger.debug("ClickUp menu finished $name within $duration\nNEW STATE:\n$newState")
                client to newState
            }
        }
    }

    fun selectTeam(teamID: TeamID) {
        update("activating $teamID") { client, state ->
            if (state is ClickupMenuState.Loaded) { // TODO for all update calls, provide requireState function
                val team = state.teams.first { it.id == teamID }
                val previousSelection = clickup.selections[team]
                logger.debug("Previous selection for team ${team.id}: $previousSelection")
                val runningTimeEntry = client.getRunningTimeEntry(team, state.user)
                val runningTimeEntryId = runningTimeEntry.map { it?.id }
                logger.debug("Running time entry: $runningTimeEntryId")
                val selectedActivityIds = buildList {
                    runningTimeEntryId.onSuccess { it?.also(::add) }
                    addAll(previousSelection)
                }
                logger.debug("Selecting: $selectedActivityIds")
                TeamSelected(state.user, state.teams, team, selectedActivityIds, runningTimeEntry)
            } else state.also { logger.error("Failed to activate; unexpected state $state") }
        }
    }

    fun refresh(force: Boolean = false) {
        update("refreshing") { client, state ->
            if (force) {
                client.clearCache()
            }
            when (state) {
                is TeamSelected -> {
                    val tasks = client.getTasks(state.selectedTeam)
                    val spaces = client.getSpaces(state.selectedTeam)
                    val folders = spaces.map { it.associate { space -> space.id to client.getFolders(space) } }.getOrDefault(emptyMap())
                    val spaceLists = spaces.map { it.associate { space -> space.id to client.getLists(space) } }.getOrDefault(emptyMap())
                    val folderLists = buildMap {
                        folders.values.forEach { folderRequests ->
                            putAll(folderRequests.map { folderList ->
                                folderList.associate { folder ->
                                    folder.id to client.getLists(folder)
                                }
                            }.getOrDefault(emptyMap()))
                        }
                    }
                    state.copy(tasks = tasks, spaces = spaces, folders = folders, spaceLists = spaceLists, folderLists = folderLists)
                }
                else -> state.also { logger.error("Failed to refresh; unexpected state $state") }
            }
        }
    }

    fun select(selection: Selection) {
        update("selecting $selection") { _, state ->
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
        update("starting time entry") { client, state ->
            when (state) {
                is TeamSelected -> state.copy(runningTimeEntry = client.startTimeEntry(state.selectedTeam, taskID, null, billable, *tags.toTypedArray()))
                else -> state.also { logger.error("Failed to start time entry; unexpected state $state") }
            }
        }
    }

    fun stopTimeEntry(tags: List<Tag>) {
        update("stopping time entry") { client, state ->
            when (state) {
                is TeamSelected -> {
                    client.stopTimeEntry(state.selectedTeam).fold({ stoppedTimeEntry ->
                        logger.debug("stopped $stoppedTimeEntry")
                        client.addTagsToTimeEntries(state.selectedTeam, listOf(stoppedTimeEntry.id), tags).fold(
                            { logger.debug("added tags $tags to time entry ${stoppedTimeEntry.id}") },
                            { logger.error("failed to add tags $tags to time entry ${stoppedTimeEntry.id}") },
                        )
                        val taskId = stoppedTimeEntry.task?.id
                        state.copy(
                            runningTimeEntry = Result.success(null),
                            tasks = updateTasks(state, taskId, client, stoppedTimeEntry),
                        )
                    }, {
                        logger.error("failed to stop time entry", it)
                        state.copy(runningTimeEntry = Result.failure(it))
                    })
                }
                else -> state.also { logger.error("Failed to stop time entry; unexpected state $state") }
            }
        }
    }

    private suspend fun updateTasks(
        state: TeamSelected,
        taskId: TaskID?,
        client: ClickupClient,
        stopResponse: TimeEntry,
    ): Result<List<Task>> {
        return state.tasks?.getOrNull()?.let { cachedTasks ->
            if (taskId == null) {
                logger.warn("time entry $stopResponse has no associated task")
                client.getTasks(state.selectedTeam)
            } else {
                client.getTask(taskId).map { updatedTask ->
                    checkNotNull(updatedTask) { logger.error("just updated task can no more be found") }
                    logger.info("got updated task $updatedTask")
                    cachedTasks.map { task -> task.takeUnless { it.id == updatedTask.id } ?: updatedTask }
                }.recover {
                    logger.error("failed to get updated task, downloading all tasks", it)
                    client.getTasks(state.selectedTeam).getOrThrow()
                }
            }
        } ?: run {
            logger.warn("surprisingly no local tasks found, downloading")
            client.getTasks(state.selectedTeam)
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
            val runningTimeEntry: Result<TimeEntry?>?,
            val tasks: Result<List<Task>>? = null,
            val spaces: Result<List<Space>>? = null,
            val folders: Map<SpaceID, Result<List<Folder>>> = emptyMap(),
            val spaceLists: Map<SpaceID, Result<List<TaskList>>> = emptyMap(),
            val folderLists: Map<FolderID, Result<List<TaskList>>> = emptyMap(),
        ) : Loaded(user, teams) {

            val runningActivity: Result<RunningTaskActivity?>? by lazy {
                runningTimeEntry?.map { timeEntry ->
                    if (timeEntry != null) {
                        val task = timeEntry.task?.let { timeEntryTask ->
                            tasks?.map { it.firstOrNull { task -> task.id == timeEntryTask.id } }?.getOrNull()
                        }
                        RunningTaskActivity(
                            timeEntry = timeEntry,
                            selected = listOfNotNull(timeEntry.id, task?.id).any { selected.contains(it) },
                            task = task
                        )
                    } else null
                }
            }

            val activityGroups: Result<List<ActivityGroup>>? by lazy {

                runningActivity.let { runningActivityResult ->
                    if (runningActivityResult == null || tasks == null || spaces == null) return@lazy null
                    runningActivityResult.mapCatching { runningActivity ->
                        buildList {
                            measureTime {
                                if (runningActivity != null) add(ActivityGroup.of(runningActivity))

                                combine(tasks, spaces) { tasks, spaces ->
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
                                                            }?.getOrThrow() ?: emptyList()

                                                            val group = lists.firstOrNull { it.id == listPreview?.id }?.let { taskList ->
                                                                ActivityGroup.of(
                                                                    space = space,
                                                                    folder = folderPreview,
                                                                    list = taskList,
                                                                    color = taskList.status?.color,
                                                                    tasks = listTasks,
                                                                    selected = this@TeamSelected.selected,
                                                                )
                                                            } ?: ActivityGroup.of(
                                                                space = space,
                                                                folder = folderPreview,
                                                                listPreview = listPreview,
                                                                color = null,
                                                                tasks = listTasks,
                                                                selected = this@TeamSelected.selected,
                                                            )
                                                            add(group)
                                                        }
                                                }
                                        }
                                }
                            }.also { logger.debug("Preparation of activity groups took $it") }
                        }
                    }
                }
            }
        }
    }

    data class Failed(
        val exception: Throwable,
    ) : ClickupMenuState {
        val message: String = exception.message ?: "message missing"
    }
}
