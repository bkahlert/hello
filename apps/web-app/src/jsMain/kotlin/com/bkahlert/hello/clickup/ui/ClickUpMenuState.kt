package com.bkahlert.hello.clickup.ui

import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.clickup.Pomodoro
import com.bkahlert.hello.clickup.client.ClickUpClient
import com.bkahlert.hello.clickup.client.CreateTaskRequest
import com.bkahlert.hello.clickup.model.Folder
import com.bkahlert.hello.clickup.model.FolderID
import com.bkahlert.hello.clickup.model.Space
import com.bkahlert.hello.clickup.model.SpaceID
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.Task
import com.bkahlert.hello.clickup.model.TaskID
import com.bkahlert.hello.clickup.model.TaskList
import com.bkahlert.hello.clickup.model.TaskListID
import com.bkahlert.hello.clickup.model.Team
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.User
import com.bkahlert.hello.clickup.model.closed
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Failed
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected.Data.CoreData
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected.Data.FullData
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelecting
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioning
import com.bkahlert.hello.clickup.ui.widgets.Activity
import com.bkahlert.hello.clickup.ui.widgets.Activity.RunningTaskActivity
import com.bkahlert.hello.clickup.ui.widgets.ActivityGroup
import com.bkahlert.hello.clickup.ui.widgets.byIdOrNull
import com.bkahlert.hello.ui.search.next
import com.bkahlert.kommons.text.simpleKebabCasedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/** State of a [ClickUpMenu]. */
sealed class ClickUpMenuState {

    class Transitioning(
        /** The state that is transitioned from. */
        val previousState: Succeeded,
    ) : ClickUpMenuState()

    sealed class Transitioned : ClickUpMenuState() {
        sealed class Succeeded : Transitioned() {
            /** Connects to ClickUp using the specified [client]. */
            suspend fun connect(client: ClickUpClient): TeamSelecting = withContext(Dispatchers.Default) {
                val deferredUser = async { client.getUser() }
                val deferredTeams = async { client.getTeams() }
                TeamSelecting(client, deferredUser.await(), deferredTeams.await())
            }

            /** Menu is not doing anything in order to save resources. */
            object Disabled : Succeeded()

            /** Menu is not connected to ClickUp. */
            object Disconnected : Succeeded()

            /** Connection to ClickUp is established. */
            sealed class Connected(
                /** The client used to connect. */
                protected open val client: ClickUpClient,
                /** The authorized user */
                open val user: User,

                /** The teams the authorized user has access to */
                open val teams: List<Team>,
            ) : Succeeded() {

                protected val logger = simpleLogger()

                /** Selects the specified [team] and restores the specified [selection]. */
                suspend fun select(team: Team, selection: Selection): TeamSelected =
                    TeamSelected(client, user, teams, team, selection, CoreData.load(user, team, client))

                suspend fun getRunningTimeEntry(team: Team): TimeEntry? = client.getRunningTimeEntry(team, user)

                /** Refreshes currently loaded data. */
                abstract suspend fun refresh(): Connected

                /** A [Team] to work with needs to be selected. */
                data class TeamSelecting(
                    override val client: ClickUpClient,
                    override val user: User,
                    override val teams: List<Team>,
                ) : Connected(client, user, teams) {

                    override suspend fun refresh(): TeamSelecting = withContext(Dispatchers.Default) {
                        val deferredUser = async { client.getUser() }
                        val deferredTeams = async { client.getTeams() }
                        TeamSelecting(client, deferredUser.await(), deferredTeams.await())
                    }
                }

                /** A [Team] to work with is selected and its [TeamSelected.Data] are loaded. */
                data class TeamSelected(
                    override val client: ClickUpClient,
                    override val user: User,
                    override val teams: List<Team>,

                    /** The team this session is related to. */
                    val selectedTeam: Team,

                    /** The items selected by the user. */
                    val selected: Selection,

                    /** The data related to the [selectedTeam]. */
                    val data: Data,
                ) : Connected(client, user, teams) {

                    fun select(selected: Selection): TeamSelected = copy(selected = selected)

                    suspend fun createTask(taskListID: TaskListID, name: String): TeamSelected {
                        val task = client.createTask(taskListID, CreateTaskRequest(name))
                        return refresh().select(listOf(task.id) + selected)
                    }

                    suspend fun closeTask(taskID: TaskID): TeamSelected {
                        val task = data.tasks.first { it.id == taskID }
                        client.updateTask(task.copy(status = client.getPossibleStatuses(task).closed.asPreview()))
                        return refresh().select(selected + data.tasks.next { it.id == taskID }.map { it.id })
                    }

                    suspend fun startTimeEntry(taskID: TaskID?, description: String?, billable: Boolean, tags: List<Tag>): TeamSelected {
                        runningActivity?.also { stopTimeEntry(it.timeEntry, listOf(Pomodoro.Status.Aborted.tag)) }
                        client.startTimeEntry(selectedTeam, taskID, description, billable, *tags.toTypedArray())
                        return refresh()
                    }

                    suspend fun stopTimeEntry(timeEntry: TimeEntry, tags: List<Tag>): TeamSelected {
                        val stoppedTimeEntry = client.stopTimeEntry(selectedTeam)
                        val stoppedTimeEntryID = if (stoppedTimeEntry != null) {
                            logger.debug("stopped $stoppedTimeEntry")
                            stoppedTimeEntry.id
                        } else {
                            logger.debug("time entry $timeEntry was already stopped")
                            timeEntry.id
                        }

                        client.addTagsToTimeEntries(selectedTeam, listOf(stoppedTimeEntryID), tags)
                        logger.debug("added tags $tags to time entry $stoppedTimeEntryID")

                        return refresh().select(listOfNotNull(timeEntry.id, timeEntry.task?.id))
                    }

                    override suspend fun refresh(): TeamSelected =
                        copy(data = FullData.load(user, selectedTeam, client))

                    private val Data.runningActivity: RunningTaskActivity?
                        get() {
                            val runningTimeEntry = runningTimeEntry ?: return null
                            val runningTask = runningTimeEntry.task?.run { tasks.firstOrNull { task -> task.id == id } }
                            return RunningTaskActivity(timeEntry = runningTimeEntry, task = runningTask)
                        }

                    /** The eventually running activity. */
                    val runningActivity: RunningTaskActivity? = data.runningActivity

                    /** The available tasks. */
                    val activityGroups: List<ActivityGroup> = buildList {
                        data.runningActivity?.also { add(ActivityGroup.of(it)) }

                        val (_, tasks, spaces, _, spaceLists, folderLists) =
                            if (data is FullData) data else FullData(data.runningTimeEntry, data.tasks, data.spaces, emptyMap(), emptyMap(), emptyMap())

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
                                                } ?: emptyList()

                                                val group = lists.firstOrNull { it.id == listPreview?.id }?.let { taskList ->
                                                    ActivityGroup.of(
                                                        space = space,
                                                        folder = folderPreview,
                                                        list = taskList,
                                                        color = taskList.status?.color,
                                                        tasks = listTasks,
                                                    )
                                                } ?: ActivityGroup.of(
                                                    space = space,
                                                    folder = folderPreview,
                                                    listPreview = listPreview,
                                                    color = null,
                                                    tasks = listTasks,
                                                )
                                                add(group)
                                            }
                                    }
                            }
                    }

                    val selectedActivity: Activity<*>? = selected.firstNotNullOfOrNull { id ->
                        runningActivity.takeIf { id == it?.id || id == it?.task?.id } ?: activityGroups.byIdOrNull(id)
                    } ?: runningActivity

                    /** Data belonging to a [TeamSelected]. */
                    sealed class Data(
                        /** The currently running time entry if any. */
                        open val runningTimeEntry: TimeEntry?,

                        /** The tasks belonging to the selected team. */
                        open val tasks: List<Task>,

                        /** The spaces belonging to the selected team. */
                        open val spaces: List<Space>,
                    ) {

                        /** Core data belonging to a [TeamSelected]. */
                        data class CoreData(
                            override val runningTimeEntry: TimeEntry?,
                            override val tasks: List<Task>,
                            override val spaces: List<Space>,
                        ) : Data(runningTimeEntry, tasks, spaces) {
                            companion object {
                                suspend fun load(user: User, team: Team, client: ClickUpClient): CoreData = withContext(Dispatchers.Default) {
                                    val deferredRunningTimeEntry = async { client.getRunningTimeEntry(team, user) }
                                    val deferredTasks = async { client.getTasks(team) }
                                    val deferredSpaces = async { client.getSpaces(team) }
                                    CoreData(
                                        runningTimeEntry = deferredRunningTimeEntry.await(),
                                        tasks = deferredTasks.await(),
                                        spaces = deferredSpaces.await(),
                                    )
                                }
                            }
                        }

                        /** Core data with additional information on where tasks are actually located. */
                        data class FullData(
                            override val runningTimeEntry: TimeEntry?,
                            override val tasks: List<Task>,
                            override val spaces: List<Space>,
                            /** The folders selected team grouped by the space they belong to. */
                            val folders: Map<SpaceID, List<Folder>>,

                            /** The task lists and the spaces they belong to (a.k.a "folder-less lists"). */
                            val spaceLists: Map<SpaceID, List<TaskList>>,

                            /** The task lists and the folders they belong to. */
                            val folderLists: Map<FolderID, List<TaskList>>,
                        ) : Data(runningTimeEntry, tasks, spaces) {

                            companion object {
                                suspend fun load(coreData: CoreData, client: ClickUpClient): FullData = withContext(Dispatchers.Default) {
                                    val deferredFoldersAndFolderLists = coreData.spaces.map { space ->
                                        async {
                                            space.id to client.getFolders(space).map { folder -> async { folder to client.getLists(folder) } }
                                        }
                                    }
                                    val deferredSpaceLists = coreData.spaces.map { space ->
                                        async {
                                            space.id to client.getLists(space)
                                        }
                                    }

                                    val foldersAndFolderLists = deferredFoldersAndFolderLists.awaitAll()
                                        .map { (spaceId, deferredFoldersAndFolderListPairs) -> spaceId to deferredFoldersAndFolderListPairs.awaitAll() }
                                    val spaceLists = deferredSpaceLists.awaitAll()

                                    FullData(
                                        runningTimeEntry = coreData.runningTimeEntry,
                                        tasks = coreData.tasks,
                                        spaces = coreData.spaces,
                                        folders = foldersAndFolderLists.associate { (spaceId, list) -> spaceId to list.map { it.first } },
                                        spaceLists = spaceLists.toMap(),
                                        folderLists = foldersAndFolderLists
                                            .flatMap { (_, list) -> list.map { (folder, folderList) -> folder.id to folderList } }.toMap()
                                    )
                                }

                                suspend fun load(user: User, team: Team, client: ClickUpClient): FullData =
                                    load(CoreData.load(user, team, client), client)
                            }
                        }
                    }
                }
            }
        }

        /** Menu is presenting a failed operation. */
        data class Failed(
            /** The name of the failed operation. */
            val operation: String,
            /** The cause of this failed state. */
            val cause: Throwable,
            /** The state that was current when the operation failed. */
            val previousState: Succeeded,
            /** If invoked, the operation that failed will be ignored. */
            val ignore: () -> Unit,
            /** If invoked, the operation that failed will be triggered again. */
            val retry: () -> Unit,
        ) : Transitioned() {
            /** The not-`null` message describing the [cause]. */
            val message: String = cause.message ?: cause::class.simpleKebabCasedName?.replace('-', ' ') ?: "unknown"
        }
    }

    override fun toString(): String {
        return this::class.simpleName ?: "<object>"
    }
}

val ClickUpMenuState.lastSucceededState: Succeeded
    get() = when (this) {
        is Transitioning -> previousState
        is Succeeded -> this
        is Failed -> previousState
    }
