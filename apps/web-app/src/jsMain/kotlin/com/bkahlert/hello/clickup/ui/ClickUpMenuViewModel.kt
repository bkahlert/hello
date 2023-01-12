package com.bkahlert.hello.clickup.ui

import AccessTokenBasedClickUpClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.clickup.api.Tag
import com.bkahlert.hello.clickup.api.TaskID
import com.bkahlert.hello.clickup.api.TaskListID
import com.bkahlert.hello.clickup.api.Team
import com.bkahlert.hello.clickup.api.TeamID
import com.bkahlert.hello.clickup.api.TimeEntry
import com.bkahlert.hello.clickup.api.rest.AccessToken
import com.bkahlert.hello.clickup.api.rest.ClickUpClient
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Failed
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Connected
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Disconnected
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioning
import com.bkahlert.hello.groupCatching
import com.bkahlert.kommons.debug.CustomToString.Ignore
import com.bkahlert.kommons.debug.render
import com.bkahlert.kommons.debug.renderType
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.clear
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface ClickUpMenuViewModel {
    val state: StateFlow<ClickUpMenuState>
    fun enable()
    fun connect(accessToken: AccessToken)
    fun disconnect()
    fun selectTeam(teamID: TeamID)
    fun refresh(background: Boolean = false)
    fun select(selection: Selection)
    fun createTask(taskListID: TaskListID, name: String)
    fun closeTask(taskID: TaskID)
    fun startTimeEntry(taskID: TaskID?, tags: List<Tag>, billable: Boolean)
    fun stopTimeEntry(timeEntry: TimeEntry, tags: List<Tag>)
}

/**
 * Returns a remembered [ClickUpMenuViewModel] with the
 * optionally specified [initialState], [dispatcher], [refreshCoroutineScope], [storage] and [createClient].
 */
@Composable
fun rememberClickUpMenuViewModel(
    initialState: ClickUpMenuState = Disconnected,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    refreshCoroutineScope: CoroutineScope = rememberCoroutineScope(),
    storage: Storage = InMemoryStorage(),
    createClient: (AccessToken, Storage, CoroutineDispatcher) -> ClickUpClient = ::AccessTokenBasedClickUpClient,
): ClickUpMenuViewModel =
    remember(createClient, dispatcher, initialState, storage) {
        ClickUpMenuViewModelImpl(
            initialState,
            dispatcher,
            refreshCoroutineScope,
            storage,
            createClient
        )
    }


class ClickUpMenuViewModelImpl(
    initialState: ClickUpMenuState = Disabled,
    private val dispatcher: CoroutineDispatcher,
    private val coroutineScope: CoroutineScope,
    storage: Storage = InMemoryStorage(),
    private val createClient: (AccessToken, Storage, CoroutineDispatcher) -> ClickUpClient,
) : ClickUpMenuViewModel {
    private val logger = simpleLogger()
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
                logger.debug("started $name in state ${currentState.renderType()}")
                Transitioning(currentState.lastSucceededState)
            }
        }
        if (!background || _state.value !is Failed) {
            updateJob = coroutineScope.launch {
                _state.update { currentState ->
                    logger.groupCatching(
                        label = "$name in state ${currentState.renderType()}",
                        render = {
                            it.render {
                                customToString = Ignore
                                filterProperties { _, prop ->
                                    prop != "client" && prop != "avatar" && prop != "profilePicture"
                                }
                            }
                        }
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

    override fun enable() {
        val token = storage.accessToken
        if (_state.value is Disabled && token != null) {
            connect(token)
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

    override fun connect(accessToken: AccessToken) {
        update("connecting") { state ->
            val client = createClient(accessToken, storage.cache, dispatcher)
            val teamSelecting = state.connect(client)
            storage.accessToken = accessToken
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
