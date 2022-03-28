package com.bkahlert.hello.plugins.clickup

import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.plugins.clickup.ClickUpState.Connected
import com.bkahlert.hello.plugins.clickup.ClickUpState.Connected.TeamSelected
import com.bkahlert.hello.plugins.clickup.ClickUpState.Connected.TeamSelecting
import com.bkahlert.hello.plugins.clickup.ClickUpState.Failed
import com.bkahlert.kommons.coroutines.flow.FlowUpdate
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.applyUpdates
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.extend
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.clear
import com.bkahlert.kommons.fix.combine
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.TaskID
import com.clickup.api.TeamID
import com.clickup.api.TimeEntry
import com.clickup.api.rest.AccessToken
import com.clickup.api.rest.ClickupClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.measureTime

sealed class InternalState(
    open val state: ClickUpState,
) {
    object Paused : InternalState(ClickUpState.Paused)
    object Disconnected : InternalState(ClickUpState.Disconnected)

    data class Connected(
        val client: ClickupClient,
        override val state: ClickUpState.Connected,
    ) : InternalState(state)

    data class Failed(
        override val state: ClickUpState.Failed,
    ) : InternalState(state)
}

class ClickUpModel(storage: Storage = InMemoryStorage()) {
    private val logger = simpleLogger()
    private val storage = ClickUpStorage(storage)

    private val internalState = MutableStateFlow<InternalState>(InternalState.Paused)
    private val internalStateUpdate = FlowUpdate<InternalState>()
    val menuState: Flow<ClickUpState> = internalStateUpdate
        .applyUpdates(internalState)
        .map { it.state }

    private fun update(name: String, operation: suspend (InternalState) -> InternalState) {
        internalStateUpdate.extend {
            logger.debug("INTERNAL STATE is $name\n- STATE: ${it.state}")
            val newInternalState: InternalState
            val duration = measureTime { newInternalState = operation(it) }
            logger.debug("INTERNAL STATE finished $name within $duration\n- NEW STATE: ${newInternalState.state}")
            newInternalState
        }
    }

    fun unpause() {
        val token = storage.`access-token`
        if (internalState.value is InternalState.Paused && token != null) {
            connect(token)
        } else {
            update("un-pausing") { internal ->
                when (internal) {
                    is InternalState.Paused -> {
                        InternalState.Disconnected
                    }
                    else -> {
                        logger.warn("ClickUp is already ${internal.state}; doing nothing.")
                        internal
                    }
                }
            }
        }
    }

    fun connect(accessToken: AccessToken) {
        update("connecting") {
            val client = ClickupClient(accessToken, this.storage.cache)
            val userResult = client.getUser()
            val teamsResult = client.getTeams()
            combine(userResult, teamsResult, Connected::TeamSelecting)
                .map { InternalState.Connected(client, it) }
                .getOrElse { InternalState.Failed(Failed(client.accessToken, it)) }
        }
    }

    fun signOut() {
        update("signing out") {
            storage.clear()
            InternalState.Disconnected
        }
    }

    fun selectTeam(teamID: TeamID) {
        update("activating $teamID") { internal ->
            if (internal is InternalState.Connected) { // TODO for all update calls, provide requireState function
                val (client, state) = internal
                val team = state.teams.first { it.id == teamID }
                val previousSelection = storage.selections[team]
                logger.debug("Previous selection for team ${team.id}: $previousSelection")
                val runningTimeEntry = client.getRunningTimeEntry(team, state.user)
                val runningTimeEntryId = runningTimeEntry.map { it?.id }
                logger.debug("Running time entry: $runningTimeEntryId")
                val selectedActivityIds = buildList {
                    runningTimeEntryId.onSuccess { it?.also(::add) }
                    addAll(previousSelection)
                }
                logger.debug("Selecting: $selectedActivityIds")
                InternalState.Connected(client, TeamSelected(state.user, state.teams, team, selectedActivityIds, runningTimeEntry))
            } else {
                internal.also { logger.error("Failed to activate; unexpected state ${internal.state}") }
            }
        }
    }

    fun refresh(force: Boolean = false) {
        update("refreshing") { internal ->
            if (force) storage.cache.clear()
            when (internal) {
                is InternalState.Connected -> {
                    val (client, state) = internal
                    when (state) {
                        is TeamSelecting -> {
                            val userResult = client.getUser()
                            val teamsResult = client.getTeams()
                            combine(userResult, teamsResult, Connected::TeamSelecting)
                                .map { InternalState.Connected(client, it) }
                                .getOrElse { InternalState.Failed(Failed(client.accessToken, it)) }
                        }
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
                            val refreshedState = state.copy(
                                tasks = tasks,
                                spaces = spaces,
                                folders = folders,
                                spaceLists = spaceLists,
                                folderLists = folderLists
                            )
                            InternalState.Connected(client, refreshedState)
                        }
                    }
                }
                else -> {
                    internal.also { logger.error("Failed to refresh; unexpected state ${internal.state}") }
                }
            }
        }
    }

    fun select(selection: Selection) {
        update("selecting $selection") { internal ->
            when (internal) {
                is InternalState.Connected -> {
                    val (client, state) = internal
                    when (state) {
                        is TeamSelecting -> {
                            internal.also { logger.error("Failed to select $selection; unexpected state ${internal.state}") }
                        }
                        is TeamSelected -> {
                            logger.debug("Storing new selection $selection team ${state.selectedTeam.id}")
                            storage.selections[state.selectedTeam] = selection
                            InternalState.Connected(client, state.copy(selected = selection))
                        }
                    }
                }
                else -> {
                    internal.also { logger.error("Failed to select $selection; unexpected state ${internal.state}") }
                }
            }
        }
    }

    fun startTimeEntry(
        taskID: TaskID,
        tags: List<Tag>,
        billable: Boolean,
    ) {
        update("starting time entry") { internal ->
            when (internal) {
                is InternalState.Connected -> {
                    val (client, state) = internal
                    when (state) {
                        is TeamSelecting -> {
                            internal.also { logger.error("Failed to start time entry; unexpected state ${internal.state}") }
                        }
                        is TeamSelected -> {
                            val runningTimeEntry = client.startTimeEntry(state.selectedTeam, taskID, null, billable, *tags.toTypedArray())
                            val updatedState = state.copy(runningTimeEntry = runningTimeEntry)
                            InternalState.Connected(client, updatedState)
                        }
                    }
                }
                else -> {
                    internal.also { logger.error("Failed to start time entry; unexpected state ${internal.state}") }
                }
            }
        }
    }

    fun stopTimeEntry(tags: List<Tag>) {
        update("stopping time entry") { internal ->
            when (internal) {
                is InternalState.Connected -> {
                    val (client, state) = internal
                    when (state) {
                        is TeamSelecting -> {
                            internal.also { logger.error("Failed to stop time entry; unexpected state ${internal.state}") }
                        }
                        is TeamSelected -> {
                            val updatedState = client.stopTimeEntry(state.selectedTeam).fold({ stoppedTimeEntry ->
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
                            InternalState.Connected(client, updatedState)
                        }
                    }
                }
                else -> {
                    internal.also { logger.error("Failed to stop time entry; unexpected state ${internal.state}") }
                }
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
        return "ClickUpModel(logger=$logger, storage=$storage, internalState=$internalState, internalStateUpdate=$internalStateUpdate, menuState=$menuState)"
    }
}
