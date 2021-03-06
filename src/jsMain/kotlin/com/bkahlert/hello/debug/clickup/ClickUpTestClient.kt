package com.bkahlert.hello.debug.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.hello.clickup.api.Folder
import com.bkahlert.hello.clickup.api.FolderID
import com.bkahlert.hello.clickup.api.Identifier
import com.bkahlert.hello.clickup.api.Space
import com.bkahlert.hello.clickup.api.SpaceID
import com.bkahlert.hello.clickup.api.Tag
import com.bkahlert.hello.clickup.api.Task
import com.bkahlert.hello.clickup.api.TaskID
import com.bkahlert.hello.clickup.api.TaskList
import com.bkahlert.hello.clickup.api.TaskListID
import com.bkahlert.hello.clickup.api.TaskPreview
import com.bkahlert.hello.clickup.api.Team
import com.bkahlert.hello.clickup.api.TimeEntry
import com.bkahlert.hello.clickup.api.TimeEntryID
import com.bkahlert.hello.clickup.api.User
import com.bkahlert.hello.clickup.api.asCreator
import com.bkahlert.hello.clickup.api.rest.ClickUpClient
import com.bkahlert.hello.clickup.api.rest.ClickUpException
import com.bkahlert.hello.clickup.api.rest.CreateTaskRequest
import com.bkahlert.hello.clickup.api.rest.CustomFieldFilter
import com.bkahlert.hello.clickup.api.rest.ErrorInfo
import com.bkahlert.hello.clickup.ui.ClickUpMenuState
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected.Data.CoreData
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected.Data.FullData
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelecting
import com.bkahlert.hello.clickup.ui.ClickUpMenuViewModel
import com.bkahlert.hello.clickup.ui.rememberClickUpMenuViewModel
import com.bkahlert.hello.debug.clickup.ClickUpFixtures.running
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.URL
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.compareTo
import com.bkahlert.kommons.time.seconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlin.js.Date
import kotlin.time.Duration
import kotlin.require as kotlinRequire
import kotlin.requireNotNull as kotlinRequireNotNull

open class ClickUpTestClient(
    private val initialUser: User = ClickUpFixtures.User,
    private val initialTeams: List<Team> = ClickUpFixtures.Teams,
    private val initialTasks: List<Task> = ClickUpFixtures.Tasks,
    private val initialSpaces: List<Space> = ClickUpFixtures.Spaces,
    private val initialLists: List<TaskList> = listOf(
        ClickUpFixtures.Space1FolderlessLists,
        ClickUpFixtures.Space2FolderlessLists,
        ClickUpFixtures.Space1FolderLists,
        ClickUpFixtures.Space2FolderLists,
    ).flatten(),
    private val initialFolders: List<Folder> = listOf(
        ClickUpFixtures.Space1Folders,
        ClickUpFixtures.Space2Folders,
    ).flatten(),
    initialTimeEntries: List<TimeEntry> = emptyList(),
    private val initialRunningTimeEntry: TimeEntry? = ClickUpFixtures.TimeEntry.running(),
    private val delayFactor: Double = .3,
) : ClickUpClient {

    fun toTeamSelecting(
        user: User = initialUser,
        teams: List<Team> = initialTeams,
    ): TeamSelecting = TeamSelecting(
        client = this,
        user = user,
        teams = teams,
    )

    fun toPartiallyLoaded(
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

    fun toFullyLoaded(
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

    private var user: User = initialUser
    private var teams: List<Team> = initialTeams
    private var tasks: List<Task> = initialTasks
    private var spaces: List<Space> = initialSpaces
    private var lists: List<TaskList> = initialLists
    private var folders: List<Folder> = initialFolders
    private var timeEntries: List<TimeEntry> = initialTimeEntries
    private var runningTimeEntry: TimeEntry? = initialRunningTimeEntry
    private suspend fun adaptedDelay(duration: Duration) = delay(duration * delayFactor)

    override suspend fun getUser(): User {
        adaptedDelay(1.5.seconds)
        return user
    }

    override suspend fun getTeams(): List<Team> {
        adaptedDelay(2.5.seconds)
        return teams
    }

    override suspend fun createTask(listId: TaskListID, task: CreateTaskRequest): Task {
        require(task.name.isNotEmpty()) { "invalid task name" }
        adaptedDelay(.5.seconds)
        val list = requireNotNull(lists.firstOrNull { it.id == listId }) {
            "unknown list $listId"
        }
        val folder = requireNotNull(list.folder?.run { folders.firstOrNull { it.id == id } }) {
            "no folder found for $list"
        }
        val space = requireNotNull(list.space) { "unknown space for $list" }
        val taskId = TaskID(randomString(7))
        adaptedDelay(1.seconds)
        return Task(
            id = taskId,
            customId = null,
            name = task.name,
            textContent = null,
            description = null,
            status = folder.statuses.first().asPreview(),
            orderIndex = tasks.size.toDouble(),
            dateCreated = Now,
            dateUpdated = Now,
            dateClosed = null,
            creator = user.asCreator(),
            assignees = emptyList(),
            watchers = emptyList(),
            checklists = emptyList(),
            tags = emptyList(),
            parent = null,
            priority = null,
            dueDate = null,
            startDate = null,
            points = null,
            timeEstimate = null,
            timeSpent = Duration.ZERO,
            customFields = emptyList(),
            dependencies = emptyList(),
            linkedTasks = emptyList(),
            teamId = teams.first().id,
            url = URL.parse("https://app.clickup.com/t/$taskId"),
            permissionLevel = "create",
            list = list.asPreview(),
            folder = folder.asPreview(),
            space = space,
        ).also { tasks = tasks + it }
    }

    override suspend fun getTasks(
        team: Team,
        page: Int?,
        order_by: String?,
        reverse: Boolean?,
        subtasks: Boolean?,
        space_ids: List<SpaceID>?,
        project_ids: List<FolderID>?,
        list_ids: List<TaskListID>?,
        statuses: List<String>?,
        include_closed: Boolean?,
        assignees: List<String>?,
        tags: List<String>?,
        due_date_gt: Date?,
        due_date_lt: Date?,
        date_created_gt: Date?,
        date_created_lt: Date?,
        date_updated_gt: Date?,
        date_updated_lt: Date?,
        custom_fields: List<CustomFieldFilter>?,
    ): List<Task> {
        adaptedDelay(3.seconds)
        return tasks.asSequence()
            .filter { space_ids == null || space_ids.contains(it.space.id) }
            .filter { project_ids == null || project_ids.contains(it.folder.id) }
            .filter { list_ids == null || list_ids.contains(it.list?.id) }
            .filter { statuses == null || statuses.contains(it.status.status) }
            .filter { tags == null || tags.any { tag -> it.tags.map { it.name }.contains(tag) } }
            .filter { due_date_gt == null || it.dueDate?.let { dueDate -> due_date_gt > dueDate } ?: false }
            .filter { due_date_lt == null || it.dueDate?.let { dueDate -> due_date_lt < dueDate } ?: false }
            .filter { date_created_gt == null || it.dateCreated?.let { dateCreated -> date_created_gt > dateCreated } ?: false }
            .filter { date_created_lt == null || it.dateCreated?.let { dateCreated -> date_created_lt < dateCreated } ?: false }
            .filter { date_updated_gt == null || it.dateUpdated?.let { dateUpdated -> date_updated_gt > dateUpdated } ?: false }
            .filter { date_updated_lt == null || it.dateUpdated?.let { dateUpdated -> date_updated_lt < dateUpdated } ?: false }
            .map { it.copy(timeSpent = it.totalTimeSpent) }
            .toList()
    }

    override suspend fun getTask(taskId: TaskID): Task? {
        adaptedDelay(1.seconds)
        return tasks.firstOrNull { it.id == taskId }
    }

    override suspend fun updateTask(task: Task): Task {
        adaptedDelay(1.seconds)
        tasks = tasks.map { it.takeUnless { it.id == task.id } ?: task }
        return task
    }

    override suspend fun getSpaces(team: Team, archived: Boolean): List<Space> {
        adaptedDelay(.5.seconds)
        return spaces
    }

    override suspend fun getLists(space: Space, archived: Boolean): List<TaskList> {
        adaptedDelay(.5.seconds)
        return lists.filterBy(space)
    }

    override suspend fun getLists(folder: Folder, archived: Boolean): List<TaskList> {
        adaptedDelay(.5.seconds)
        return lists.filterBy(folder)
    }

    override suspend fun getFolders(space: Space, archived: Boolean): List<Folder> {
        adaptedDelay(.5.seconds)
        return folders.filterBy(space)
    }

    override suspend fun getFolder(folderId: FolderID): Folder {
        return folders.firstOrNull { it.id == folderId } ?: Folder(
            id = folderId,
            name = "hidden folder",
            orderIndex = 0,
            overrideStatuses = false,
            hidden = true,
            taskCount = "0",
            archived = false,
            statuses = initialSpaces.firstOrNull()?.statuses ?: initialFolders.firstOrNull()?.statuses ?: emptyList(),
            lists = emptyList(),
            permissionLevel = "create",
        )
    }

    override suspend fun getTimeEntry(team: Team, timeEntryID: TimeEntryID): TimeEntry? {
        adaptedDelay(.5.seconds)
        return timeEntries.firstOrNull { it.wid == team.id && it.id == timeEntryID }
    }

    override suspend fun getRunningTimeEntry(team: Team, assignee: User?): TimeEntry? {
        adaptedDelay(.5.seconds)
        return runningTimeEntry
    }

    override suspend fun startTimeEntry(team: Team, taskId: TaskID?, description: String?, billable: Boolean, vararg tags: Tag): TimeEntry {
        adaptedDelay(1.seconds)
        stopTimeEntry(team)
        val timeEntry = taskId?.let {
            checkNotNull(getTask(taskId = it)).let { task ->
                TimeEntry(
                    id = TimeEntryID(id = randomString()),
                    task = TaskPreview(taskId, task.name, task.status, null),
                    wid = team.id,
                    user = user,
                    billable = billable,
                    start = Now,
                    end = null,
                    description = description ?: "",
                    tags = tags.toList(),
                    source = "hello",
                    taskUrl = null,
                )
            }
        } ?: TimeEntry(
            id = TimeEntryID(id = randomString()),
            task = null,
            wid = team.id,
            user = user,
            billable = billable,
            start = Now,
            end = null,
            description = description ?: "",
            tags = tags.toList(),
            source = "hello",
            taskUrl = null,
        )
        runningTimeEntry = timeEntry
        return timeEntry
    }

    override suspend fun stopTimeEntry(team: Team): TimeEntry? {
        adaptedDelay(1.seconds)
        return runningTimeEntry?.also {
            val timeEntry = it.copy(end = Now)
            timeEntries = timeEntries + timeEntry
            runningTimeEntry = null
        }
    }

    override suspend fun addTagsToTimeEntries(team: Team, timeEntryIDs: List<TimeEntryID>, tags: List<Tag>) {
        adaptedDelay(1.5.seconds)
        timeEntries = timeEntries.map {
            it.takeUnless { it.wid == team.id && timeEntryIDs.contains(it.id) } ?: it.copy(tags = buildSet { addAll(it.tags);addAll(tags) }.toList())
        }
    }

    /**
     * The total time spent based on what is already tracked as part of this task itself
     * and matching time entries.
     */
    val Task.totalTimeSpent: Duration?
        get() = timeEntries.fold(timeSpent ?: Duration.ZERO) { acc, timeEntry ->
            if (timeEntry.task?.id == id) acc + (timeEntry.duration ?: Duration.ZERO)
            else acc
        }.takeUnless { it == Duration.ZERO }

    companion object {
        inline fun require(value: Boolean, lazyMessage: () -> Any): Unit {
            kotlin.runCatching { kotlinRequire(value, lazyMessage) }.getOrElse {
                throw ClickUpException(ErrorInfo("Task name invalid", "INPUT_005"), it)
            }
        }

        inline fun <T : Any> requireNotNull(value: T?, lazyMessage: () -> Any): T {
            return kotlin.runCatching { kotlinRequireNotNull(value, lazyMessage) }.getOrElse {
                throw ClickUpException(ErrorInfo("error", "CODE-123"), it)
            }
        }

        fun Iterable<TaskList>.filterBy(space: Space) = filter { it.space?.id == space.id }
        fun Iterable<TaskList>.filterBy(folder: Folder) = filter { it.folder?.id == folder.id }
        fun Iterable<Folder>.filterBy(space: Space) = filter { folder -> folder.lists.any { it.space?.id == space.id } }
    }
}

/**
 * Returns a remembered [ClickUpMenuViewModel] for the purpose of testing with the
 * optionally specified [dispatcher], [refreshCoroutineScope], [storage]
 * and the [initialState] derived from the optionally specified [testClient].
 */
@Composable
fun rememberClickUpMenuTestViewModel(
    testClient: ClickUpTestClient = ClickUpTestClient(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    refreshCoroutineScope: CoroutineScope = rememberCoroutineScope(),
    storage: Storage = InMemoryStorage(),
    initialState: ClickUpTestClient.() -> ClickUpMenuState,
): ClickUpMenuViewModel =
    rememberClickUpMenuViewModel(
        initialState = testClient.initialState(),
        dispatcher = dispatcher,
        refreshCoroutineScope = refreshCoroutineScope,
        storage = storage,
        createClient = { _, _, _ -> testClient },
    )
