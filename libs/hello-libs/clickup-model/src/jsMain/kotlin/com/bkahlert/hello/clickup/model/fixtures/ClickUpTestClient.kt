package com.bkahlert.hello.clickup.model.fixtures

import com.bkahlert.hello.clickup.model.ClickUpClient
import com.bkahlert.hello.clickup.model.ClickUpException
import com.bkahlert.hello.clickup.model.CustomFieldFilter
import com.bkahlert.hello.clickup.model.Folder
import com.bkahlert.hello.clickup.model.FolderID
import com.bkahlert.hello.clickup.model.Space
import com.bkahlert.hello.clickup.model.SpaceID
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.Task
import com.bkahlert.hello.clickup.model.TaskID
import com.bkahlert.hello.clickup.model.TaskList
import com.bkahlert.hello.clickup.model.TaskListID
import com.bkahlert.hello.clickup.model.TaskPreview
import com.bkahlert.hello.clickup.model.Team
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.TimeEntryID
import com.bkahlert.hello.clickup.model.User
import com.bkahlert.hello.clickup.model.asCreator
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures.running
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.uri.Uri
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.times
import kotlin.require as kotlinRequire
import kotlin.requireNotNull as kotlinRequireNotNull

public open class ClickUpTestClient(
    public val initialUser: User = ClickUpFixtures.User,
    public val initialTeams: List<Team> = ClickUpFixtures.Teams,
    public val initialTasks: List<Task> = ClickUpFixtures.Tasks,
    public val initialSpaces: List<Space> = ClickUpFixtures.Spaces,
    public val initialLists: List<TaskList> = listOf(
        ClickUpFixtures.Space1FolderlessLists,
        ClickUpFixtures.Space2FolderlessLists,
        ClickUpFixtures.Space1FolderLists,
        ClickUpFixtures.Space2FolderLists,
    ).flatten(),
    public val initialFolders: List<Folder> = listOf(
        ClickUpFixtures.Space1Folders,
        ClickUpFixtures.Space2Folders,
    ).flatten(),
    initialTimeEntries: List<TimeEntry> = emptyList(),
    public val initialRunningTimeEntry: TimeEntry? = ClickUpFixtures.TimeEntry.running(),
    public val slowDown: Double = .05,
) : ClickUpClient {

    private var user: User = initialUser
    private var teams: List<Team> = initialTeams
    private var tasks: List<Task> = initialTasks
    private var spaces: List<Space> = initialSpaces
    private var lists: List<TaskList> = initialLists
    private var folders: List<Folder> = initialFolders
    private var timeEntries: List<TimeEntry> = initialTimeEntries
    private var runningTimeEntry: TimeEntry? = initialRunningTimeEntry
    private suspend fun adaptedDelay(duration: Duration) = delay(duration * slowDown)

    override suspend fun getUser(): User {
        adaptedDelay(DEMO_BASE_DELAY)
        return user
    }

    override suspend fun getTeams(): List<Team> {
        adaptedDelay(1.5 * DEMO_BASE_DELAY)
        return teams
    }

    override suspend fun createTask(listId: TaskListID, name: String): Task {
        require(name.isNotEmpty()) { "invalid task name" }
        adaptedDelay(.3 * DEMO_BASE_DELAY)
        val list = requireNotNull(lists.firstOrNull { it.id == listId }) {
            "unknown list $listId"
        }
        val folder = requireNotNull(list.folder?.run { folders.firstOrNull { it.id == id } }) {
            "no folder found for $list"
        }
        val space = requireNotNull(list.space) { "unknown space for $list" }
        val taskId = TaskID(randomString(7))
        adaptedDelay(0.6 * DEMO_BASE_DELAY)
        return Task(
            id = taskId,
            customId = null,
            name = name,
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
            url = Uri.parse("https://app.clickup.com/t/$taskId"),
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
        due_date_gt: Instant?,
        due_date_lt: Instant?,
        date_created_gt: Instant?,
        date_created_lt: Instant?,
        date_updated_gt: Instant?,
        date_updated_lt: Instant?,
        custom_fields: List<CustomFieldFilter>?,
    ): List<Task> {
        adaptedDelay(2 * DEMO_BASE_DELAY)
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
        adaptedDelay(0.6 * DEMO_BASE_DELAY)
        return tasks.firstOrNull { it.id == taskId }
    }

    override suspend fun updateTask(task: Task): Task {
        adaptedDelay(0.6 * DEMO_BASE_DELAY)
        tasks = tasks.map { it.takeUnless { it.id == task.id } ?: task }
        return task
    }

    override suspend fun getSpaces(team: Team, archived: Boolean): List<Space> {
        adaptedDelay(0.3 * DEMO_BASE_DELAY)
        return spaces
    }

    override suspend fun getLists(space: Space, archived: Boolean): List<TaskList> {
        adaptedDelay(0.3 * DEMO_BASE_DELAY)
        return lists.filterBy(space)
    }

    override suspend fun getLists(folder: Folder, archived: Boolean): List<TaskList> {
        adaptedDelay(0.3 * DEMO_BASE_DELAY)
        return lists.filterBy(folder)
    }

    override suspend fun getFolders(space: Space, archived: Boolean): List<Folder> {
        adaptedDelay(0.3 * DEMO_BASE_DELAY)
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
        adaptedDelay(0.3 * DEMO_BASE_DELAY)
        return timeEntries.firstOrNull { it.wid == team.id && it.id == timeEntryID }
    }

    override suspend fun getRunningTimeEntry(team: Team, assignee: User?): TimeEntry? {
        adaptedDelay(0.3 * DEMO_BASE_DELAY)
        return runningTimeEntry
    }

    override suspend fun startTimeEntry(team: Team, taskId: TaskID?, description: String?, billable: Boolean, vararg tags: Tag): TimeEntry {
        adaptedDelay(0.6 * DEMO_BASE_DELAY)
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
        adaptedDelay(0.6 * DEMO_BASE_DELAY)
        return runningTimeEntry?.also {
            val timeEntry = it.copy(end = Now)
            timeEntries = timeEntries + timeEntry
            runningTimeEntry = null
        }
    }

    override suspend fun addTagsToTimeEntries(team: Team, timeEntryIDs: List<TimeEntryID>, tags: List<Tag>) {
        adaptedDelay(10.3 * DEMO_BASE_DELAY)
        timeEntries = timeEntries.map {
            it.takeUnless { it.wid == team.id && timeEntryIDs.contains(it.id) } ?: it.copy(tags = buildSet { addAll(it.tags);addAll(tags) }.toList())
        }
    }

    /**
     * The total time spent based on what is already tracked as part of this task itself
     * and matching time entries.
     */
    public val Task.totalTimeSpent: Duration?
        get() = timeEntries.fold(timeSpent ?: Duration.ZERO) { acc, timeEntry ->
            if (timeEntry.task?.id == id) acc + (timeEntry.duration ?: Duration.ZERO)
            else acc
        }.takeUnless { it == Duration.ZERO }

    public companion object {
        public inline fun require(value: Boolean, lazyMessage: () -> Any): Unit {
            kotlin.runCatching { kotlinRequire(value, lazyMessage) }.getOrElse {
                throw ClickUpException("Task name invalid", "INPUT_005", it)
            }
        }

        public inline fun <T : Any> requireNotNull(value: T?, lazyMessage: () -> Any): T {
            return kotlin.runCatching { kotlinRequireNotNull(value, lazyMessage) }.getOrElse {
                throw ClickUpException("error", "CODE-123", it)
            }
        }

        public fun Iterable<TaskList>.filterBy(space: Space): List<TaskList> = filter { it.space?.id == space.id }
        public fun Iterable<TaskList>.filterBy(folder: Folder): List<TaskList> = filter { it.folder?.id == folder.id }
        public fun Iterable<Folder>.filterBy(space: Space): List<Folder> = filter { folder -> folder.lists.any { it.space?.id == space.id } }
    }
}
