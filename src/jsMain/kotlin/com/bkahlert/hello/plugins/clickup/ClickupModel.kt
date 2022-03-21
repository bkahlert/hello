package com.bkahlert.hello.plugins.clickup

import com.bkahlert.Brand
import com.bkahlert.hello.ClickupConfig
import com.bkahlert.hello.Failure
import com.bkahlert.hello.Response
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.Success
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Initializing
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activated
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activated.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activated.Activity.TaskActivity
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activated.ActivityGroup.Meta
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activating
import com.bkahlert.hello.ui.errorMessage
import com.bkahlert.kommons.Color
import com.bkahlert.kommons.Either.Left
import com.bkahlert.kommons.Either.Right
import com.bkahlert.kommons.coroutines.flow.FlowUpdate
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.applyUpdates
import com.bkahlert.kommons.coroutines.flow.FlowUpdate.Companion.extend
import com.bkahlert.kommons.coroutines.flow.toStringAndHash
import com.bkahlert.kommons.fix.map
import com.bkahlert.kommons.fix.or
import com.bkahlert.kommons.fix.orNull
import com.bkahlert.kommons.fix.value
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.compareTo
import com.bkahlert.kommons.time.toMoment
import com.clickup.api.ClickupList
import com.clickup.api.Folder
import com.clickup.api.Space
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.User
import com.clickup.api.rest.AccessToken
import com.clickup.api.rest.ClickupClient
import com.clickup.api.rest.Identifier
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ClickupModel(
    private val config: ClickupConfig,
) {

    private val logger = simpleLogger()

    private val clickupToken = MutableStateFlow(AccessToken.load())

    fun configureClickUp(accessToken: AccessToken) {
        logger.debug("setting access token")
        clickupToken.update { accessToken }
    }

    // save access token in locally
    // if access token is missing use fallback if set
    // result is saved
    private val savedClickupToken = clickupToken
        .onEach { it?.save() }
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

    fun activate(teamID: Team.ID) {
        logger.info("Activating $teamID")
        update { client, state ->
            if (state is ClickupMenuState.Loaded) {
                val team = state.teams.first { it.id == teamID }
                Activated(state.user, state.teams, team, client.getRunningTimeEntry(team, state.user))
            } else {
                state
            }
        }
    }

    fun refresh() {
        logger.info("Refreshing ${menuState.toStringAndHash()}")
        update { client, state ->
            when (state) {
                is Activated -> {
                    val tasks = client.getTasks(state.activeTeam)
                    val spaces = client.getSpaces(state.activeTeam)
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
                else -> state
            }
        }
    }

    fun startTimeEntry(
        taskID: Task.ID,
        tags: List<Tag>,
        billable: Boolean,
    ) {
        update { client, state ->
            logger.log("starting time entry")
            when (state) {
                is Activated -> state.copy(runningTimeEntry = client.startTimeEntry(state.activeTeam, taskID, null, billable, *tags.toTypedArray()))
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
                is Activated -> {
                    when (val stopResponse = client.stopTimeEntry(state.activeTeam)) {
                        is Success -> {
                            console.log("stopped", stopResponse.value)
                            when (client.addTagsToTimeEntries(state.activeTeam, listOf(stopResponse.value.id), tags).also {
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
                                                            client.getTasks(state.activeTeam)
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
                                                val tasks1: Response<List<Task>> = client.getTasks(state.activeTeam)
                                                tasks1
                                            }
                                        }
                                        else -> {
                                            logger.info("getting tasks as none were successfully loaded yet")
                                            client.getTasks(state.activeTeam)
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
                is Activated -> {
                    // TODO
                    when (val response = client.stopTimeEntry(state.activeTeam)) {
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

        data class Activating(
            override val user: User,
            override val teams: List<Team>,
        ) : Loaded(user, teams)

        data class Activated(
            override val user: User,
            override val teams: List<Team>,
            val activeTeam: Team,
            val runningTimeEntry: Response<TimeEntry?>?,
            val tasks: Response<List<Task>>? = null,
            val spaces: Response<List<Space>>? = null,
            val folders: Map<Space.ID, Response<List<Folder>>> = emptyMap(),
            val spaceLists: Map<Space.ID, Response<List<ClickupList>>> = emptyMap(),
            val folderLists: Map<Folder.ID, Response<List<ClickupList>>> = emptyMap(),
        ) : Loaded(user, teams) {

            data class ActivityGroup(
                val name: List<Meta>,
                val color: Color?,
                val tasks: List<Activity<*>>,
            ) {
                data class Meta(
                    val iconVariations: List<String>,
                    val title: String,
                    val text: String?,
                ) {
                    constructor(
                        title: String,
                        vararg iconVariations: String,
                        text: String? = null,
                    ) : this(iconVariations.asList(), title = title, text = text)
                }
            }

            sealed interface Activity<ID : Identifier<*>> {
                val id: ID?
                val taskID: Task.ID?
                val name: String
                val color: Color?
                val url: Url?
                val meta: List<Meta>
                val descriptions: Map<String, String?>
                val tags: Set<Tag>

                data class RunningTaskActivity(
                    val timeEntry: TimeEntry,
                    val task: Task? = null,
                ) : Activity<TimeEntry.ID> {
                    private val taskActivity: TaskActivity? = task?.let(::TaskActivity)
                    override val id: TimeEntry.ID get() = timeEntry.id
                    override val taskID: Task.ID? get() = task?.id
                    override val name: String get() = timeEntry.task?.name ?: taskActivity?.name ?: "Timer running"
                    override val color: Color? get() = timeEntry.task?.status?.color ?: taskActivity?.color
                    override val url: Url? get() = timeEntry.url ?: timeEntry.taskUrl ?: taskActivity?.url
                    override val meta: List<Meta>
                        get() = buildList {
                            if (timeEntry.billable) add(Meta("billable", "dollar"))
                            taskActivity?.also { addAll(it.meta) }
                        }
                    override val descriptions: Map<String, String?>
                        get() = buildMap {
                            put("Timer", timeEntry.description.takeUnless { it.isBlank() })
                            taskActivity?.also { putAll(it.descriptions) }
                        }
                    override val tags: Set<Tag>
                        get() = buildSet {
                            addAll(timeEntry.tags)
                            taskActivity?.also { addAll(it.tags) }
                        }
                }

                data class TaskActivity(
                    val task: Task,
                ) : Activity<Task.ID> {
                    override val id: Task.ID get() = task.id
                    override val taskID: Task.ID get() = task.id
                    override val name: String get() = task.name
                    override val color: Color get() = task.status.color
                    override val url: Url? get() = task.url
                    override val meta: List<Meta>
                        get() = buildList {
                            if (task.dateCreated != null) {
                                add(Meta("created", "calendar", "alternate", "outline", text = task.dateCreated.toMoment()))
                            }
                            if (task.timeEstimate != null) {
                                add(Meta("estimated time", "hourglass", "outline", text = task.timeEstimate.toMoment(false)))
                            }
                            if (task.timeSpent != null) {
                                when (task.timeEstimate?.compareTo(task.timeSpent)) {
                                    -1 -> add(Meta("spent time (critical)", "red", "stopwatch", text = task.timeSpent.toMoment(false)))
                                    else -> add(Meta("spent time", "stopwatch", text = task.timeSpent.toMoment(false)))
                                }
                            }
                            when (task.dueDate?.compareTo(Now)) {
                                -1 -> add(Meta("due", "red", "calendar", "times", "outline", text = task.dueDate.toMoment(false)))
                                +1 -> add(Meta("due", "calendar", "outline", text = task.dueDate.toMoment(false)))
                                0 -> add(Meta("due", "yellow", "calendar", "outline", text = task.dueDate.toMoment(false)))
                                else -> {}
                            }
                        }
                    override val descriptions: Map<String, String?> get() = mapOf("Task" to task.description?.takeUnless { it.isBlank() })
                    override val tags: Set<Tag> get() = task.tags.toSet()
                }
            }

            val runningActivity: RunningTaskActivity? by lazy {
                val runningTimeEntry: TimeEntry? = runningTimeEntry?.orNull()
                val tasks: List<Task> = tasks?.orNull() ?: emptyList()
                runningTimeEntry?.let { timeEntry ->
                    RunningTaskActivity(timeEntry, timeEntry.task?.run { tasks.firstOrNull { it.id == id } })
                }
            }

            fun activity(id: String) = activities.firstNotNullOf { (_, _, activities) ->
                activities.firstOrNull { it.id?.stringValue == id }
            }

            val activities: List<ActivityGroup> by lazy {

                buildList {
                    runningActivity?.also {
                        add(ActivityGroup(
                            listOf(Meta("running timer", "stop", "circle", text = "Running")),
                            Brand.colors.red,
                            listOf(it),
                        ))
                    }

                    val tasks: List<Task> = tasks?.orNull() ?: emptyList()
                    val spaces = spaces?.orNull() ?: emptyList()

                    tasks.groupBy { it.space.id }
                        .forEach { (spaceID, spaceTasks) ->
                            val spaceMeta = Meta("project", "clone", text = spaces.firstOrNull { it.id == spaceID }?.name ?: "[no space name]")
                            spaceTasks.groupBy { it.folder }
                                .forEach { (folderPreview, folderTasks) ->
                                    val folderMeta = if (folderPreview.hidden) null else Meta("folder", "folder", text = folderPreview.name)
                                    folderTasks.groupBy { it.list }
                                        .forEach { (listPreview, listTasks) ->
                                            val lists = when {
                                                folderPreview.hidden -> spaceLists[spaceID]
                                                else -> folderLists[folderPreview.id]
                                            }?.orNull() ?: emptyList()

                                            val (listName, listColor) = lists
                                                .firstOrNull { it.id == listPreview?.id }
                                                ?.run { name to status?.color }
                                                ?: ((listPreview?.name ?: "[no list]") to null)

                                            val listMeta = Meta("list", "list", text = listName)
                                            add(ActivityGroup(
                                                listOfNotNull(spaceMeta, folderMeta, listMeta),
                                                listColor,
                                                listTasks.map { TaskActivity(it) },
                                            ))
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
                teamsResponse.map { Activating(user, it) } or { Failed(it) }
            } or { ex ->
                userResponse.map { Failed(ex) } or { Failed(ex, it) }
            }
    }
}
