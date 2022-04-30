package com.bkahlert.hello.plugins.clickup

import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Failed
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected.Data.CoreData
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected.Data.FullData
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelecting
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioning
import com.bkahlert.hello.plugins.clickup.menu.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.menu.ActivityGroup
import com.bkahlert.kommons.asString
import com.bkahlert.kommons.text.toSentenceCaseString
import com.clickup.api.Folder
import com.clickup.api.FolderID
import com.clickup.api.Identifier
import com.clickup.api.Space
import com.clickup.api.SpaceID
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.TaskID
import com.clickup.api.TaskList
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.User
import com.clickup.api.rest.ClickUpClient
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

                    suspend fun startTimeEntry(taskID: TaskID?, description: String?, billable: Boolean, tags: List<Tag>): TeamSelected {
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

                    private val selectable: List<Identifier<*>>
                        get() = listOfNotNull(
                            data.runningTimeEntry?.id,
                            data.runningTimeEntry?.task?.id,
                            *data.tasks.map { it.id }.toTypedArray(),
                        )

                    private val effectivelySelected: Selection
                        get() = selectable.filter { selected.contains(it) }.takeIf { it.isNotEmpty() }
                            ?: data.runningTimeEntry?.let { listOf(it.id) }
                            ?: data.tasks.firstOrNull()?.let { listOf(it.id) }
                            ?: emptyList()

                    private val Data.runningActivity: RunningTaskActivity?
                        get() {
                            val runningTimeEntry = runningTimeEntry ?: return null
                            val runningTask = runningTimeEntry.task?.run { tasks.firstOrNull { task -> task.id == id } }
                            return RunningTaskActivity(
                                timeEntry = runningTimeEntry,
                                selected = listOfNotNull(runningTimeEntry.id, runningTask?.id).any { effectivelySelected.contains(it) },
                                task = runningTask
                            )
                        }

                    /** The eventually running activity. */
                    val runningActivity: RunningTaskActivity? = data.runningActivity

                    /** The available tasks. */
                    val activityGroups: List<ActivityGroup> = buildList {
                        val effectivelySelected = effectivelySelected

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
                                                        selected = effectivelySelected,
                                                    )
                                                } ?: ActivityGroup.of(
                                                    space = space,
                                                    folder = folderPreview,
                                                    listPreview = listPreview,
                                                    color = null,
                                                    tasks = listTasks,
                                                    selected = effectivelySelected,
                                                )
                                                add(group)
                                            }
                                    }
                            }
                    }

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
                            //                            override fun toString(): String = asString {
//                                ::runningTimeEntry.name to runningTimeEntry
//                                ::tasks.name to tasks.size
//                                ::spaces.name to spaces.size
//                            }
                            override fun toString(): String = asString()

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
                            //                            override fun toString(): String = asString {
//                                ::runningTimeEntry.name to runningTimeEntry
//                                ::tasks.name to tasks.size
//                                ::spaces.name to spaces.size
//                                ::folders.name to folders.size
//                                ::spaceLists.name to spaceLists.size
//                                ::folderLists.name to folderLists.size
//                            }
                            override fun toString(): String = asString()

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
            val message: String = cause.message ?: cause::class.simpleName?.toSentenceCaseString() ?: "unknown"
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
