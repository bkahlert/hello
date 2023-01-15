package com.bkahlert.hello.clickup.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.hello.clickup.model.ClickUpClient
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.TaskID
import com.bkahlert.hello.clickup.model.TaskListID
import com.bkahlert.hello.clickup.model.Team
import com.bkahlert.hello.clickup.model.TeamID
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Failed
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Connected
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disconnected
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioning
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.clear
import com.bkahlert.kommons.logging.InlineLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

public interface ClickUpMenuViewModel {
    public val state: StateFlow<ClickUpMenuState>
    public fun enable(client: ClickUpClient? = null)
    public fun connect(client: ClickUpClient)
    public fun disconnect()
    public fun selectTeam(teamID: TeamID)
    public fun refresh(background: Boolean = false)
    public fun select(selection: Selection)
    public fun createTask(taskListID: TaskListID, name: String)
    public fun closeTask(taskID: TaskID)
    public fun startTimeEntry(taskID: TaskID?, tags: List<Tag>, billable: Boolean)
    public fun stopTimeEntry(timeEntry: TimeEntry, tags: List<Tag>)
}

/**
 * Returns a remembered [ClickUpMenuViewModel] with the
 * optionally specified [initialState], [refreshCoroutineScope], and [storage].
 */
@Composable
public fun rememberClickUpMenuViewModel(
    initialState: ClickUpMenuState = Disconnected,
    refreshCoroutineScope: CoroutineScope = rememberCoroutineScope(),
    storage: Storage = InMemoryStorage(),
): ClickUpMenuViewModel =
    remember(initialState, storage) {
        ClickUpMenuViewModelImpl(
            initialState,
            refreshCoroutineScope,
            storage
        )
    }


public class ClickUpMenuViewModelImpl(
    initialState: ClickUpMenuState = Disabled,
    private val coroutineScope: CoroutineScope,
    storage: Storage = InMemoryStorage(),
) : ClickUpMenuViewModel {
    private val logger by InlineLogging
    private var updateJob: Job? = null
    private val storage = ClickUpStorage(storage)

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<ClickUpMenuState> = _state.asStateFlow()

    /**
     * Updates the [state] using the specified [name] and the specified [operation]
     * optionally in the [background], that is, hiding any progress indication.
     */
    private fun update(name: String, background: Boolean = false, operation: suspend CoroutineScope.(Succeeded) -> ClickUpMenuState) {
        if (!background) {
            _state.update { currentState ->
                logger.debug("started $name in state ${currentState::class.simpleName}")
                Transitioning(currentState.lastSucceededState)
            }
        }
        if (!background || _state.value !is Failed) {
            updateJob = coroutineScope.launch {
                _state.update { currentState ->
                    logger.groupCatching(
                        label = "$name in state ${currentState::class.simpleName}",
                        render = { JSON.stringify(it, arrayOf("client", "avatar", "profilePicture")) }
                    ) {
                        operation(currentState.lastSucceededState)
                    }.getOrElse {
                        Failed(
                            operation = name,
                            cause = it,
                            previousState = currentState.lastSucceededState,
                            ignore = { update(name) { currentState } },
                            retry = { update(name) { operation(currentState.lastSucceededState) } }
                        )
                    }
                }
            }
        }
    }
//
//    init {
//        coroutineScope.launch {
//            delay(5.seconds)
//            while (isActive) {
//                while (updateJob?.isActive == true) delay(1.seconds)
//                refresh(background = true)
//                delay(15.seconds)
//            }
//        }
//    }

    override fun enable(client: ClickUpClient?) {
        if (_state.value is Disabled && client != null) {
            connect(client)
        } else {
            update("initializing") { state ->
                when (state) {
                    is Disabled -> {
                        Disconnected
                    }

                    else -> {
                        logger.warn("ClickUp is already $state; doing nothing.")
                        state
                    }
                }
            }
        }
    }

    override fun connect(client: ClickUpClient) {
        update("connecting") { state ->
            val teamSelecting = state.connect(client)
            if (teamSelecting.teams.size == 1) internalSelectTeam(teamSelecting, teamSelecting.teams.first())
            else teamSelecting
        }
    }

    override fun disconnect() {
        update("signing out") {
            storage.clear()
            Disconnected
        }
    }

    override fun selectTeam(teamID: TeamID) {
        update("selecting team $teamID") { state ->
            when (state) {
                is Connected -> { // TODO for all update calls, provide requireState function
                    val team = state.teams.first { it.id == teamID }
                    internalSelectTeam(state, team)
                }

                else -> state.also { console.warn("unexpected state $state") }
            }
        }
    }

    // TODO remove
    private suspend fun internalSelectTeam(
        state: Connected,
        team: Team,
    ): Connected {
        val previousSelection = storage.selections[team]
        logger.debug("Previous selection for team ${team.id}: $previousSelection")
        val runningTimeEntry = kotlin.runCatching { state.getRunningTimeEntry(team) }
        val runningTimeEntryId = runningTimeEntry.map { it?.id }
        logger.debug("Running time entry: $runningTimeEntryId")
        val selectedActivityIds = buildList {
            runningTimeEntryId.onSuccess { it?.also(::add) }
            addAll(previousSelection)
        }
        logger.debug("Selecting: $selectedActivityIds")
        return state.select(team, selectedActivityIds)
    }

    override fun refresh(background: Boolean) {
        update("refreshing", background) { state ->
            when (state) {
                is Connected -> {
                    storage.cache.clear()
                    state.refresh()
                }

                else -> state
            }
        }
    }

    override fun select(selection: Selection) {
        update("selecting $selection") { state ->
            when (state) {
                is TeamSelected -> {
                    storage.selections[state.selectedTeam] = selection
                    state.select(selection)
                }

                else -> state.also { console.warn("unexpected state $state") }
            }
        }
    }

    override fun createTask(taskListID: TaskListID, name: String) {
        update("creating $name in $taskListID") { state ->
            when (state) {
                is TeamSelected -> {
                    state.createTask(taskListID, name)
                }

                else -> state.also { console.warn("unexpected state $state") }
            }
        }
    }

    override fun closeTask(taskID: TaskID) {
        update("closing $taskID") { state ->
            when (state) {
                is TeamSelected -> {
                    state.closeTask(taskID)
                }

                else -> state.also { console.warn("unexpected state $state") }
            }
        }
    }

    override fun startTimeEntry(
        taskID: TaskID?,
        tags: List<Tag>,
        billable: Boolean,
    ) {
        update("starting time entry") { state ->
            when (state) {
                is TeamSelected -> state.startTimeEntry(taskID, null, billable, tags)
                else -> state.also { console.warn("unexpected state $state") }
            }
        }
    }

    override fun stopTimeEntry(timeEntry: TimeEntry, tags: List<Tag>) {
        update("stopping time entry") { state ->
            when (state) {
                is TeamSelected -> state.stopTimeEntry(timeEntry, tags)
                else -> state.also { console.warn("unexpected state $state") }
            }
        }
    }

    override fun toString(): String {
        return "ClickUpModel(logger=$logger, storage=$storage, state=$state)"
    }
}
